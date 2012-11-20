package com.example.gomotion;

import java.util.LinkedList;

public class User
{
	private String nickname;
	private int facebookID;
	private Settings settings;
	private LinkedList<Exercise> exercises;

	public User()
	{
		
	}
	
	public User(String nick)
	{
		this.nickname = nick;
		this.settings = new Settings();
		this.exercises = new LinkedList<Exercise>();
	}
	
	
}
