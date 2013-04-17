package com.gomotion;

import java.sql.*;

public class OnlineDatabase {
	static private final String url = "jdbc:mysql://hexdex.net:3306/hexdexne_csc2015";
	static private final String user = "hexdexne_csc2015";
	static private final String password = "g0m0t10n";
	static private Connection connection;

	static public Connection getConnection() throws SQLException {
		System.out.println("Connecting to database...");

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
					+ exercise.getUserID() + ","
					+ exercise.getSets() + ","
					+ exercise.getReps() + ","
					+ "\"" + exercise.getName().replace("\"", "\\\"") + "\","
					+ exercise.getType().ordinal() + ");");
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
					+ exercise.getTimeStamp() + "," + exercise.getUserID()
					+ "," + exercise.getTimeLength() + ","
					+ exercise.getDistance() + ","
					+ "\"" + exercise.getMapURL().replace("\"", "\\\"") + "\","
					+ exercise.getType().ordinal() + ");");
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	static public BodyWeightExercise getFirstBodyWeightExercise(int userID) {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();

			ResultSet result = s
					.executeQuery("SELECT FROM bodyweight b WHERE b.user = "
							+ userID + " LIMIT 1;");

			if (!result.next())
				return null;

			return new BodyWeightExercise(result);
		} catch (SQLException e) {
			return null;
		}
	}

	static public CardioExercise getFirstCardioExercise(int userID) {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();

			ResultSet result = s
					.executeQuery("SELECT FROM cardio c WHERE c.user = "
							+ userID + " LIMIT 1;");

			if (!result.next())
				return null;

			return new CardioExercise(result);
		} catch (SQLException e) {
			return null;
		}
	}

}