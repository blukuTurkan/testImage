package realImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public final class ImageCreator {

	private Graphics2D image;
	private int width;
	private int height;
	private String color;
	private int fontSize;
	
	public ImageCreator(int w, int h, String background)
	{
		this.width = w;
		this.height = h;
		this.color = background;
		this.fontSize = 40;
		
		BufferedImage im = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR );
        this.image = im.createGraphics();
        this.image.setColor(Color.white);
        this.image.fillRect(0, 0, width, height);
        this.image.setColor(Color.black);
        this.image.setFont(new Font("helvetica", Font.PLAIN, this.fontSize));
	}

	private boolean DoesTextFits(String text)
	{
		FontMetrics fm = this.image.getFontMetrics();
		int textLength = fm.stringWidth(text);
		return textLength <= this.width;
	}
	
	private int GetCenteredDrawPoint(String text)
	{
		FontMetrics fm = this.image.getFontMetrics();
		return (this.width - fm.stringWidth(text))/2;
	}
	
	public static void EmbedTextToImage(ArrayList<String> textItems)
	{
		int rowSeparationPixel = 50;
		int rows = textItems.size();
		int embeddedTextHeight = rowSeparationPixel*(rows-1) + (rows*this.fontSize);
		
		for(String item : textItems)
		{
			
		}
	}
	
}
