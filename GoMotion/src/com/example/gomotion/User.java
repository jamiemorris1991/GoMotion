package com.example.gomotion;

import java.util.LinkedList;

public class User
{
	//Online Users
	private Integer facebookID = null;
	
	//Offline Users
	private List<Excercise> excercises = null;
	
	//All Users
	private String firstName, lastName;
	
	public boolean isOnline()
	{
		return excercises == null;
	}

	//Create Offline User
	public User(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	//Create Online User
	public User(int facebookID)
	{
		this.facebookID = facebookID;
		//TODO: Pull firstName and secondName from Facebook
	}
	
	//Convert To Online User
	public void convertToOnline(int facebookID)
	{
		facebookID = facebookID;
		//TODO: Set firstName and lastName from Facebook
		
		//Add All Excercises To The Database
		ListIterator<Excercise> iterator = excercises.listIterator();
		while(iterator.hasNext())
		{
			excercise.setUser(facebookID);
			excercise.tryUpload();
		}
		
		//Remove Excercise List
		excercises = null;
	}
	
	public void saveToFile(String path)
	{
		/* TODO: Save to file
		** File structure:
		** isOnline()
		** EITHER
		** facebookID
		** OR
		** firstName
		** lastName
		** list of excercises until end of file
		*/
	}
	
	static public void readFromFile(String path)
	{
		//TODO: Open file, read if online, process accordingly
	}
}
