/**
 * 
 */
package realImage;

// Image Creation
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;

// Image EXIF tag processing
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

// Argument Parser
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author antoniooh
 *
 */
public class realImage {

	/**
	 * @param args
	 */
	public static class drawPoint
	{
		public int x;
		public int y;
	}
	
	public static int getDrawPoint(String text, int w, Graphics2D image)
	{
		FontMetrics fm = image.getFontMetrics();
		int x = (w - fm.stringWidth(text))/2;
		if (x < 0)
		{
			System.out.println("Text wider that image widht. Re-adjusting image");
		}

		return x;
	}

	
	public static class timeData 
	{
		public String hours;
		public String minutes;
		public String seconds;
		
	}

	
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("realImage")
				.defaultHelp(true)
				.description("Create Test images with EXIF data");
		
		parser.addArgument("-s", "--size")
		    .help("Size of the output image. Width, Height.\n"
		    		+ "Format: [WidhtxHeight] WxH i.e.: 800x600");
		
		parser.addArgument("-d", "--date")
		    .help("Optional. Date to be used as the 'date' component of the EXIF_TAG_DATE_TIME_ORIGINAL.\n"
		    		+ "Format: [Month:Day:Year] mm:dd:yyyy i.e.: 03:15:1999 March 15th 1999\nDefault: "
		    		+ "Jan 1st 2015");
		    //.setDefault("01:01:2015");
		
		parser.addArgument("-t", "--time")
		    .help("Optional. Time to be used as the 'time' component of the EXIF_TAG_DATE_TIME_ORIGINAL.\n"
		    		+ "Format: [Hours:Minutes:Seconds] hh:mm:ss i.e.: 15:14:13 03:14:13 PM\nDefault:12:00:00");
		    //.setDefault("12:00:00");
		
		parser.addArgument("-bc", "--backcolor")
	        .help("Optional. Set the background color for the image.")
	        .choices("black", "blue", "cyan", "darkGray", "gray", "green", "lightGray", "magenta",
	        		 "orange", "pink", "red", "white", "yellow");
		    
	    parser.addArgument("--title")
	        .help("Optional. Add a title into the image.")
	        .setDefault("Test Image");
	    
	    parser.addArgument("-c", "--count")
	        .help("Optional. Count sets the number of test images to be created.")
	        .setDefault(1);
	    
	    /*
	     * MISSING ARGUMENTS
	     * -p --place "name of place" i.e.: "Seattle Center"
	     * -lat --latitude 
	     * -lon --longitude
	     * */
	    		
		Namespace ns = null;
        try 
        {
            ns = parser.parseArgs(args);
        } 
        catch (ArgumentParserException e) 
        {
        	System.out.println("[Error] - Error parsing command line arguments.");
            parser.handleError(e);
            System.exit(1);
        }
				

        // Process arguments
        String size = ns.getString("size");
        
        // Default Image Size 800 x 600
		int imageWidth = 800;
		int imageHeight = 600;
		if (size != null)
		{
			size = size.toLowerCase();
			if (size.contains("x")) 
			{
				String[] sizes = size.split("x");
				if (sizes.length == 2)
				{
				   // TODO: Not checking for only 'digit' chars on input string
				   imageWidth  = Integer.parseInt(sizes[0]);
			       imageHeight = Integer.parseInt(sizes[1]);
				}
			}
		}
		                       
        String date = ns.getString("date");
        if(date != null)
        {
        	String[] dateData = date.split(":");
        	if (dateData.length == 3 && 
        		dateData[0].length() == 2 &&
        		dateData[1].length() == 2 &&
        		dateData[2].length() == 4)
        	{
        		date = dateData[2] + ":" + dateData[0] + ":" + dateData[1];
        	}
        	else
        	{
        		date = "2015:01:01";
        	}
        }
        
        String time = ns.getString("time");
        if (time != null)
        {
        	String[] timeData = time.split(":");
        	if (timeData.length == 3 && 
        		timeData[0].length() == 2 &&
        		timeData[1].length() == 2 &&
        		timeData[2].length() == 2)
        	{
        		time = timeData[0] + ":" + timeData[1] + ":" + timeData[2];
        	}
        	else
        	{
        		time = "12:00:00";
        	}

        }

        String dateTimeOriginal = String.format("%s %s", date, time);
		
		int margin = 50; // Margin 50 pixels
		int maxW = imageWidth - margin;
		int maxH = imageHeight - margin;
				
		double lon	 = -100.0;
		double lat   = 90.134;
		
		// Strings to embed on image
		String title = "Test Image"; 
		String geo   = String.format("Lat = %f Long = %f", lat, lon);
		String place = "Montana 544, Broadus, MT 59317, USA";
		
		// Create an Array to iterate through text to be embedded on test image
		ArrayList<String> text = new ArrayList<String>();
		text.add(title);
		text.add(String.format("Widht = %s Height = %s", imageWidth, imageHeight));
		text.add(dateTimeOriginal);
		text.add(geo);
		text.add(place);
		
		// Create Base Image
        BufferedImage im = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_BGR );
        Graphics2D myGraphic = im.createGraphics();
        myGraphic.setColor(Color.blue);
        myGraphic.fillRect(0, 0, imageWidth, imageHeight);
        myGraphic.setColor(Color.black);
		myGraphic.setFont(new Font("helvetica", Font.PLAIN, 40));

		//myGraphic.drawString(s, dw, dh);
		
		int rowSize = (imageHeight-140)/4;
		int rowPos = 50;

		for(String item : text)
		{ 
			int x = getDrawPoint(item, imageWidth, myGraphic);
		    myGraphic.drawString(item, x, rowPos);
		    rowPos+=rowSize;
		}
		
		try {
		    File outputfile = new File("saved.jpg");
		    ImageIO.write(im, "jpg", outputfile);
	
    		TiffOutputSet outputSet = new TiffOutputSet();
			TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
			exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, dateTimeOriginal);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_PROCESSING_SOFTWARE, "Test Image Creator");
			outputSet.setGPSInDegrees(lon, lat);
			
			File input = new File("saved_data.jpg");
			
			OutputStream os = new FileOutputStream(input);
			os = new BufferedOutputStream(os);
			new ExifRewriter().updateExifMetadataLossless(outputfile, os, outputSet);
			
		} catch (ImageWriteException e) {
			// TODO Auto-generated catch block
			System.out.println("Image Write Error");
			e.printStackTrace();
		}  catch (IOException e) {
		    System.out.println("oops... something went wrong");
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			System.out.println("Image Read Error");
			e.printStackTrace();
		}

		

	}

}
