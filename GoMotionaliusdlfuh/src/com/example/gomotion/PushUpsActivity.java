package com.example.gomotion;

import java.util.LinkedList;

import com.example.gomotion.BodyWeightExercise.BodyWeightType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PushUpsActivity extends Activity
{
	private int countdown;
	private int restTime;
	
	BodyWeightExercise exercise;
	private int setCount;
	private int repCount;
	
	private TextView setView;
	private TextView repView;
	private Button repButton; 
	
	private int initialSetCount;
	private int initialRepCount;
	
	//private LinkedList<Integer> setValues = new LinkedList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_ups);
        
        Intent intent = getIntent();
        initialSetCount = intent.getIntExtra(BodyWeightSettingsDialogFragment.SET_CHOICE, 1);
        initialRepCount = intent.getIntExtra(BodyWeightSettingsDialogFragment.REP_CHOICE, 1);
        restTime = intent.getIntExtra(BodyWeightSettingsDialogFragment.REST_TIME, 10000) * 1000; // convert seconds to milliseconds
       
        setCount = initialSetCount;
        repCount = initialRepCount;
        
        setView = (TextView) findViewById(R.id.set_count);
        repView = (TextView) findViewById(R.id.rep_count);
        repButton = (Button) findViewById(R.id.rep_button);
        
        setView.setText(String.valueOf(setCount));
        repView.setText(String.valueOf(repCount));
        
        exercise = new BodyWeightExercise();
        exercise.setTimestamp(System.currentTimeMillis());
       	exercise.setSets(initialSetCount);
    	exercise.setReps(initialRepCount);
        exercise.setType(BodyWeightType.PUSHUPS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_push_ups, menu);
        return true;
    }
    
    public void doRep(View view)
    {
    	if(repCount > 1)
    	{
	    	repCount--;
        	repView.setText(String.valueOf(repCount));
    	}
    	else if(setCount == 1 && repCount == 1) // finished
    	{
    		finishExercise();
    	}
    	else // finish set
    	{
        	setCount--;
        	repCount = initialRepCount;
        	
        	setView.setText(String.valueOf(setCount));
        	repView.setText(String.valueOf(repCount));
    		
    		repButton.setClickable(false);
    		countdown = (restTime/1000) + 1;
    		
    		CountDownTimer timer = new CountDownTimer(restTime + 1000, 1000) 
    		{
				@Override
				public void onTick(long millisUntilFinished)
				{
					countdown--;
					repButton.setText(String.valueOf(countdown));
				} 
				@Override
				public void onFinish()
				{
		        	repButton.setText("Touch Me!");
		        	repButton.setClickable(true);
				}   			
    		};
    		
    		timer.start();
    	}	
    }
    
    
//    public void incrementRep(View view)
//    {
//    	TextView reps = (TextView) findViewById(R.id.repCount);
//    	int repCount =  Integer.parseInt( reps.getText().toString() );
//    	repCount++;
//    	reps.setText(String.valueOf(repCount));
//    }
//    
//    public void incrementSet(View view)
//    {
//    	// Get set value
//    	TextView sets = (TextView) findViewById(R.id.setCount);
//    	setCount = Integer.parseInt( sets.getText().toString() );
//    	
//    	// Get rep value
//    	TextView reps = (TextView) findViewById(R.id.repCount);
//    	repCount = Integer.parseInt(reps.getText().toString());
//    	
//    	// Increment set value and store rep count
//    	if(repCount > 0)
//    	{
//	    	setValues.add( repCount );	
//	    	reps.setText(String.valueOf(0));
//
//	    	setCount++;
//	    	sets.setText(String.valueOf(setCount));	    	
//    	}
//    }
    
    public void finishExercise()
    {	
		OfflineDatabase db = new OfflineDatabase(this);    	
		db.add(exercise);
		db.close();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("GoMotion")
			.setMessage("Well done, you have completed this exercise!")
			.setCancelable(false)
			.setNegativeButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
    }
}
