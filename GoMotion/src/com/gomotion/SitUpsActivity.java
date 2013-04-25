package com.gomotion;

import com.gomotion.BodyWeightExercise.BodyWeightType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SitUpsActivity extends Activity
{
	private int countdown;
	private int restTime;
	
	BodyWeightExercise exercise;
	private int setCount;
	private int repCount;
	
	private TextView setView;
	private Button repButton; 
	private ImageView stopWatch;
	
	private int initialSetCount;
	private int initialRepCount;
	
	private SensorManager sensorManager;
	private Sensor sensor;
	private SensorEventListener listener;
	
	private boolean down;
	private boolean up;
	private boolean waiting;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sit_ups);
		setupActionBar();
		
		 Intent intent = getIntent();
        initialSetCount = intent.getIntExtra(BodyWeightSettingsDialogFragment.SET_CHOICE, 1);
        initialRepCount = intent.getIntExtra(BodyWeightSettingsDialogFragment.REP_CHOICE, 1);
        restTime = intent.getIntExtra(BodyWeightSettingsDialogFragment.REST_TIME, 10000) * 1000; // convert seconds to milliseconds
       
        setCount = initialSetCount;
        repCount = initialRepCount;
        
        setView = (TextView) findViewById(R.id.sit_set_count);
        repButton = (Button) findViewById(R.id.sit_rep_button);
        
        stopWatch = (ImageView) findViewById(R.id.sit_imgStopwatch);
        
        setView.setText(String.valueOf(setCount));
        
        exercise = new BodyWeightExercise(initialSetCount, initialRepCount, BodyWeightType.SITUPS);
        exercise.setTimeStamp(System.currentTimeMillis());
		
		down = false;
		up = false;
						
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		listener = new SensorEventListener() {

			public void onAccuracyChanged(Sensor sensor, int accuracy) {				
			}

			public void onSensorChanged(SensorEvent event)
			{
				//System.out.println(event.values[0]);
				if(!waiting)
				{
					if(!down && event.values[0] < -8)
					{
						down = true;
					}
					else if(down && !up && event.values[0] > -2)
					{
						up = true;
					}
					
					if(up && down)
					{
						doRep();
						down = up = false;
					}
				}
			}			
		};
		
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	private void setupActionBar() 
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
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

	@Override
	protected void onDestroy() {
		sensorManager.unregisterListener(listener);
		
		super.onDestroy();
	}
	
	private void doRep()
	{
    	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	v.vibrate(50);
    	
    	if(repCount > 1)
    	{
	    	repCount--;
        	repButton.setText(String.valueOf(repCount) + "/" + String.valueOf(initialRepCount));
        	//view.setBackgroundColor(getResources().getColor(R.color.goBlue));
    	}
    	else if(setCount == 1 && repCount == 1) // finished
    	{
    		sensorManager.unregisterListener(listener);
    		finishExercise();
    	}
    	else // finish set
    	{
    		waiting = true;
        	setCount--;
        	repCount = initialRepCount;
        	
        	final Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        	final Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        	in.setDuration(1000);
        	out.setDuration(1000);
        	
            stopWatch.startAnimation(in);
            stopWatch.setVisibility(View.VISIBLE);
        	
        	//view.setBackgroundColor(getResources().getColor(R.color.timerBackground));
        	
        	setView.setText(String.valueOf(setCount));
    		
    		repButton.setClickable(false);
    		countdown = (restTime/1000) + 1;
    		
    		CountDownTimer timer = new CountDownTimer(restTime + 1300, 1000) 
    		{
				@Override
				public void onTick(long millisUntilFinished)
				{
					countdown--;
					if (countdown == 0) {
						repButton.setText("00:0" + String.valueOf(countdown));
						stopWatch.startAnimation(out);
			        	stopWatch.setVisibility(View.INVISIBLE);
					} else if (countdown < 10) {
						repButton.setText("00:0" + String.valueOf(countdown));
					} else {
						repButton.setText("00:" + String.valueOf(countdown));
					}
				} 
				@Override
				public void onFinish()
				{
		        	repButton.setClickable(true);
		        	repButton.startAnimation(in);
		        	repButton.setText("Keep going!");
		        	waiting = false;
		        	//view.setBackgroundColor(getResources().getColor(R.color.goBlue));
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
