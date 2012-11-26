package com.example.gomotion;

import java.util.LinkedList;

public class User
{
	private String nick;
	private int facebookID;
	private Settings settings;
	private LinkedList<Exercise> exerciseList;

	public User()
	{
		
	}
	
	public User(String nick)
	{
		this.nick = nick;
		this.settings = new Settings();
		this.exerciseList = new LinkedList<Exercise>();
	}
	
	
}
