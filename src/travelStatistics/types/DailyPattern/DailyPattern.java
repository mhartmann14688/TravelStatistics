package travelStatistics.types.DailyPattern;

import java.io.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import data.Activity;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.commons.lang3.time.DateUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import data.Config;
import data.GPSData;

public class DailyPattern {
	private static DailyPattern dailyPattern;
	private File file;
	private Config config;
	private GPSData data;

	private DailyPattern(){		
		config = Config._getInstance();
		
	}
	

	public static DailyPattern _getInstance(){
		if (dailyPattern==null)
			dailyPattern = new DailyPattern();
		return dailyPattern;
	}


	public void setFile(File file){
		this.file = file;
		data = new GPSData(file.getPath());
	}

	public void createTimeline(Date from, Date to){
		
		Date day = (Date) from.clone();
		
		while (day.getTime()<to.getTime()){
			writeToFile(day, data, "svg");
			writeToFile(day, data, "jpg");
			day.setTime(day.getTime()+24*60*60*1000);
			
		}

	}

	public void showOutput(){
		String targetDate = "2013/12/24";
		Date day = null;

		try {
			day = DateUtils.parseDate(targetDate, "yyyy/MM/dd");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
		// Create a converter for this document.
		SVGGraphics2D g = new SVGGraphics2D(doc);	
		
		PrintDailyPattern.print(g,  data.getDayActivities(day), data.getStartingTimeInSec());
		// Display the document.
		Element root = doc.getDocumentElement();
		g.getRoot(root);

		JSVGCanvas canvas = new JSVGCanvas();
		JFrame f = new JFrame();
		f.getContentPane().add(canvas);
		canvas.setSVGDocument(doc);
		f.pack();
		f.setVisible(true);
	}
	


	public void writeToFile(Date day, GPSData data, String imgType){
		
		System.out.println("Creating "+imgType.toUpperCase()+" for "+DateUtils.truncate(day,Calendar.DATE));
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document doc = (Document)impl.createDocument(svgNS, "svg", null);
		
		Element root = doc.getDocumentElement();
      

		// Create a converter for this document.
		SVGGraphics2D g = new SVGGraphics2D(doc);
		List<Activity> activities = data.getDayActivities(day);
		if (activities == null){
			System.err.println("No data found for "+day);
			return;
		}


		PrintDailyPattern.print(g,  activities, data.getStartingTimeInSec());
		if (!imgType.equals("svg"))
			g.getRoot(root);
		
		boolean useCSS = true; // we want to use CSS style attributes
		Writer out;
		try {
			if (imgType.equals("svg")){
				//---------- save as SVG ----------------
				//out = new OutputStreamWriter(System.out, "UTF-8");
	
				//TODO Define output folder
				File file = new File("output/"+config.getFilename(day, "svg"));
				FileOutputStream fop = new FileOutputStream(file);
	
				// if file doesnt exist, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				out = new OutputStreamWriter(fop, "UTF-8");
				g.stream(out, useCSS);
				//g.stream(new OutputStreamWriter(System.out, "UTF-8"), useCSS);
				fop.flush();
				fop.close();
			}else{
				
				//---------- save as JPG -----------------------
				JPEGTranscoder t = new JPEGTranscoder();
		        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
		                   new Float(1));
		        t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(2700));//was 900
	
		        // Set the transcoder input and output.
		        TranscoderInput input = new TranscoderInput(doc);
		        
		        File fileJPG = new File("output/"+config.getFilename(day, "jpg"));
				OutputStream fopJPG = new FileOutputStream(fileJPG);
	
				// if file doesnt exists, then create it
				if (!fileJPG.exists()) {
					fileJPG.createNewFile();
				}
		        TranscoderOutput output = new TranscoderOutput(fopJPG);
	
		        // Perform the transcoding.
		        t.transcode(input, output);
		        fopJPG.flush();
		        fopJPG.close();
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVGGraphics2DIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void create() {
    	// dailyPattern.showOutput();
    	dailyPattern.createTimeline(data.getMinDateInFile(), data.getMaxDateInFile());
		
	}
}
