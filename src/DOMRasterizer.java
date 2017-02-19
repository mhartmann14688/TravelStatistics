import java.io.*;
import java.util.Date;
import java.util.List;

import data.Activity;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;

import data.GPSData;

import travelStatistics.types.DailyPattern.PrintDailyPattern;

public class DOMRasterizer {
	
	public Document createDocument2() throws IOException{
		GPSData data = new GPSData("turkey.csv");
		Date day = data.getMaxDateInFile();
		
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document doc = (Document)impl.createDocument(svgNS, "svg", null);
		
		Element root = doc.getDocumentElement();
        root.setAttributeNS(null, "width", "450");
        root.setAttributeNS(null, "height", "500");


		// Create a converter for this document.
		SVGGraphics2D g = new SVGGraphics2D(doc);
		
		List<Activity> activities = data.getDayActivities(day);
		if (activities == null){
			System.err.println("No data found for "+day);
			return null;
		}

		PrintDailyPattern.print(g,  activities, data.getStartingTimeInSec());
		
		g.getRoot(root);
		//Writer writer = new OutputStreamWriter(new FileOutputStream("out.svg"), "UTF-8");
		//g.stream(root, writer);
        //writer.close();
		
		return doc;

	}

    public Document createDocument() throws SVGGraphics2DIOException, UnsupportedEncodingException {
    	GPSData data = new GPSData("turkey.csv");
		Date day = data.getMaxDateInFile();

        // Create a new document.
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document document =
          impl.createDocument(svgNS, "svg", null);
        Element root = document.getDocumentElement();
        root.setAttributeNS(null, "width", "450");
        root.setAttributeNS(null, "height", "500");

        // Add some content to the document.
        Element e;
        e = document.createElementNS(svgNS, "rect");
        e.setAttributeNS(null, "x", "10");
        e.setAttributeNS(null, "y", "10");
        e.setAttributeNS(null, "width", "200");
        e.setAttributeNS(null, "height", "300");
        e.setAttributeNS(null, "style", "fill:red;stroke:black;stroke-width:4");
        root.appendChild(e);

        e = document.createElementNS(svgNS, "circle");
        e.setAttributeNS(null, "cx", "225");
        e.setAttributeNS(null, "cy", "250");
        e.setAttributeNS(null, "r", "100");
        e.setAttributeNS(null, "style", "fill:green;fill-opacity:.5");
        root.appendChild(e);
        SVGGraphics2D g = new SVGGraphics2D(document);
        PrintDailyPattern.print(g,  data.getDayActivities(day), data.getStartingTimeInSec());
		System.out.println(g.toString());
		g.stream(new OutputStreamWriter(System.out, "UTF-8"));
        return document;
    }

    public void save(Document document) throws Exception {

        // Create a JPEGTranscoder and set its quality hint.
        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
                   new Float(.8));

        // Set the transcoder input and output.
        TranscoderInput input = new TranscoderInput(document);
        OutputStream ostream = new FileOutputStream("out.jpg");
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Perform the transcoding.
        t.transcode(input, output);
        ostream.flush();
        ostream.close();
    }

    public static void main(String[] args) throws Exception {
        // Runs the example.
        DOMRasterizer rasterizer = new DOMRasterizer();
        Document document = rasterizer.createDocument2();
        rasterizer.save(document);
        System.exit(0);
    }
}