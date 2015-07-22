package realImage;

import java.lang.Math;

public class GeoMath {
	
	private double R = 6371000;         // R is earth’s radius in meters (mean radius = 6,371km)
	private double travelRange = 5000; 	// Distance in meters
	private double lat; 
	private double lon;
	private double lat2;
	private double lon2;
	
	/**
	 * GeoMath Constructor. The object is used to calculate a second point based
	 * on a given coordinate (Latitude/Longitude). 
	 * 
	 * @param lat Origin Latitude. Decimal representation.
	 * @param lon Origin Longitude. Decimal representation.
	 */
	public GeoMath(double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
		this.lat2 = -500;
		this.lon2 = -500;
	}
	
    /**
     * Calculates a point Latitude and Coordinate from the Origin Point set on
     * object construction, based on distance (meters) and bearing (direction).
     * 
     * @param b Bearing is the direction of motion. 
     * @param d Distance traveled from the point of origin
     */
	public void CalculateDestinationPoint(double b, double d)
	{
		/**
		 * Reference: 
		 * Vennes, Chris. "Calculate distance and bearing between two Latitude/Longitude 
		 * points using haversine formula in JavaScript."
		 * June 2014. Available @ http://www.movable-type.co.uk/scripts/latlong.html
		 * */
		
		double latRad = Math.toRadians(lat);
		double lonRad = Math.toRadians(lon);
		
		lat2 = Math.asin(Math.sin(latRad)*Math.cos(d/R)) + Math.cos(latRad)*Math.sin(d/R)*Math.cos(b);
		lon2 = lonRad + Math.atan2(Math.sin(b)*Math.sin(d/R)*Math.cos(latRad), Math.cos(d/R)-Math.sin(latRad)*Math.sin(lat2));
		lon2 = (lon2+3*Math.PI)% (2*Math.PI) - Math.PI; // Normalize to -180/+180°
		
		lat2 = Math.toDegrees(lat2);
		lon2 = Math.toDegrees(lon2);
	}
	
	public double GetLatitude()
	{
		return this.lat2;
	}
	
	public double GetLongitude()
	{
		return this.lon2;
	}
	
	/**
	 * Sets the object travel radius.
	 * 
	 * @param dist
	 */
	public void SetTravelDistanceMaxRange(double dist)
	{
		this.travelRange = dist;
	}
	
	/**
	 * Generates a random bearing. Bearing is the direction of motion of a travel.
	 * Bearing is measured in degrees. The range is 0 - 360. 0 is North. 90 East.
	 * 180 South. 270 West.
	 * 
	 * @return Random value for travel bearing
	 */
	private double GetRandomBearing()
	{
		int intBearing = (int)(Math.random()*360);
		return (double) intBearing;
	}
	
	/**
	 * Generates a random travel distance. Used to calculate a random travel position
	 * based on the origin coordinate.
	 * 
	 * @return Travel distance in meters
	 */
	private double GetRandomTravelDistance()
	{
		return Math.random()*travelRange;
	}
	
	/**
	 * Calculates a new destination point from the origin, by using random bearing
	 * and distance values for the calculation.
	 */
	public void CalculateRandomDestinationPoint()
	{
		CalculateDestinationPoint(GetRandomBearing(), GetRandomTravelDistance());
	}
	
}
