package com.example.gomotion;


public abstract class Exercise
{
	//Online Exercises
	protected Integer ID;
	protected Integer userID;
	protected boolean shouldShare;
	
	//All exercises
	protected long timeStamp;
	
	//If id isn't null then the record has been stored on the server
	public boolean isStored()
	{
		return ID != null;
	}
	
	//Online or offline user functions
	public void setOnline(int userID)
	{
		this.userID = userID;
	}
	public boolean isOnline()
	{
		return userID != null;
	}
	
	//Getters And Setters
	public Integer getID()
	{
		return ID;
	}
	public void setID(Integer iD)
	{
		ID = iD;
	}
	public Integer getUserID()
	{
		return userID;
	}
	public void setUserID(Integer userID)
	{
		this.userID = userID;
	}
	public long getTimeStamp()
	{
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

}
