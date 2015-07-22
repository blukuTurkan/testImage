package realImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONObject;


public class Geocoding {

	private static String googleURI = "https://maps.googleapis.com/maps/api/geocode/json?";
	private static String googleAPIKey = "INSERT YOU GOOGLE DEV KEY HERE";
		
	private Geocoding(){}
	
	private static String PrepareStringForQuery(String place)
	{
		if(place.isEmpty() || place == null)
		{
			return " ";
		}
		
	    return place.replace(" ","+");
	}
	
	public static GeoData ResolveCoordinates(double lat, double lng)
	{
		String url = String.format("%slatlng=%f,%f&key=%s", googleURI, lat, lng, googleAPIKey);
		return  ResolveGeoDataFromGoogle(url);
	}
	
	public static GeoData ResolvePlace(String place)
	{
		if (place.isEmpty() || place == null)
		{
			return null;
		}
		
		place = PrepareStringForQuery(place);
		String url = String.format("%saddress=%s&key=%s", googleURI, place, googleAPIKey);
      
		return ResolveGeoDataFromGoogle(url);

	}
	
	public static GeoData ResolveGeoDataFromGoogle(String url)
	{
		GeoData output = null;
		try {
			
			URLConnection connection = new URL(url).openConnection();
			BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8") );
			
			String line = null;
			StringBuilder body = new StringBuilder();
			while((line = response.readLine()) != null)
			{
				body.append(line);
			}

			//build a JSON object
		    JSONObject obj = new JSONObject(body.toString());
		    if (! obj.getString("status").equals("OK"))
		        return output;
			
		    // Get the first Object of the results.
		    // This is the most specific address resolved
		    JSONArray result = obj.getJSONArray("results");
		    
		    int arraySize = result.length();
		    arraySize = arraySize > 3 ? 3 : arraySize;
		    JSONObject location = result.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
		    StringBuilder addresses = new StringBuilder();
		    
		    for(int x=0; x < arraySize; x++)
		    {
		    	addresses.append(result.getJSONObject(x).getString("formatted_address")+"|");
		    }
		    output = new GeoData(location.getDouble("lat"), location.getDouble("lng"), addresses.substring(0, addresses.length()-1));
		    return output;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		    return output;
		}
		catch (org.json.JSONException e)
		{
			System.out.println("Error Processing JSON Response Body");
		    e.printStackTrace();	
		    return output;
		}
	}
	
}
