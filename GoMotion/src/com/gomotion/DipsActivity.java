package com.gomotion;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DipsActivity extends Activity
{	
	private SensorManager sensorManager;
	private Sensor sensor;
	private SensorEventListener listener;
	
	private boolean up = false;
	private boolean down = false;
	private double sum = 0;
	private int n = 0;
	
	TextView sumAccel;
	TextView dipCount;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dips);
		// Show the Up button in the action bar.
		setupActionBar();
		
		sumAccel = (TextView) findViewById(R.id.sum_accel);
		dipCount = (TextView) findViewById(R.id.sum);

		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		
		listener = new SensorEventListener() {			
			
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
				
			}
			
			public void onSensorChanged(SensorEvent event)
			{
				sum += event.values[2];
				
				sumAccel.setText(String.valueOf(sum));
				dipCount.setText(String.valueOf(n));
				
				if(!down && sum < -5)
				{
					down = true;
					System.out.println("down");
					sum = 0;
				}
				else if(down && !up && sum > 5)
				{
					up = true;
					System.out.println("up");
					sum = 0;
				}
				
				if(up && down)
				{
					System.out.println("Full sit up");
					down = up = false;
					n++;
				}
			}
		};
		
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);		
	}
	
	private void setupActionBar() 
	{

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dips, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		sensorManager.unregisterListener(listener);
		
		super.onDestroy();
	}

}
