package com.example.gomotion;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BodyWeightExercise
{
    public enum BodyWeightType{PUSHUPS, SITUPS, DIPS};
	
    private int id;
	private long timestamp;
	private int sets;
	private int reps;
	private BodyWeightType type;
	
	public BodyWeightExercise()
	{
		
	}
	
	public BodyWeightExercise(long timestamp, int sets, int reps, BodyWeightType type)
	{
		this.timestamp = timestamp;
		this.sets = sets;
		this.reps = reps;
		this.type = type;
	}
	
	public BodyWeightExercise(int id, long timestamp, int sets, int reps, BodyWeightType type)
	{
		this.id = id;
		this.timestamp = timestamp;
		this.sets = sets;
		this.reps = reps;
		this.type = type;
	}

	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}

	public long getTimestamp()
	{
		return timestamp;
	}
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public int getSets()
	{
		return sets;
	}
	public void setSets(int sets)
	{
		this.sets = sets;
	}

	public int getReps()
	{
		return reps;
	}
	public void setReps(int reps)
	{
		this.reps = reps;
	}

	public BodyWeightType getType()
	{
		return type;
	}
	public void setType(BodyWeightType type)
	{
		this.type = type;
	}
	
	public String toString()
	{
		return "(" + timestamp + ", " + sets  + ", " + reps  + ", " + type + ")";
	}
}
