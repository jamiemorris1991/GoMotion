package com.gomotion;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.gomotion.HomeScreen.WallSortMode;

public class OnlineDatabase {
	static private final String url = "jdbc:mysql://hexdex.net:3306/hexdexne_csc2015";
	static private final String user = "hexdexne_csc2015";
	static private final String password = "g0m0t10n";
	static private Connection connection;

	static public Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (connection != null)
			if (!connection.isClosed())
				return connection;

		return connection = DriverManager.getConnection(url, user, password);
	}

	static public boolean add(BodyWeightExercise exercise) {
		try {
			// Try to get a connection
			getConnection();
			Statement s = connection.createStatement();			
						
			s.executeUpdate("INSERT INTO bodyweight VALUES (" + "null,"
					+ exercise.getTimeStamp() + ","
					+ "UNIX_TIMESTAMP(),"
					+ exercise.getUserID() + ","
					+ exercise.getSets() + ","
					+ exercise.getReps() + ","
					+ (exercise.getName() == null ? "null," :  "\"" + exercise.getName().replace("\"", "\\\"") + "\",")
					+ exercise.getType().ordinal() + ");");

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	static public boolean add(CardioExercise exercise) {
		try {
			// Try to get a connection
			System.out.println("Connecting...");
			Connection connection = getConnection();
			System.out.println("Connected!");
			Statement s = connection.createStatement();
			s.execute("INSERT INTO cardio VALUES (" + "null,"
					+ exercise.getTimeStamp() + ","
					+ "UNIX_TIMESTAMP(),"
					+ exercise.getUserID() + ","
					+ exercise.getTimeLength() + ","
					+ exercise.getDistance() + ","
					+ "\"" + exercise.getMapURL().replace("\"", "\\\"") + "\","
					+ exercise.getType().ordinal() + ");");
			
			connection.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	static public LinkedList<BodyWeightExercise> getBodyWeightExercises(HashMap<String, FacebookUser> friends, int num, WallSortMode wsm) {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
						
			String whereClause = "";
			if(friends.size() == 0)
				return null;
			else
			{
				Iterator<String> i = friends.keySet().iterator();
				while(i.hasNext())
					whereClause += "b.user = \"" + i.next() + "\" OR ";
				whereClause = whereClause.substring(0, whereClause.length() - 4);
			}
			
			String order = " ORDER BY ";
			switch(wsm)
			{
			case indoor:
				order += "Sets*Reps desc";
				break;
				case timeline:
				default:
					order += "DBTimestamp desc";
					break;
			}

			String query = "SELECT * FROM bodyweight b WHERE "
					+ whereClause + order + " LIMIT " + num + ";";
			
			System.out.println("SQL:: " + query);

			ResultSet result = s
					.executeQuery(query);
			
			LinkedList<BodyWeightExercise> out = new LinkedList<BodyWeightExercise>();
			

			while(result.next())
				out.add(new BodyWeightExercise(result));
			
			connection.close();
			return out;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	static public LinkedList<CardioExercise> getCardioExercises(HashMap<String, FacebookUser> friends, int num, WallSortMode wsm) {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			System.out.println("Creating cardio statement");

			String whereClause = "";
			if(friends.size() == 0)
				return null;
			else
			{
				Iterator<String> i = friends.keySet().iterator();
				while(i.hasNext())
					whereClause += "c.user = \"" + i.next() + "\" OR ";
				whereClause = whereClause.substring(0, whereClause.length() - 4);
			}		
			
			String order = "ORDER BY ";
			switch(wsm)
			{
				case outdoorDistance:
					order += "Distance desc";
				break;
				case outdoorSpeed:
					order += "Distance/TimeLength desc";
					break;
				case timeline:
				default:
					order += "DBTimestamp desc";
					break;
			}
			
			String query = "SELECT * FROM cardio c WHERE "
					+ whereClause + order + " LIMIT " + num + ";";

			ResultSet result = s
					.executeQuery(query);			

			LinkedList<CardioExercise> out = new LinkedList<CardioExercise>();

			while(result.next())
			{
				out.add(new CardioExercise(result));
			}
			
			System.out.println(out);

			connection.close();
			return out;
		} catch (SQLException e) {
			return null;
		}
	}

}