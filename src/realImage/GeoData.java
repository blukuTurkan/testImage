package realImage;

/**
 * 
 * @author Antonio Orellana Handal
 *
 */

public class GeoData
{
    public double lat;
    public double lng;
    public String place;

    /**
     * Create an Object to hold Geographic Point data
     * 
     * @param lat Latitude. Decimal representation.
     * @param lng
     * @param place
     */
    public GeoData(double lat, double lng, String place)
    {
    	this.lat = lat;
    	this.lng = lng;
    	this.place = place;
    }
}