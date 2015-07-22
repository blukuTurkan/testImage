/**
 * @author Antonio Orellana Handal
 * @version 1.0.1
 */
package realImage;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Argument Parser
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import realImage.ImageCreator;

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
		
		parser.addArgument("-t", "--time")
		    .help("Optional. Time to be used as the 'time' component of the EXIF_TAG_DATE_TIME_ORIGINAL.\n"
		    		+ "Format: [Hours:Minutes:Seconds] hh:mm:ss i.e.: 15:14:13 03:14:13 PM\nDefault:12:00:00");
		
		parser.addArgument("-bc", "--backcolor")
	        .help("Optional. Set the background color for the image.")
	        .choices("blue", "cyan", "gray", "green", "orange", "pink", "red", "white", "yellow")
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
	          .type(Double.class)
	          .setDefault(47.6062095);
	    
	    parser.addArgument("--lon")
	    	  .help("Optional. Longitude in decimal format. i.e.: -122.3359058")
	       	  .type(Double.class)
	    	  .setDefault(-122.3320708);
	    	    
	    parser.addArgument("-p", "--place")
	          .help("Optional. Set the 'human friendly name' of the place to be used as location.");
	          	    
	    parser.addArgument("--travel")
	          .help("Optional. Create the specified number of pictures as a travel. "
	          		+ "Date is assigned as a randomly from the base Date "
	          		+ "and Time set (Max offset is a week). Location is randomly assigned"
	          		+ " using a 5Km radius from the Lat-Long position or place specificied.")
	          .type(Integer.class)
	          .setDefault(1);
	    
	    // Start processing command line arguments
		Namespace ns = null;
        try 
        {
      	    ns = parser.parseArgs(args);
        } 
        catch (ArgumentParserException e) 
        {
        	System.out.println(e.getMessage());
        	parser.handleError(e);
            System.exit(1);
        }
				
        // Process Image Size command line argument
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
		
		// Process Date command line argument
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
        }
        
        // Process Time command line argument
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
        }
        
        // Get today date
        String dateTimeOriginal = "";
        if (date == null  || time == null)
        {
        	Date now = new Date();
        	Timestamp nowTimeStamp = new Timestamp(now.getTime());
        	dateTimeOriginal = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(nowTimeStamp);
        }
        else
        {
	        // Date Time Original - String to be embedded as part of the EXIF metadata
	        dateTimeOriginal = String.format("%s %s", date, time);
        }

        // Process Title command line argument
        String title = ns.getString("title");
        
        // Process Latitude command line argument
        double lat = ns.getDouble("lat");
                
        // Process Longitude command line argument
        double lon = ns.getDouble("lon");
        
        // Process Place command line argument
        String place = ns.getString("place");
        GeoData placeData = null;
        if(place != null)
        {   
        	// Place Name resolution has priority over coordinates
        	placeData = Geocoding.ResolvePlace(place);
        }
        else 
        {
        	placeData = Geocoding.ResolveCoordinates(lat, lon);
        	place = "";
        }
        
        if (placeData != null)
    	{
        	// Place resolution worked (either by place or coordinates)
    	    place = placeData.place;
    	    lat = placeData.lat;
    	    lon = placeData.lng;
        }
        else
        {
        	// We need place data to add EXIF location
        	placeData = new GeoData(lat, lon, "");
        }

        // Used for --travel option
        GeoMath geocalculator = new GeoMath(lat, lon);

		ImageData imageDescription = new ImageData();
		imageDescription.SetTitle(title);
		imageDescription.SetHeight(imageHeight);
		imageDescription.SetWidth(imageWidth);
		imageDescription.SetDateTime(dateTimeOriginal);
		imageDescription.SetLatitude(lat);
		imageDescription.SetLongitude(lon);
		imageDescription.SetPlace(place);
		
		
		// Process Color command line argument
		String color = ns.getString("backcolor");

		// Process Number of Images command line argument
		int numberOfImages = ns.getInt("number");
	    numberOfImages = numberOfImages > 0 ? numberOfImages : 1;
	    int ctr = 1;
				
	    int travel = ns.getInt("travel");
	    boolean isTravel = travel > 1 ? true : false;
	    
	    numberOfImages = travel;
	    boolean appendCtr = numberOfImages > 1 ? true : false;
	    
		// Create Test Images
		while(numberOfImages > 0)
		{
			String fileName = title;
			if (appendCtr)
			{
				// Set Image Title
				fileName = title.concat(" " + ctr);
				imageDescription.SetTitle(fileName);
				
								
				if (isTravel && numberOfImages < travel) 
				{
				    geocalculator.CalculateRandomDestinationPoint();
				    lat = geocalculator.GetLatitude();
				    lon = geocalculator.GetLongitude();
				    
				    placeData = Geocoding.ResolveCoordinates(lat, lon);
				    
				    if (placeData != null)
				    {
			    	    place = placeData.place;
			    	    lat = placeData.lat;
			    	    lon = placeData.lng;
				    }
			        else
			        {
			        	// We need place data to add EXIF location
			        	placeData = new GeoData(lat, lon, "");
			        }
				    
				    // Calculate Random Date. Offset is 1 week from base date.
				    DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
				    String newDateTimeOriginal = "";
				    try {
						Date baseDate = dateFormat.parse(dateTimeOriginal);
						long MILLISECONDS_PER_WEEK = 604800000;
						long randomDateMsec = baseDate.getTime() + (long)(Math.random()*MILLISECONDS_PER_WEEK);
						Timestamp newTimestamp = new Timestamp(randomDateMsec);
						newDateTimeOriginal = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(newTimestamp);
					} catch (ParseException e) {
						newDateTimeOriginal = "1970:01:01 12:00:00";						
					}

				    imageDescription.SetTitle(fileName);
				    imageDescription.SetDateTime(newDateTimeOriginal);
				    imageDescription.SetLatitude(placeData.lat);
				    imageDescription.SetLongitude(placeData.lng);
				    imageDescription.SetPlace(placeData.place);
				}
			}

			ImageCreator testImage = new ImageCreator(imageWidth, imageHeight, color);
			testImage.CreateJPEGImage();
			testImage.EmbedTextToImage(imageDescription.GetData());
			testImage.SaveImageJPEGToFile(fileName, placeData);
			
			ctr++;
			numberOfImages--;
		}
	}

}
