package com.example.gomotion;

public class User
{
	private Integer facebookID;
	private String name;
	
	public boolean isOnline()
	{
		return facebookID != null;
	}

	//Create Offline User
	public User(String name)
	{
		facebookID = null;
		this.name = name;
	}
	
	//Create Online User
	public User(int facebookID)
	{
		this.facebookID = facebookID;
		//TODO: Pull name from Facebook
	}
}
