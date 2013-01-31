package com.example.gomotion;
import java.util.*;
import android.content.*;
import android.location.*;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

//Class to store running/walking/cycling sessions
public class CardioExcercise extends Excercise
{
	//Enumerates the types or session
    public enum CardioType{WALK, RUN, CYCLE};

    //Trip specific variables
    private int timeLength;
    private CardioType type;
    private double distance;    
    private LinkedList<Location> waypoints;

    public CardioExcercise()
    {
    	
    }
    
    public CardioExcercise(int timeLength, CardioType type, double distance, LinkedList<Location> waypoints)
    {
    	this.timeLength = timeLength;
    	this.type = type;
    	this.distance = distance;
    	this.waypoints = waypoints;
    }

	public int getTimeLength()
	{
		return timeLength;
	}

	public void setTimeLength(int timeLength)
	{
		this.timeLength = timeLength;
	}

	public CardioType getType()
	{
		return type;
	}

	public void setType(CardioType type)
	{
		this.type = type;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public LinkedList<Location> getWaypoints()
	{
		return waypoints;
	}

	public void setWaypoints(LinkedList<Location> waypoints)
	{
		this.waypoints = waypoints;
	}
    
    
    
//    //Method to add a waypoint to wayPoints, gotten from android.location
//    static private LocationManager locationManager;
//    public void poll()
//    {
//    	if(locationManager == null)
//    		locationManager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    }
//    
//    //TODO: Getters and setters
}