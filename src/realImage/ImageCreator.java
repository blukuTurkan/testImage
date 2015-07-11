package realImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageCreator {

	private Graphics2D graphic;
	private BufferedImage image;
	private int width;
	private int height;
	private String color;
	private int fontSize;
	private int margin;
	
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
		
		// Calculate the Height of the whole lines of text to embed
		// Adjust margin and font size as required to fit text
		while( CalculateEmbeddedTextHeight(rows) >= this.height )
		{
			this.margin   /= 2;
			this.fontSize -= 10;
			
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
				tmpFontSize = (tmpFontSize-10 > 10) ? tmpFontSize-10 : 5;
				this.graphic.setFont(new Font("helvetica", Font.PLAIN, tmpFontSize));
			}
			
			int x = GetCenteredDrawPoint(item);
		    this.graphic.drawString(item, x, rowPosition);
		    rowPosition += ( this.margin + this.fontSize );
		}
	}
	
	public void SaveImageJPEGToFile(String fileName)
	{
		if (!fileName.contains(".jpg"))
		{
			fileName = fileName.concat(".jpg");
		}
		
		try {
		    File outputfile = new File(fileName);
		    ImageIO.write(this.image, "jpg", outputfile);
		} catch (IOException e) {
		    System.out.println("Could not save image " + fileName + " to File.");
		}
	}
}
