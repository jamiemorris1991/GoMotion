package com.example.gomotion;
import java.util.*;
import android.location.Location;
import java.sql.*;

import com.example.gomotion.BodyWeightExercise.BodyWeightType;

//Class to store running/walking/cycling sessions
public class CardioExercise extends Exercise
{
	//Enumerates the types or session
    public enum CardioType{WALK, RUN, CYCLE};

    //Trip specific variables
    private int distance;    
    private int timeLength;
    private CardioType type;
    private LinkedList<Location> waypoints;

    public CardioExercise()
    {
    	
    }
    
    public CardioExercise(ResultSet r) throws SQLException
    {
		//ID
		ID = r.getInt(0);
		//timeStamp
		timeStamp = r.getInt(1);
		//user
		userID = r.getInt(2);
		//timeLength
		timeLength = r.getInt(3);
		//distance
		distance = r.getInt(4);
		//type
		type = CardioType.values()[r.getInt(5)];
    }
    
    public CardioExercise(int distance, int timeLength, CardioType type, LinkedList<Location> waypoints)
    {
    	this.timeLength = timeLength;
    	this.type = type;
    	this.distance = distance;
    	this.waypoints = waypoints;
    }

	public CardioExercise(int ID, long timeStamp, int distance, int timeLength, CardioType type)
	{
		this.ID = ID;
		this.timeStamp = timeStamp;
		
		this.timeLength = timeLength;
    	this.type = type;
    	this.distance = distance;
	}


	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
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