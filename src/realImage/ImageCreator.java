package realImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class ImageCreator {

	private Graphics2D graphic;
	private BufferedImage image;
	private int width;
	private int height;
	private String color;
	private int fontSize;
	private int margin;
	private String dateTimeOriginal;
	
	public ImageCreator(int w, int h, String background)
	{
		this.width = w;
		this.height = h;
		this.color = background;
		this.margin = 50;
		this.fontSize = 40;
	}

	private Color GetColorByName(String colorName)
	{
		Color colorObj;
		if (colorName == null || colorName.isEmpty())
		{
		    return Color.white;	
		}
		
		try 
		{
		    Field field = Color.class.getField(colorName);
		    colorObj = (Color)field.get(null);
		    return colorObj;
		} catch (Exception e) {
			return Color.white;
		}
	}
	
	public void CreateJPEGImage()
	{
		this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR );
        this.graphic = this.image.createGraphics();
        // Set background color
        Color background = GetColorByName(this.color);
        this.graphic.setColor(background);
        this.graphic.fillRect(0, 0, width, height);
        this.graphic.setColor(Color.black);
	}
	
	private boolean DoesTextFits(String text)
	{
		FontMetrics fm = this.graphic.getFontMetrics();
		int textLength = fm.stringWidth(text);
		return textLength <= this.width;
	}
	
	private int GetCenteredDrawPoint(String text)
	{
		FontMetrics fm = this.graphic.getFontMetrics();
		return (this.width - fm.stringWidth(text))/2;
	}
	
	private int CalculateEmbeddedTextHeight(int rows)
	{
		return margin*(rows+1) + (rows * this.fontSize);
	}
	
	public void EmbedTextToImage(ArrayList<String> textItems)
	{
		int rows = textItems.size();
		dateTimeOriginal = textItems.get(2);
		// Calculate the Height of the whole lines of text to embed
		// Adjust margin and font size as required to fit text
		while( CalculateEmbeddedTextHeight(rows) >= this.height-margin )
		{
			this.margin   -= 10;
			this.fontSize -= 3;
			
			if (this.margin < this.fontSize)
			{
				this.margin = this.fontSize;
			}
		}
		
		int rowPosition = (this.height-CalculateEmbeddedTextHeight(rows))/2;
		
		//int rowPosition = margin + this.fontSize;
				
		for(String item : textItems)
		{
			this.graphic.setFont(new Font("helvetica", Font.PLAIN, this.fontSize));
			int tmpFontSize = this.fontSize;
			
			// Check if the text fits (width). Modify Font Size to make it fit.
			while(!DoesTextFits(item))
			{
				tmpFontSize = (tmpFontSize-2 > 20) ? tmpFontSize-2 : 20 ;
				this.graphic.setFont(new Font("helvetica", Font.PLAIN, tmpFontSize));
			}
			
			int x = GetCenteredDrawPoint(item);
		    this.graphic.drawString(item, x, rowPosition);
		    rowPosition += ( this.margin + this.fontSize );
		}
	}
	
	public void SaveImageJPEGToFile(String fileName, GeoData exif)
	{
		try 
		{
		    File outputfile = new File("template.jpg");
		    ImageIO.write(this.image, "jpg", outputfile);

			
			TiffOutputSet outputSet = new TiffOutputSet();
			TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
			exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, dateTimeOriginal);
	        exifDirectory.add(ExifTagConstants.EXIF_TAG_PROCESSING_SOFTWARE, "Test Image Creator");
			outputSet.setGPSInDegrees(exif.lng, exif.lat);
			
			if (!fileName.contains(".jpg"))
			{
				fileName = fileName.concat(".jpg");
			}
			
			String outPutFileName = fileName.replace(" ","_");
			
			File input = new File(outPutFileName);
			
			OutputStream os = new FileOutputStream(input);
			os = new BufferedOutputStream(os);
			new ExifRewriter().updateExifMetadataLossless(outputfile, os, outputSet);
			
			System.out.println("Image <" + outPutFileName  +"> created.");
		} 
		catch (IOException e) 
		{
			    System.out.println("Could not save image " + fileName + " to File.");	
		} 
		catch (ImageWriteException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Image Write Error");
			e.printStackTrace();
		}  
		catch (ImageReadException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Image Read Error");
			e.printStackTrace();
	    }
	}
}
