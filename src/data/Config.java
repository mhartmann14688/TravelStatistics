package data;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Config {
	private static Config config=null;
	

	private Config(){
		 //Date Format 1PM etc.
		 dateFormatHourLabels = new SimpleDateFormat ("ha");
		 dateFormatHourLabels.setTimeZone(TimeZone.getTimeZone("UTC"));
		 dateFormatFileName = new SimpleDateFormat("yyyy-MM-dd");
	}

	public static Config _getInstance(){
		if (config==null){
			config = new Config();
		}
		return config;
	}
	
	//public final String outputType = "jpg"; //svg or jpg
	
	
	private SimpleDateFormat dateFormatHourLabels;
	private SimpleDateFormat dateFormatFileName;
	
	public String getFilename(Date date, String fileFormat){
		return "daily_"+dateFormatFileName.format(date)+"."+fileFormat;
	}

	//totalHeight
	private IntegerProperty totalHeight = new SimpleIntegerProperty(30);
	public final int getTotalHeight(){return totalHeight.get();}
	public final void setTotalHeight(int value){totalHeight.set(value);}
	public IntegerProperty totalHeightProperty() {return totalHeight;}
	
	//maxSpeedIdle
	private FloatProperty maxSpeedIdle = new SimpleFloatProperty(0.2f);
	public final float getMaxSpeedIdle(){return maxSpeedIdle.get();}
	public final void setMaxSpeedIdle(float value){System.out.println("Setting max speed idle to "+value);
	maxSpeedIdle.set(value);}
	public FloatProperty maxSpeedIdleProperty() {System.out.println("Getting property");
	return maxSpeedIdle;}
	
	
	//maxSpeedWalking
		private FloatProperty maxSpeedWalking = new SimpleFloatProperty(8f);
		public final float getMaxSpeedWalking(){return maxSpeedWalking.get();}
		public final void setMaxSpeedWalking(float value){maxSpeedWalking.set(value);}
		public FloatProperty maxSpeedWalkingProperty() {return maxSpeedWalking;}
		
		//maxSpeedBiking
				private FloatProperty maxSpeedBiking = new SimpleFloatProperty(32f);
				public final float getMaxSpeedBiking(){return maxSpeedBiking.get();}
				public final void setMaxSpeedBiking(float value){System.out.println("Setting max speed biking to "+value);
				maxSpeedBiking.set(value);}
				public FloatProperty maxSpeedBikingProperty() {System.out.println("Getting property");
				return maxSpeedBiking;}
			
		
	public javafx.scene.paint.Color idleColor = javafx.scene.paint.Color.rgb(210,210,210);
	public javafx.scene.paint.Color walkingColor = javafx.scene.paint.Color.rgb(183,206,132);
	public javafx.scene.paint.Color bikingColor = javafx.scene.paint.Color.rgb(153,179,255);
	public javafx.scene.paint.Color drivingColor = javafx.scene.paint.Color.rgb(255,153,153);
	
	public Color getColorForSpeed(float speed){
		//speed = speed * 3.6f; //as it is usually entered in m/s whereas we are using km/h
		if(speed<getMaxSpeedIdle()) return transformToAWTColor(idleColor);
		if (speed<getMaxSpeedWalking()) return transformToAWTColor(walkingColor) ;
		if (speed<getMaxSpeedBiking()) return transformToAWTColor(bikingColor) ;		
		else return transformToAWTColor(drivingColor);
	}
	
	private Color transformToAWTColor(javafx.scene.paint.Color color){
		return new Color((int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
	}
	
	public SimpleDateFormat getDateFormat(){
		return dateFormatHourLabels;
	}


	public Color getColorForSpeed(ActivityType activity) {
		switch (activity) {
			case IDLE: return transformToAWTColor(idleColor);
			case WALKING: return transformToAWTColor(walkingColor);
			case BIKING: return transformToAWTColor(bikingColor);
			default: return transformToAWTColor(drivingColor);
		}
	}
}
