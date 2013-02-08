package com.example.gomotion;

public class BodyWeightExercise extends Exercise
{
    public enum BodyWeightType{PUSHUPS, SITUPS, DIPS};
	
	private int sets;
	private int reps;
	private BodyWeightType type;
	
	
	public BodyWeightExercise(int sets, int reps, BodyWeightType type)
	{
		this.sets = sets;
		this.reps = reps;
		this.type = type;
	}

	public BodyWeightExercise(int ID, long timeStamp, int sets,
			int reps, BodyWeightType type)
	{
		this.ID = ID;
		this.timeStamp = timeStamp;
		
		this.sets = sets;
		this.reps = reps;
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

	public BodyWeightType getType()
	{
		return type;
	}

	public void setType(BodyWeightType type)
	{
		this.type = type;
	}


	@Override
	protected boolean saveToDatabase()
	{
		return Main.user.getDatabase().add(this);
	}
	
	
}
