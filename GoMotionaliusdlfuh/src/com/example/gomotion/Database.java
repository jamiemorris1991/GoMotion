package com.example.gomotion;

public interface Database
{
	public boolean add(BodyWeightExcercise excercise);
	public boolean add(CardioExcercise excercise);
	
	public BodyWeightExcercise getBodyWeightExcercise(int id);
	public CardioExcercise getCardioExcercise(int id);
}
