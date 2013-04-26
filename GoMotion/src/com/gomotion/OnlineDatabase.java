package com.gomotion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import com.gomotion.HomeScreen.ExerciseType;
import com.gomotion.HomeScreen.LeaderboardType;

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

	static public LinkedList<BodyWeightExercise> getBodyWeightExercises(HashMap<String, FacebookUser> friends, int num) {
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

			String query = "SELECT * FROM bodyweight b WHERE "
					+ whereClause + " ORDER BY DBTimestamp desc" + " LIMIT " + num + ";";

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


	static public LinkedList<CardioExercise> getCardioExercises(HashMap<String, FacebookUser> friends, int num) {
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

			String query = "SELECT * FROM cardio c WHERE "
					+ whereClause + " ORDER BY DBTimestamp desc " + " LIMIT " + num + ";";

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

	public static LinkedList<FacebookUser> getBodyWeightExercisesLeaderboard(HashMap<String, FacebookUser> friends, int postsToShow,
			LeaderboardType type, ExerciseType exerciseType)
			{
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();

			System.out.println("Creating body weight statement");

			String whereClause = "";
			if(friends.size() == 0)
				return null;
			else
			{
				Iterator<String> i = friends.keySet().iterator();
				while(i.hasNext())
					whereClause += "user = \"" + i.next() + "\" OR ";
				whereClause = whereClause.substring(0, whereClause.length() - 4);
			}		

			int num = postsToShow;

			int exerciseNum = 0;
			switch(exerciseType)
			{
				case PUSHUPS:
					exerciseNum = 0;
					break;
				case SITUPS:
					exerciseNum = 1;
					break;
			}

			String query = "";
			ResultSet result;
			LinkedList<FacebookUser >resultSet = new LinkedList<FacebookUser>();

			query = "SELECT user, SUM(Sets*Reps) as totalReps FROM (SELECT * FROM bodyweight WHERE " + whereClause + ") c WHERE ExerciseType=" + exerciseNum + " GROUP BY user ORDER BY totalReps DESC LIMIT " + num + ";";
			result = s.executeQuery(query);	

			int n = 0;
			while(result.next())
			{
				n++;
				FacebookUser user = friends.get(result.getString(1));
				user.setData(result.getInt(2));
				user.setNum(n);
				resultSet.add(user);
			}							

			connection.close();
			return resultSet;
		} catch (SQLException e) {
			return null;
		}
			}

	public static LinkedList<FacebookUser> getCardioExercisesLeaderboard(HashMap<String, FacebookUser> friends, int postsToShow,
			LeaderboardType type, ExerciseType exerciseType)
		{
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
						whereClause += "user = \"" + i.next() + "\" OR ";
					whereClause = whereClause.substring(0, whereClause.length() - 4);
				}		

				int num = postsToShow;

				int exerciseNum = 0;
				switch(exerciseType)
				{
					case WALK:
						exerciseNum = 0;
						break;
					case RUN:
						exerciseNum = 1;
						break;
					case CYCLE:
						exerciseNum = 2;
						break;
				}

				String query = "";
				ResultSet result;
				LinkedList<FacebookUser> resultSet = new LinkedList<FacebookUser>();
				switch(type)
				{
					case DISTANCE:
						query = "SELECT user, SUM(Distance) as totalDistance FROM (SELECT * FROM cardio WHERE " + whereClause + ") c WHERE ExerciseType=" 
								+ exerciseNum + " GROUP BY user ORDER BY totalDistance DESC LIMIT " + num + ";";
						
						result = s.executeQuery(query);	

						int n = 0;
						while(result.next())
						{
							n++;
							FacebookUser user = friends.get(result.getString(1));
							user.setData(result.getInt(2));
							user.setNum(n);

							resultSet.add(user);
						}								
						break;
					case SPEED:
						query = "SELECT user, AVG(Distance/TimeLength) as averageSpeed FROM (SELECT * FROM cardio WHERE " + whereClause + ") c WHERE ExerciseType=" + exerciseNum
							+ " GROUP BY user ORDER BY averageSpeed DESC LIMIT " + num + ";";
						
						result = s.executeQuery(query);	

						while(result.next())
						{
							FacebookUser user = friends.get(result.getString(1));
							resultSet.add(user);
						}

						break;
				}

				connection.close();
				return resultSet;
			} catch (SQLException e) {
				return null;
			}
		}
	}