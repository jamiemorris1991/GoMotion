package com.example.gomotion;

import java.io.*;
import java.util.*;

public class Cache
{
	static final String CACHE_PATH = "cache.txt";
	
	static public void append(Excercise excercise)
	{
		//Open Cache File To Append
		OutputStream writer = null;
		try
		{
			writer = new FileOutputStream(CACHE_PATH, true);
		} catch (FileNotFoundException e)
		{
			//FileNotFound never actually occurs - file is created
		}
		
		//Write excercise
		excercise.writeToFile(writer);
		
		//Close Stream
		try
		{
			writer.close();
		} catch (IOException e)
		{
		}
	}
	
	static public void tryEmpty()
	{
		//Initialise list
		LinkedList<Excercise> cachedExcercises = new LinkedList<Excercise>();
		
		//Open cache file
		Scanner in;
		try
		{
			in = new Scanner(new FileInputStream(CACHE_PATH));
		} catch (FileNotFoundException e)
		{
			//File doesn't exist or isn't accessible somehow
			return;
		}
		
		//Get All Excercises From Cache
		while(in.hasNext())
		{
			cachedExcercises.add(readExcercise(in));
		}
		in.close();
		
		//Clear Cache
		new File(CACHE_PATH).delete();
		
		//Try to save to database
		ListIterator<Excercise> iterator = cachedExcercises.listIterator();
		while(iterator.hasNext())
		{
			iterator.next().saveToDatabase();
		}
	}
	
	private static Excercise readExcercise(Scanner in)
	{
		
		
	}
	
}
