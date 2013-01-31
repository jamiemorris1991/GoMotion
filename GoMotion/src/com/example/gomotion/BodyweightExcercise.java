package com.example.gomotion;

public class BodyweightExcercise extends Excercise
{
    public enum BodyweightType{PUSHUPS, SITUPS, DIPS};
	
	private int reps;
	private int sets;
	private BodyweightType type;
	
	
	public BodyweightExcercise(int reps, int sets, BodyweightType type)
	{
		this.reps = reps;
		this.sets = sets;
		this.type = type;
	}

	public int getReps()
	{
		return reps;
	}

	public void setReps(int reps)
	{
		this.reps = reps;
	}

	public int getSets()
	{
		return sets;
	}

	public void setSets(int sets)
	{
		this.sets = sets;
	}

	public BodyweightType getType()
	{
		return type;
	}

	public void setType(BodyweightType type)
	{
		this.type = type;
	}
	
	
}
