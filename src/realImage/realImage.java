/**
 * 
 */
package realImage;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int width = 800;
		int height= 600;
		
		int margin = 50; // Margin 50 pixels
		int maxW = width - margin;
		int maxH = height - margin;
				
		double lon	 = -100.0;
		double lat   = 90.134;
		
		// Strings to embbed on image
		String size = String.format("Width = %s Height = %s", width, height);
		String date = String.format("2014:03:12 12:38:38");
		String geo  = String.format("Lat = %f Long = %f", lat, lon);
		String place = "Montana 544, Broadus, MT 59317, USA";
		
		ArrayList<String> text = new ArrayList<String>();
		text.add(size);
		text.add(date);
		text.add(geo);
		text.add(place);
		
		// Create Base Image
        BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D myGraphic = im.createGraphics();
        myGraphic.setColor(Color.GREEN);
        myGraphic.fillRect(0, 0, width, height);
        myGraphic.setColor(Color.black);
		myGraphic.setFont(new Font("helvetica", Font.PLAIN, 40));

		//myGraphic.drawString(s, dw, dh);
		
		int rowSize = (height-140)/4;
		int rowPos = 50;

		for(String item : text)
		{ 
			int x = getDrawPoint(item, width, myGraphic);
			rowPos+=rowSize; 
		    myGraphic.drawString(item, x, rowPos);
		}
		
		try {
		    File outputfile = new File("saved.jpg");
		    ImageIO.write(im, "jpg", outputfile);
	
    		TiffOutputSet outputSet = new TiffOutputSet();
			TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
			exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, "2014:03:12 12:38:38");

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
