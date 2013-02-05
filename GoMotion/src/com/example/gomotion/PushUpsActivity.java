package com.example.gomotion;

import java.util.LinkedList;

import com.example.gomotion.BodyWeightExercise.BodyWeightType;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class PushUpsActivity extends Activity
{
	
	BodyWeightExercise exercise;
	private int setCount;
	private int repCount;
	
	private LinkedList<Integer> setValues = new LinkedList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_ups);
        
        exercise = new BodyWeightExercise();
        exercise.setTimestamp(System.currentTimeMillis());
        exercise.setType(BodyWeightType.PUSHUPS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_push_ups, menu);
        return true;
    }
    
    public void incrementRep(View view)
    {
    	TextView reps = (TextView) findViewById(R.id.repCount);
    	int repCount =  Integer.parseInt( reps.getText().toString() );
    	repCount++;
    	reps.setText(String.valueOf(repCount));
    }
    
    public void incrementSet(View view)
    {
    	// Get set value
    	TextView sets = (TextView) findViewById(R.id.setCount);
    	setCount = Integer.parseInt( sets.getText().toString() );
    	
    	// Get rep value
    	TextView reps = (TextView) findViewById(R.id.repCount);
    	repCount = Integer.parseInt(reps.getText().toString());
    	
    	// Increment set value and store rep count
    	if(repCount > 0)
    	{
	    	setValues.add( repCount );	
	    	reps.setText(String.valueOf(0));

	    	setCount++;
	    	sets.setText(String.valueOf(setCount));	    	
    	}
    }
    
    public void finishExercise(View view)
    {
    	exercise.setSets(setCount);
    	exercise.setReps(repCount);
    	
    	DatabaseHandler db = new DatabaseHandler(this);    	
    	db.addBodyWeightExercise(exercise);
    	db.close();
    }
}
