package com.example.gomotion;

import java.sql.*;


public class OnlineDatabase implements Database
{
	static private final String connectionString
	= "jdbc:sqlserver://homepages.ncl.ac.uk;user=t2015t12;password=Vary|Tan";
	static private Connection connection;
	
	public void init()
	{
		try
		{
			connection = DriverManager.getConnection(connectionString);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean add(BodyWeightExercise exercise)
	{
		return false;
	}

	public boolean add(CardioExercise exercise)
	{
		return false;
	}

	public BodyWeightExercise getBodyWeightExercise(int id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public CardioExercise getCardioExercise(int id)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
