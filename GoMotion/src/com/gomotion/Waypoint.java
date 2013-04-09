package com.gomotion;

public class Waypoint 
{
	private double longitude;
	private double latitude;
	
	public Waypoint(double latitude, double longitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() 
	{
		return longitude;
	}
	public void setLongitude(double longitude) 
	{
		this.longitude = longitude;
	}

	public double getLatitude() 
	{
		return latitude;
	}
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
}
