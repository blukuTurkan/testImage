/**
 * 
 */
package realImage;


import java.io.*;
import java.util.ArrayList;



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
import realImage.ImageCreator;

/**
 * @author antoniooh
 *
 */
public class realImage {

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
	        		 "orange", "pink", "red", "white", "yellow")
	        .setDefault("white");
		    
	    parser.addArgument("--title")
	        .help("Optional. Add a title into the image.")
	        .setDefault("Test Image");
	    
	    parser.addArgument("-n", "--number")
	        .help("Optional. Number sets the number of test images to be created.")
	        .type(Integer.class)
	        .setDefault(1);
	    
	    parser.addArgument("--lat")
	          .help("Optional. Latitude in decimal format. i.e.: 47.614848")
	          .type(double.class)
	          .setDefault(47.614848);
	    
	    parser.addArgument("--lon")
	    	  .help("Optional. Longitude in decimal format. i.e.: -122.3359058")
	       	  .type(double.class)
	    	  .setDefault(-122.3359058);
	    
	    /*
	     * MISSING ARGUMENTS
	     * -p --place "name of place" i.e.: "Seattle Center"
	     * -lat --latitude 
	     * -lon --longitude
	     * */
	    
	    
	    // Start processing command line arguments
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
				
        // Process Image Size CML Arg 
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
		
		 // Process Date CML Arg
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
        // Process Time CML Arg
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

        // Process Title CML Arg
        String title = ns.getString("title");


        // Process Latitude CML Arg
        double lat = ns.getDouble("lat");
                
        // Process Longitude CML Arg
        double lon = ns.getDouble("lon");
        
        GeoMath geocalculator = new GeoMath(lat, lon);
        
		String geo   = String.format("Lat = %f Long = %f", lat, lon);
		String place = "Place Place Holder";
		
		// Create an Array to iterate through text to be embedded on test image
		ArrayList<String> dataOnImage = new ArrayList<String>();
		dataOnImage.add(title);
		dataOnImage.add(String.format("Width = %s Height = %s", imageWidth, imageHeight));
		dataOnImage.add(dateTimeOriginal);
		dataOnImage.add(geo);
		dataOnImage.add(place);
		
		// Process Color CML Arg
		String color = ns.getString("backcolor");

		// Process Number of Images CML Arg
		int numberOfImages = ns.getInt("number");
	    numberOfImages = numberOfImages > 0 ? numberOfImages : 1;
	    boolean appendCtr = numberOfImages > 1 ? true : false;
	    int ctr = 1;
				
	    
		// Create Test Images
		while(numberOfImages > 0)
		{
			String fileName = title;
			if (appendCtr)
			{
				fileName = title.concat(" " + ctr);
				dataOnImage.set(0, fileName);
				ctr++;
				
				geocalculator.CalculateRandomDestinationPoint();
				lat = geocalculator.lat2;
				lon = geocalculator.lon2;
				dataOnImage.set(3, String.format("Lat = %f Long = %f", lat, lon));
			}
			
			
			
			ImageCreator testImage = new ImageCreator(imageWidth, imageHeight, color);
			testImage.CreateJPEGImage();
			testImage.EmbedTextToImage(dataOnImage);
			
			numberOfImages--;
			
			try {
			    //File outputfile = new File("saved.jpg");
			    //ImageIO.write(im, "jpg", outputfile);
		
				String fullFileName = title.concat(".jpg");
				testImage.SaveImageJPEGToFile(fullFileName);
				
	    		TiffOutputSet outputSet = new TiffOutputSet();
				TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
				exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, dateTimeOriginal);
	            exifDirectory.add(ExifTagConstants.EXIF_TAG_PROCESSING_SOFTWARE, "Test Image Creator");
				outputSet.setGPSInDegrees(lon, lat);
				
				String outPutFileName = fileName.concat("_data.jpg");
				
				File input = new File(outPutFileName);
				File outputfile = new File(fullFileName);
				OutputStream os = new FileOutputStream(input);
				os = new BufferedOutputStream(os);
				new ExifRewriter().updateExifMetadataLossless(outputfile, os, outputSet);
				
			} catch (ImageWriteException e) {
				// TODO Auto-generated catch block
				System.out.println("Image Write Error");
				e.printStackTrace();
			}  catch (IOException e) {
			    System.out.println("oops... something went wrong");
			    e.printStackTrace();
			} catch (ImageReadException e) {
				// TODO Auto-generated catch block
				System.out.println("Image Read Error");
				e.printStackTrace();
			}
		}

	}

}
