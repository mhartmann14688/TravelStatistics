package travelStatistics;
/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */


import java.awt.Font;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.event.DocumentEvent.EventType;

import org.apache.commons.lang3.time.DateUtils;

import travelStatistics.types.DailyPattern.DailyPattern;

import data.Config;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * Controller class of the HelloWorld sample.
 */
public class TravelStatisticsController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML
    private Label filename;
    
    @FXML 
    private TextField maxSpeedIdleTextField;
    
    @FXML 
    private TextField maxSpeedWalkingTextField;
    
    @FXML 
    private TextField maxSpeedBikingTextField;
    
    @FXML
    private ColorPicker idleColor;
    @FXML
    private ColorPicker walkingColor;
    @FXML
    private ColorPicker bikingColor;
    @FXML
    private ColorPicker drivingColor;
    
    @FXML //  fx:id="button"
    private Button generateDailyStatistics; // Value injected by FXMLLoader
    
     
    @FXML //  fx:id="button"
    private Button loadFile; // Value injected by FXMLLoader
    private Config c = Config._getInstance();
    int i=0;
    private DailyPattern dailyPattern = DailyPattern._getInstance();
    //TODO only for development purposes
    private File file = new File(".\\input.csv");


    /**
     * Initializes the controller class.
     */
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	//TODO remove
    	dailyPattern.setFile(file);
    	
    	//maxSpeedIdle
    	assert maxSpeedIdleTextField != null : "fx:id=\"maxSpeedIdle\" was not injected: check your FXML file";
        if (maxSpeedIdleTextField==null){
        	System.err.println("fx:id=\"maxSpeedIdle\" was not injected: check your FXML file");
        }
        maxSpeedIdleTextField.setText(""+c.getMaxSpeedIdle());
    	maxSpeedIdleTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                try {
                	c.setMaxSpeedIdle(Float.parseFloat(newValue));
                } catch (Exception e) {
                	maxSpeedIdleTextField.setText(oldValue);
                	System.err.println("Could not parse "+oldValue+": "+e.getMessage());
                }
            }});
    	
    	//maxSpeedWalking
        assert maxSpeedWalkingTextField != null : "fx:id=\"maxSpeedWalking\" was not injected: check your FXML file";
        if (maxSpeedWalkingTextField==null){
        	System.err.println("fx:id=\"maxSpeedWalking\" was not injected: check your FXML file");
        }
        
        maxSpeedWalkingTextField.setText(""+c.getMaxSpeedWalking());
        maxSpeedWalkingTextField.textProperty().addListener(new ChangeListener<String>() {
               @Override
               public void changed(ObservableValue<? extends String> observable,
                       String oldValue, String newValue) {
                   try {
                   	c.setMaxSpeedWalking(Float.parseFloat(newValue));
                   } catch (Exception e) {
                	   maxSpeedWalkingTextField.setText(oldValue);
                   	System.err.println("Could not parse "+oldValue+": "+e.getMessage());
                   }
               }});
        
        //idleColor
        idleColor.setValue(c.idleColor);
        idleColor.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	System.out.println("Setting color");
                c.idleColor = (idleColor.getValue());                    
            }
        });
        
        
      //walkingColor
        walkingColor.setValue(c.walkingColor);
        walkingColor.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	System.out.println("Setting color");
                c.walkingColor = (walkingColor.getValue());                    
            }
        });
        
      //drivingColor
        drivingColor.setValue(c.drivingColor);
        drivingColor.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	System.out.println("Setting color");
                c.drivingColor = (drivingColor.getValue());                    
            }
        });
    	
    	
    	//generate 
        assert generateDailyStatistics != null : "fx:id=\"generateDailyStatistics\" was not injected: check your FXML file";
        if (generateDailyStatistics==null){
        	System.err.println("fx:id=\"generateDailyStatistics\" was not injected: check your FXML file");
        }
        if (generateDailyStatistics != null) {
        	generateDailyStatistics.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                	if (file == null){
                		System.err.println("No file loaded");
                		//TODO Fehlermeldung
                	}else{
                		//TODO Configurable via UI
                		dailyPattern.create();
	            	}
                }
            });
         }
        
        //load file 
        assert loadFile != null : "fx:id=\"loadFile\" was not injected: check your FXML file";

        loadFile.setOnAction(new EventHandler<ActionEvent>(){
            @Override
           public void handle(ActionEvent arg0) {
               FileChooser fileChooser = new FileChooser();
               FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
               fileChooser.getExtensionFilters().add(extFilter);
               //TODO Abspeichern des Directories for next use
               //fileChooser.setInitialDirectory(new File("C:\\Users\\melanie\\workspace\\TravelStatistics\\"));
               file = fileChooser.showOpenDialog(TravelStatistics.primaryStage);
               System.out.println("File: "+file);
               dailyPattern.setFile(file);
               if (file!=null){
            	   filename.getStyleClass().remove("unloaded");
            	   filename.getStyleClass().add("loaded");
            	   filename.setText(file.getPath());
               }else{
            	   filename.getStyleClass().remove("loaded");
            	   filename.getStyleClass().add("unloaded");
            	   filename.setText("<<no file loaded>>");
               }
               
           }
       });
    }
}
