package com.example.gomotion;

import java.util.LinkedList;

import com.example.gomotion.BodyWeightExercise.BodyWeightType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private Button repButton; 
	private TextView timerLabel;
	
	private int initialSetCount;
	private int initialRepCount;
	
	//private LinkedList<Integer> setValues = new LinkedList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_ups);
     	getActionBar().setDisplayHomeAsUpEnabled(true);	
        
        Intent intent = getIntent();
        initialSetCount = intent.getIntExtra(BodyWeightSettingsDialogFragment.SET_CHOICE, 1);
        initialRepCount = intent.getIntExtra(BodyWeightSettingsDialogFragment.REP_CHOICE, 1);
        restTime = intent.getIntExtra(BodyWeightSettingsDialogFragment.REST_TIME, 10000) * 1000; // convert seconds to milliseconds
       
        setCount = initialSetCount;
        repCount = initialRepCount;
        
        setView = (TextView) findViewById(R.id.set_count);
        repButton = (Button) findViewById(R.id.rep_button);
        timerLabel = (TextView) findViewById(R.id.textLabel);
        
        setView.setText(String.valueOf(setCount));
        
        exercise = new BodyWeightExercise(initialSetCount, initialRepCount, BodyWeightType.PUSHUPS);
        exercise.setTimeStamp(System.currentTimeMillis());
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case android.R.id.home:
				confirmExit();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    public void doRep(View view)
    {
    	if(repCount > 1)
    	{
	    	repCount--;
        	repButton.setText(String.valueOf(repCount) + "/" + String.valueOf(initialRepCount));
        	timerLabel.setText("");
        	view.setBackgroundColor(getResources().getColor(R.color.goBlue));
    	}
    	else if(setCount == 1 && repCount == 1) // finished
    	{
    		finishExercise();
    	}
    	else // finish set
    	{
        	setCount--;
        	repCount = initialRepCount;
        	timerLabel.setText("Timer:");
        	
        	view.setBackgroundColor(getResources().getColor(R.color.timerBackground));
        	
        	setView.setText(String.valueOf(setCount));
    		
    		repButton.setClickable(false);
    		countdown = (restTime/1000) + 1;
    		
    		CountDownTimer timer = new CountDownTimer(restTime + 100, 1000) 
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
					timerLabel.setText("Timer Complete:");
		        	repButton.setText("Touch Me!");
		        	repButton.setClickable(true);
				}   			
    		};
    		
    		timer.start();
    	}	
    }
    
	@Override
	public void onBackPressed() 
	{
		confirmExit();
	}
    
	private void confirmExit()
	{
		new AlertDialog.Builder(this)
		.setTitle("Warning")
		.setMessage("Are you sure you wish to exit?\n\nCurrent progress will be lost.")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		})
		.setNegativeButton("No", null)
		.show();		
	}
    
    public void finishExercise()
    {	
		OfflineDatabase db = new OfflineDatabase(this);    	
		db.add(exercise);
		db.close();
		

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("GoMotion")
			.setTitle("Finished")
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
