package com.example.gomotion;

public class User
{
	private Integer facebookID;
	private String name;
	
	private Database database;
	public Database getDatabase()
	{
		if(database != null)
			return database;
		
		if(isOnline())
		{
			OnlineDatabase db = new OnlineDatabase();
			db.init();
			database = db;
		}
		else
		{
			OfflineDatabase db = new OfflineDatabase(Main.getContext());
			database = db;
		}
		return database;
	}
	
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
