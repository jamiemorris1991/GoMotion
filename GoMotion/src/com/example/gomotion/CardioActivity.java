package com.example.gomotion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CardioActivity extends Activity 
{
	private List<Location> waypoints;
	private LocationManager locationManager;
	private GpsStatus gpsStatus;

	
	// Listeners
	private GpsStatus.Listener gpsListener;
	private LocationListener locationListener;

	// Signal handling
	private boolean signal;
	private boolean gpsSettings;
	private SignalDialog signalAlert;
	private Handler signalLost;
	private Handler signalFound;
	
	// Time/distance variables
	private long timestamp;
	private boolean started;
	private Timer timer;
	private String timeFormatted;
	private int time;
	private double distance;
	private double pace;
	
	// Debugging views
	private TextView waypoint_count;
	private TextView gps_setting;
	
	// Views
	private TextView signalView;
	private TextView timeView;
	private TextView distanceView;
	private TextView paceView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardio);
		getActionBar().setDisplayHomeAsUpEnabled(true);	
		
		timestamp = System.currentTimeMillis();
		distance = 0;
		
		timeView = (TextView) findViewById(R.id.cardio_time);
		distanceView = (TextView) findViewById(R.id.cardio_distance);
		paceView = (TextView) findViewById(R.id.cardio_pace);
		
		timeView.setText("00:00");
		distanceView.setText("0m");
		paceView.setText("0 mins/mile");
		
		started = false;
		signal = false;
		gpsSettings = true;
		
		timer = new Timer();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		waypoints = new LinkedList<Location>();
		
		gps_setting = (TextView) findViewById(R.id.gps_setting);
		waypoint_count = (TextView) findViewById(R.id.waypoint_count);
        signalView = (TextView) findViewById(R.id.gps_signal);
        signalView.setText("No Signal");
        signalView.setTextColor(Color.RED);
		
		gpsListener = new GpsStatus.Listener() {
			
			public void onGpsStatusChanged(int event)
			{
				gpsStatus = locationManager.getGpsStatus(gpsStatus);
				
				switch (event) 
				{
			        case GpsStatus.GPS_EVENT_STARTED:
			            break;
	
			        case GpsStatus.GPS_EVENT_STOPPED:
			            break;
	
			        case GpsStatus.GPS_EVENT_FIRST_FIX:
			        	signalView.setText("Signal");
			        	signalView.setTextColor(Color.GREEN);
			        	
			        	signal = true;
		                signalFound.sendEmptyMessage(0);
			        	
			            break;
	
			        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			        	int satCount = 0;			        	
				        Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
				        Iterator<GpsSatellite> itr = sats.iterator();
				        
				        while(itr.hasNext()) {
				        	itr.next();
				        	satCount++;
				        }		
				        				        
				        break;
				}
		    }		
		};
		
		locationManager.addGpsStatusListener(gpsListener);
		
		locationListener =  new LocationListener() {

			public void onLocationChanged(Location location) 
			{
				if(started)
				{					
					waypoints.add(location);
					waypoint_count.setText(String.valueOf(waypoints.size()));
					
					int size = waypoints.size();
					if(size > 1)
					{
						distance += waypoints.get(size-2).distanceTo(location);
						distanceView.setText(String.valueOf((int) distance) + "m");
						
						double miles = distance * 0.000621371192;
						
						pace = ((double) time) / miles;						
						int mins = (int) (pace / 60);
						int secs = (int) (pace % 60);
						
						String paceString = String.format("%02d:%02d", mins, secs);
						paceView.setText(paceString + " min/mile");
					}
				}
			}

			public void onProviderDisabled(String provider)
			{
				gpsSettings = false;
				gps_setting.setText("GPS is turned off");
				gps_setting.setTextColor(Color.RED);
				
				new AlertDialog.Builder(CardioActivity.this)
				.setTitle("Attention")
				.setMessage("Cardio tracking requires GPS.\n\nTo continue with this exercise, please enable GPS in your location settings.")
				.setCancelable(false)
				.setPositiveButton("Location settings", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				})
				.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				})
				.show();
			}

			public void onProviderEnabled(String provider)
			{
				gpsSettings = true;
				gps_setting.setText("GPS is turned on");
				gps_setting.setTextColor(Color.GREEN);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) 
			{
				switch (status) 
				{
		            case LocationProvider.AVAILABLE:
		                System.out.println("Network available again\n");
		                signal = true;
		                signalFound.sendEmptyMessage(0);		                
		                break;
		                
		            case LocationProvider.OUT_OF_SERVICE:
		            	System.out.println("Network out of service\n");
		            	
		            	signal = false;
		            	signalLost.sendEmptyMessage(0);		            	
		                break;
		                
		            case LocationProvider.TEMPORARILY_UNAVAILABLE:
		            	System.out.println("Network temporarily unavailable\n");
		            	
		            	signal = false;
		            	signalLost.sendEmptyMessage(0);		            	
		                break;
	            }
			}			
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
		
		/**
		 * Signal Handler
		 * 
		 **/
		
		signalAlert = new SignalDialog();
		signalAlert.setCancelable(false);
		
		signalLost = new Handler() {
			public void handleMessage(Message msg)
			{
				if(gpsSettings && !signal) signalAlert.show(getFragmentManager(), "signal dialog");
			}
		};
		
		signalFound = new Handler() {
			public void handleMessage(Message msg)
			{
				if(signal) signalAlert.dismiss();
			}
		};
	}

	@Override
	protected void onResume() 
	{
		signalLost.sendEmptyMessage(0);
		super.onResume();
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

	public void startExercise(View view)
	{
		started = true;
		view.setEnabled(false);		
		
		// Needed to allow updating of view outside of the main thread
		final Handler timeHandler = new Handler() {	
			@Override
			public void handleMessage(Message msg) {
				timeView.setText(timeFormatted);
			}			
		};
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run()
			{
				if(signal && gpsSettings)
				{
					time++;
					int hours = time / (60 * 60);
					int mins = (hours == 0) ? time / 60 : (time % (hours * 60*60)) / 60;
					int secs = (hours ==0 && mins == 0) ? time : time % ((hours*60*60) + (mins*60));
					
					if(hours == 0) timeFormatted = String.format("%02d:%02d", mins, secs).toString();
					else timeFormatted = String.format("%02d:%02d:%02d", hours, mins, secs).toString();
					
					timeHandler.sendEmptyMessage(0);
				}
			}
			
		}, 1000, 1000);

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

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		timer.cancel();				
		locationManager.removeGpsStatusListener(gpsListener);
		locationManager.removeUpdates(locationListener);
		locationManager = null;
	}
}