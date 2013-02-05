package com.example.gomotion;

//Class to store running/walking/cycling sessions
public class CardioExercise
{
	//Enumerates the types or session
    public enum CardioType{WALK, RUN, CYCLE};

    //Trip specific variables
    private int id;
    private long timestamp;
    private double distance; 
    private int timeLength;
    private CardioType type;

    public CardioExercise()
    {
    	
    }
    
    public CardioExercise(long timestamp, double distance, int timeLength, CardioType type)
    {
    	this.timestamp = timestamp;
    	this.distance = distance;
    	this.timeLength = timeLength;
    	this.type = type;
    }
    
    public CardioExercise(int id, long timestamp, double distance, int timeLength, CardioType type)
    {
    	this.id = id;
    	this.timestamp = timestamp;
    	this.distance = distance;
    	this.timeLength = timeLength;
    	this.type = type;
    }

	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}

	public long getTimestamp()
	{
		return timestamp;
	}
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public int getTimeLength()
	{
		return timeLength;
	}
	public void setTimeLength(int timeLength)
	{
		this.timeLength = timeLength;
	}

	public double getDistance()
	{
		return distance;
	}
	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public CardioType getType()
	{
		return type;
	}
	public void setType(CardioType type)
	{
		this.type = type;
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