package realImage;

import java.lang.Math;

public class GeoMath {

    double R = 6371000;         // R is earth’s radius in meters (mean radius = 6,371km)
	double travelRange = 5000; 	// Distance in meters
	double lat; 
	double lon;
	double lat2;
	double lon2;
	
	public GeoMath(double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
	}
	
	/* Calculates a Point Based on Bearing and Distance
	 * 
	 * b - double bearing
	 * */
	public void CalculateDestinationPoint(double b, double d)
	{
		double latRad = Math.toRadians(lat);
		double lonRad = Math.toRadians(lon);
		
		lat2 = Math.asin(Math.sin(latRad)*Math.cos(d/R)) + Math.cos(latRad)*Math.sin(d/R)*Math.cos(b);
		lon2 = lonRad + Math.atan2(Math.sin(b)*Math.sin(d/R)*Math.cos(latRad), Math.cos(d/R)-Math.sin(latRad)*Math.sin(lat2));
		lon2 = (lon2+3*Math.PI)% (2*Math.PI) - Math.PI; // Normalize to -180..+180°
		
		lat2 = Math.toDegrees(lat2);
		lon2 = Math.toDegrees(lon2);
	}
	
	public void SetTravelDistanceMaxRange(double dist)
	{
		this.travelRange = dist;
	}
	
	private double GetRandomBearing()
	{
		int intBearing = (int)(Math.random()*360);
		return (double) intBearing;
	}
	
	private double GetRandomTravelDistance()
	{
		return Math.random()*travelRange;
	}
	
	public void CalculateRandomDestinationPoint()
	{
		CalculateDestinationPoint(GetRandomBearing(), GetRandomTravelDistance());
	}
	
}
