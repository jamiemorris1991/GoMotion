package com.example.gomotion;

import java.sql.*;


public class OnlineDatabase
{
	static private final String connectionString
	= "jdbc:sqlserver://homepages.ncl.ac.uk;user=t2015t12;password=Vary|Tan";
	static private Connection connection;
	
	public Connection getConnection() throws SQLException
	{
		return connection =
				DriverManager.getConnection(connectionString);
	}

	public boolean add(BodyWeightExercise exercise) throws SQLException
	{
		try
		{
		//Try to get a connection
		Connection connection = getConnection();
		
		Statement s = connection.createStatement();
		s.executeQuery("INSERT INTO bodyweight VALUES ("
				+ "null," 
				+ exercise.getTimeStamp() + ","
				+ exercise.getSets() + ","
				+ exercise.getReps() + ","
				+ exercise.getType().ordinal() + ","
				+ exercise.getName() + ");");
		}
		catch(SQLException e)
		{
			return false;
		}
		return true;
	}

	public boolean add(CardioExercise exercise)
	{
		try
		{
		//Try to get a connection
		Connection connection = getConnection();
		
		Statement s = connection.createStatement();
		s.executeQuery("INSERT INTO cardio VALUES ("
				+ "null," 
				+ exercise.getTimeStamp() + ","
				+ exercise.getTimeLength() + ","
				+ exercise.getDistance() + ","
				+ exercise.getType().ordinal() + ");");
		}
		catch(SQLException e)
		{
			return false;
		}
		return true;
	}

	public BodyWeightExercise getBodyWeightExercise(int id)
	{
		return null;
	}

	public CardioExercise getCardioExercise(int id)
	{
		return null;
	}


}
