package data;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class GPSData {
	private List<CSVRecord> entries;
	private Map<String, Integer> headers;
	private static String HEADER_DATE = "LOCAL DATE";
	private static String HEADER_TIME = "LOCAL TIME";
	private static String HEADER_DATE_2 = "DATE";
	private static String HEADER_TIME_2 = "TIME";
	private static String HEADER_SPEED = "SPEED";
	private static String HEADER_SPEED_2 = "SPEED(km/h)";
	private Map<DateTime,List<Float>> sampledSpeeds = new HashMap<DateTime, List<Float>>();
	private Map<DateTime,List<Activity>> sampledActivities = new HashMap<DateTime, List<Activity>>();
	private Date minDate = null;
	private Date maxDate = null;
	
	//Configuration
	private static int samplingRateInSec = 300; //in seconds
	private static int startingTimeInHours = 8;
	private static int startingTimeInMin = startingTimeInHours * 60; // in minutes
	private static int endTimeInMin = 19*60; // in minutes
	
	public GPSData(String filename){
		Reader in;
		try {
			in = new FileReader(filename);
			CSVParser parser = CSVFormat.newFormat(',').parse(in);
			entries = parser.getRecords();
			//if parsing was not successful try with other delimiter
			if (entries.get(0).size()<2){
				in = new FileReader(filename);
				parser = CSVFormat.newFormat(';').parse(in);
				entries = parser.getRecords();
			}
			
			//headers = parser.getHeaderMap(); does not work somehow...
			
			CSVRecord e = entries.remove(0);
			//System.out.println("Removing "+e);
			storeHeaderMap(e);
			sample();
			convertToActivities();

            // Mapper for and to JSON
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("HH:mm");
            DateTimeFormatter df2 = DateTimeFormat.forPattern("yyyy-MM-dd");
            mapper.registerModule(new JodaModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setDateFormat(df);


            try {
                for (DateTime d: sampledActivities.keySet()) {
					//d.toString(df2)
                    String jsonFile = d.toString(df2) + ".json";
                    // Convert to JSON
                    mapper.writeValue(new File("output\\"+jsonFile), sampledActivities.get(d));

                    // Read from JSON
                    File f = new File("input\\"+jsonFile);
                    System.out.println("Trying to read " + f.getAbsolutePath());

                    if(f.exists() && !f.isDirectory()) {
                        System.out.println("Trying to parse "+ f.getAbsolutePath());
                        List<Activity> modifiedActivities = mapper.readValue(new FileInputStream(f), new TypeReference<List<Activity>>(){});
                        System.out.println("Overwriting values for " + jsonFile + " ("+modifiedActivities+")");
                        sampledActivities.put(d,modifiedActivities);
                    }


                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            System.out.println("Done");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void storeHeaderMap(CSVRecord e) {
		headers =  new HashMap<String, Integer>();
		Iterator<String> iterator = e.iterator();
		int i = 0;
		while(iterator.hasNext()){
			String s = iterator.next();
			headers.put(s,i);
			i++;
			
		}
	}

	private void sample(){
		Date lastDate = null;
		List<Double> speedWindow = new LinkedList<Double>();
		Date currentDay = null;
		List<Float> daySpeeds = new LinkedList<Float>();
		
		for (CSVRecord e : entries) {
			Date date = null;
			String dateString = "";
			try {
				if (headers.containsKey(HEADER_DATE) && headers.containsKey(HEADER_TIME)){
					dateString = e.get(headers.get(HEADER_DATE))+" "+ e.get(headers.get(HEADER_TIME));
					date = DateUtils.parseDate(dateString, "yyyy/MM/dd HH:mm:ss");
				}else if (headers.containsKey(HEADER_DATE_2) && headers.containsKey(HEADER_TIME_2)){
					dateString = e.get(headers.get(HEADER_DATE_2))+" "+
						e.get(headers.get(HEADER_TIME_2));
					
					date = DateUtils.parseDate(dateString+" UTC", "yyyy/MM/dd HH:mm:ss.SSS z");
				}else{
					System.err.println("Could not find headers "+HEADER_DATE+" and "+ HEADER_TIME+ " / "+HEADER_DATE_2+" and "+HEADER_TIME_2);
				}
				
				
			} catch (ParseException e1) {
				System.err.println("Could not parse "+dateString+": "+e1.getMessage()+" "+e);
				continue;
			}
		
			if (minDate==null || minDate.after(date)){
				minDate = date;
			}if (maxDate==null || maxDate.before(date)){
				maxDate = date;
			}
			long minOfDay = DateUtils.getFragmentInMinutes(date, Calendar.DATE);
			if (minOfDay<startingTimeInMin || minOfDay> endTimeInMin){
				//System.err.println("Ignoring "+date);
				continue;
			}
			
			if (lastDate==null || !DateUtils.isSameDay(lastDate,date)){
				//Fill previous day if available
				if (lastDate!=null){
					fillSampledDataFrom(lastDate, daySpeeds);
				}
				daySpeeds = new LinkedList<Float>();
				lastDate = getBeginningOfSamplingInterval(date);
				//fill current day until sampling interval 				
				currentDay = DateUtils.truncate(date, Calendar.DATE);
				//System.out.println("Current Day is "+currentDay);
				fillSampledDataUpTo(lastDate,daySpeeds);
				sampledSpeeds.put(new DateTime(currentDay), daySpeeds);
				 
			}
			//it is already next sampling interval
			else if (date.after(DateUtils.addSeconds(lastDate, samplingRateInSec))){
				Double[] tmp = speedWindow.toArray(new Double[speedWindow.size()]);
				double median = StatUtils.percentile(ArrayUtils.toPrimitive(tmp), 50);
				daySpeeds.add(new Float(median));
				//System.out.println(lastDate+": "+median);
				speedWindow = new LinkedList<Double>();
				fillSampledData(DateUtils.addSeconds(lastDate,  samplingRateInSec), getBeginningOfSamplingInterval(date), daySpeeds);
				lastDate = getBeginningOfSamplingInterval(date); 
			}
			if (headers.containsKey(HEADER_SPEED))
				speedWindow.add(Double.parseDouble(e.get(headers.get(HEADER_SPEED))));
			else if (headers.containsKey(HEADER_SPEED_2))
				speedWindow.add(Double.parseDouble(e.get(headers.get(HEADER_SPEED_2))));
			
		}
		fillSampledDataFrom(lastDate, daySpeeds);
		
	}

	private void convertToActivities(){
        for (DateTime date: sampledSpeeds.keySet()) {
            List<Float> speeds = sampledSpeeds.get(date);
            ActivityType activity = null;
            List<Activity> activities = new LinkedList<>();
            sampledActivities.put(date,activities);
			DateTime time = date.withHourOfDay(startingTimeInHours);
			activities.add(new Activity(new DateTime(time), ActivityType.IDLE));
            for (Float speed: speeds){
                ActivityType newActivity = ActivityType.getActivity(speed);
				if (activity!=null && newActivity!=activity ){
                    activities.add(new Activity(new DateTime(time), activity));
                }
                activity = newActivity;
                time = time.plusSeconds(samplingRateInSec);
            }
            activities.add(new Activity(new DateTime(time),activity));
        }

	}
	
	private Date getBeginningOfSamplingInterval(Date date){
		return new Date(date.getTime()-date.getTime()%(samplingRateInSec*1000));		
	}
	
	private void fillSampledData(Date from, Date to, List<Float> daySpeeds){
		if (from.equals(to)) return;
		for(Date tmp = from; tmp.before(to); tmp = DateUtils.addSeconds(tmp, samplingRateInSec)){
			//System.out.println("\t"+tmp+": "+0);
			daySpeeds.add(0.0f);
		}
	}
	
	private void fillSampledDataUpTo(Date to, List<Float> daySpeeds){
		Date startingDate = DateUtils.truncate(to, Calendar.DATE);
		startingDate = DateUtils.addMinutes(startingDate, startingTimeInMin);
		fillSampledData(startingDate, to, daySpeeds);
	}
	
	private void fillSampledDataFrom(Date from, List<Float> daySpeeds){
		Date endDate = DateUtils.truncate(from, Calendar.DATE);
		endDate = DateUtils.addMinutes(endDate, endTimeInMin);
		fillSampledData(from, endDate, daySpeeds);
	}
	

	private List<Float> extractSpeeds(Date day){
		DateTime d = new DateTime(day).withTimeAtStartOfDay();
		return sampledSpeeds.get(d);
	}

    public List<Activity> getDayActivities(Date day){
		DateTime d = new DateTime(day).withTimeAtStartOfDay();
        return sampledActivities.get(d);
    }


	public static int getSamplingRateInSec() {
		return samplingRateInSec;
	}

	public static long getStartingTimeInSec() {
		return startingTimeInMin*60;
	}

	public static long getEndTimeInSec() {
		return endTimeInMin*60;
	}
	
	public Date getMinDateInFile(){
		return minDate;
	}
	public Date getMaxDateInFile(){
		return maxDate;
	}

	
	}
