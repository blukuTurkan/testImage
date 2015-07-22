package realImage;

import java.util.ArrayList;

public class ImageData {

	private String title = "";
	private String place = "";
	private String dateTime = "";
	private int width = -1;
	private int height = -1;
	private double lat = -500;
	private double lon = -500;
	public ArrayList<String> data;
	
	public ImageData()
	{
		data = new ArrayList<String>();
		data.add(title);
		data.add(String.format("Width = %s Height = %s", width, height));
		data.add(dateTime);
		data.add(String.format("Lat = %f Long = %f", lat, lon));
		data.add(place);
	}
	
	public void SetTitle(String title)
	{
		this.title = title;
	}
	
	public void SetWidth(int w)
	{
		this.width = w;
	}
	
	public void SetHeight(int h)
	{
		this.height = h;
	}
	
	public void SetPlace(String places)
	{
		this.place = places;
	}
	
	public void SetLatitude(double lat)
	{
		this.lat = lat;
	}
	
	public void SetLongitude(double lon)
	{
		this.lon = lon;
	}
	
	public void SetDateTime(String datetime)
	{
		this.dateTime = datetime;
	}
	
	public ArrayList<String> GetData()
	{
		data.set(0, title);
		data.set(1, String.format("Width = %s Height = %s", width, height));
		data.set(2, dateTime);
		data.set(3, String.format("Lat = %f Long = %f", lat, lon));
		processPlace(place);
	
		return data;
	}
	
	private void processPlace(String places)
	{
		String[] placeArray = places.split("\\|");
		
		// Get rid of Old Places if Any
	    while(data.size() > 4)
	    {
	    	data.remove(data.size()-1);
	    }
		
		for(String place : placeArray)
		{
			data.add(place);
		}
	}
}
