package travelStatistics.types.DailyPattern;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.AbstractMap;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import data.Activity;
import data.ActivityType;
import org.apache.batik.svggen.SVGGraphics2D;

import data.Config;
import data.GPSData;
import org.apache.commons.lang3.time.DateUtils;

import static data.ActivityType.IDLE;


public class PrintDailyPattern {
	static int height = 10;
   // static int strokeWidth = 3;
    //configuration
    static int elementWidth = 3;
    static int hoursPerSegment = 3;
    
    static int widthHourLine = 2;
    static float heightHourLineRelativeToHeight = 1.07f;
    private static Color hourLineColor = new Color(72,62,55);
    		// 483e37ff;
    private static float fontSize = 8.0f;
    
    private static boolean horizontal = false;
    
   
    
    
    
	public static void print(SVGGraphics2D g, List<Activity> list, final long startingTimeInSec) {
		Config config = Config._getInstance();

		//TODO Runden beachten...
		int secondsPerSegment = hoursPerSegment*60*60;
		//TODO runden mit Sampling Interval
	    long firstHour = Math.round(Math.ceil(startingTimeInSec / (double)secondsPerSegment)*secondsPerSegment); //to be calculated
	    long showHourBar = firstHour; 
	       
	    int totalHeight = config.getTotalHeight();
	    int x = 0;
	    List<Entry<String,Integer>> labels = new LinkedList<>();
	    //TODO runden...runden mit Sampling Interval
	    //long time = startingTimeInSec;
	  /*  if (!horizontal){
	    	g.rotate(Math.toRadians(45));
	    	//g.translate(totalHeight,0);
	  	   
	    }*/
	    int offset = (horizontal)?0:(int)(totalHeight*(heightHourLineRelativeToHeight-1));
        int currentIndex = 0;
		ActivityType currentActivity = IDLE;
        for (long time = startingTimeInSec; time<=GPSData.getEndTimeInSec(); time+=GPSData.getSamplingRateInSec()){
	    //for(int i=0; i<list.size(); i++){
	    	if (time==showHourBar){
	    		if (time!=startingTimeInSec && time!=GPSData.getEndTimeInSec()){
		    		Shape h = new Rectangle(x, 0, widthHourLine,(int)(totalHeight*heightHourLineRelativeToHeight));
			    	g.setPaint(hourLineColor);
			    	g.fill(h);
			    	x+=widthHourLine;
			    	int xPosLabel = (horizontal)?x+1:x+9;
			    	labels.add(new AbstractMap.SimpleEntry<String, Integer>(getHourString(time),xPosLabel));
			 	}
	    		showHourBar +=secondsPerSegment;
	    	}
            if (time >= list.get(currentIndex).getEndDate().getSecondOfDay()){
                if (currentIndex < list.size()-1) {
                    currentIndex++;
                    currentActivity = list.get(currentIndex).getActivity();
                }else{
                    currentActivity = IDLE;
                }
            }
	    	Shape r = new Rectangle(x, offset, elementWidth,totalHeight);
	    	g.setPaint(config.getColorForSpeed(currentActivity));
	    	g.fill(r);
	    	x+=elementWidth;

	    	//time+=GPSData.getSamplingRateInSec();
	    }
	    g.setPaint(hourLineColor);
	    AffineTransform affineTransform = new AffineTransform();
	    if (!horizontal)	
	    	affineTransform.rotate(Math.toRadians(-90), 0, 0);
	    
	    g.setFont(g.getFont().deriveFont(fontSize).
	    		deriveFont(Font.BOLD).
	    		deriveFont(affineTransform));
	    for (Entry<String,Integer> label:labels){
	    	if (horizontal)
	    		g.drawString(label.getKey(), label.getValue(), (int)(totalHeight*heightHourLineRelativeToHeight));
	    	else
	    		g.drawString(label.getKey(), label.getValue(), (int)(totalHeight));
	    }
	//    Dimension canvasSize = (horizontal)?new Dimension(x+10,totalHeight+10):
	//    	new Dimension(totalHeight+10,x+10);

	    Dimension canvasSize = new Dimension(x,(int)(totalHeight*heightHourLineRelativeToHeight));
	    	
	    g.setSVGCanvasSize(canvasSize);

	}


	public static void print2(SVGGraphics2D g, List<Float> list, final long startingTimeInSec) {
		Config config = Config._getInstance();

		//TODO Runden beachten...
		int secondsPerSegment = hoursPerSegment*60*60;
		//TODO runden mit Sampling Interval
		long firstHour = Math.round(Math.ceil(startingTimeInSec / (double)secondsPerSegment)*secondsPerSegment); //to be calculated
		long showHourBar = firstHour;

		int totalHeight = config.getTotalHeight();
		int x = 0;
		List<Entry<String,Integer>> labels = new LinkedList<>();
		//TODO runden...runden mit Sampling Interval
		long time = startingTimeInSec;
	  /*  if (!horizontal){
	    	g.rotate(Math.toRadians(45));
	    	//g.translate(totalHeight,0);

	    }*/
		int offset = (horizontal)?0:(int)(totalHeight*(heightHourLineRelativeToHeight-1));
		for(int i=0; i<list.size(); i++){
			if (time==showHourBar){
				if (i!=0 && i!=list.size()-1){
					Shape h = new Rectangle(x, 0, widthHourLine,(int)(totalHeight*heightHourLineRelativeToHeight));
					g.setPaint(hourLineColor);
					g.fill(h);
					x+=widthHourLine;
					int xPosLabel = (horizontal)?x+1:x+9;
					labels.add(new AbstractMap.SimpleEntry<String, Integer>(getHourString(time),xPosLabel));
				}
				showHourBar +=secondsPerSegment;
			}
			Shape r = new Rectangle(x, offset, elementWidth,totalHeight);
			g.setPaint(config.getColorForSpeed(list.get(i)));
			g.fill(r);
			x+=elementWidth;
			time+=GPSData.getSamplingRateInSec();
		}
		g.setPaint(hourLineColor);
		AffineTransform affineTransform = new AffineTransform();
		if (!horizontal)
			affineTransform.rotate(Math.toRadians(-90), 0, 0);

		g.setFont(g.getFont().deriveFont(fontSize).
				deriveFont(Font.BOLD).
				deriveFont(affineTransform));
		for (Entry<String,Integer> label:labels){
			if (horizontal)
				g.drawString(label.getKey(), label.getValue(), (int)(totalHeight*heightHourLineRelativeToHeight));
			else
				g.drawString(label.getKey(), label.getValue(), (int)(totalHeight));
		}
		//    Dimension canvasSize = (horizontal)?new Dimension(x+10,totalHeight+10):
		//    	new Dimension(totalHeight+10,x+10);

		Dimension canvasSize = new Dimension(x,(int)(totalHeight*heightHourLineRelativeToHeight));

		g.setSVGCanvasSize(canvasSize);

	}


	private static String getHourString(long timeInSec) {
		return Config._getInstance().getDateFormat().format(new Date(timeInSec*1000));
	}
	
	

}
