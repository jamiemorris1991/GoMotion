package com.example.gomotion;

public class BodyWeightExcercise extends Excercise
{
    public enum BodyweightType{PUSHUPS, SITUPS, DIPS};
	
	private int reps;
	private int sets;
	private BodyweightType type;
	
	
	public BodyWeightExcercise(int reps, int sets, BodyweightType type)
	{
		this.reps = reps;
		this.sets = sets;
		this.type = type;
	}

	public BodyWeightExcercise(Integer ID, int timeStamp, int sets,
			int reps, BodyweightType type)
	{
		this.ID = ID;
		this.timeStamp = timeStamp;
		
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


	@Override
	protected boolean saveToDatabase()
	{
		return Main.user.getDatabase().add(this);
	}
	
	
}
