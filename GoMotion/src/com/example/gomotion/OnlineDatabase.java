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
				+ exercise.getUserID() + ","
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
				+ exercise.getUserID() + ","
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

	public BodyWeightExercise getFirstBodyWeightExercise(int userID)
	{
		try
		{
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			ResultSet result = s.executeQuery("SELECT FROM bodyweight b WHERE b.user = " + userID + " LIMIT 1;");
			
			if (!result.next())
				return null;
			
			return new BodyWeightExercise(result);
		}
		catch(SQLException e)
		{
			return null;	
		}
	}

	public CardioExercise getFirstCardioExercise(int userID)
	{
		try
		{
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			ResultSet result = s.executeQuery("SELECT FROM cardio c WHERE c.user = " + userID + " LIMIT 1;");
			
			if (!result.next())
				return null;
			
			return new CardioExercise(result);
		}
		catch(SQLException e)
		{
			return null;	
		}
	}


}