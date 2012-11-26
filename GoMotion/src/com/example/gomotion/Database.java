package com.example.gomotion;

import java.sql.*;


public class Database
{
	static private final String connectionString
	= "jdbc:sqlserver://homepages.ncl.ac.uk;user=t2015t12;password=Vary|Tan";
	static private Connection connection;
	
	static public void init()
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
}
