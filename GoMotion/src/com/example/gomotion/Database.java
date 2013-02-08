package com.example.gomotion;

public interface Database
{
	public boolean add(BodyWeightExercise exercise);
	public boolean add(CardioExercise exercise);
	
	public BodyWeightExercise getBodyWeightExercise(int id);
	public CardioExercise getCardioExercise(int id);
}
