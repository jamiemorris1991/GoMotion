package com.example.gomotion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CardioActivity extends Activity 
{
	private List<Waypoint> waypoints;
	private LocationManager locationManager;
	private GpsStatus gpsStatus;
	
	// Listeners
	private GpsStatus.Listener gpsListener;
	private LocationListener locationListener;
	
	// Time/distance variables
	private long timestamp;
	private boolean started;
	private Timer timer;
	private String timeFormatted;
	private int time;
	private int distance;
	private double pace;
	
	// Debugging views
	private TextView waypoint_count;
	private TextView gps_setting;
	
	// Views
	private TextView signal;
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
		started = false;
		timer = new Timer();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		waypoints = new LinkedList<Waypoint>();
		
		gps_setting = (TextView) findViewById(R.id.gps_setting);
		waypoint_count = (TextView) findViewById(R.id.waypoint_count);
        signal = (TextView) findViewById(R.id.gps_signal);
        signal.setText("Not Found");
        signal.setTextColor(Color.RED);
		
		gpsListener = new GpsStatus.Listener() {
			
			public void onGpsStatusChanged(int event)
			{
				gpsStatus = locationManager.getGpsStatus(gpsStatus);
				
				switch (event) 
				{
			        case GpsStatus.GPS_EVENT_STARTED:
			        	System.out.println("Started");
			            break;
	
			        case GpsStatus.GPS_EVENT_STOPPED:
			        	System.out.println("Stopped");

			            break;
	
			        case GpsStatus.GPS_EVENT_FIRST_FIX:
			        	signal.setText("Found");
			        	signal.setTextColor(Color.GREEN);
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
					waypoints.add(new Waypoint(location.getLongitude(), location.getLatitude()));
					waypoint_count.setText(String.valueOf(waypoints.size()));
				}
			}

			public void onProviderDisabled(String provider)
			{
				gps_setting.setText("GPS is turned off");
				gps_setting.setTextColor(Color.RED);
			}

			public void onProviderEnabled(String provider)
			{
				gps_setting.setText("GPS is turned on");
				gps_setting.setTextColor(Color.GREEN);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) 
			{
				switch (status) 
				{
		            case LocationProvider.AVAILABLE:
		                System.out.println("Network available again\n");
		                break;
		            case LocationProvider.OUT_OF_SERVICE:
		            	System.out.println("Network out of service\n");
		                break;
		            case LocationProvider.TEMPORARILY_UNAVAILABLE:
		            	System.out.println("Network temporarily unavailable\n");
		                break;
	            }
			}			
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10000, locationListener);
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
		
		final Handler timeHandler = new Handler() {	
			@Override
			public void handleMessage(Message msg) {
				timeView.setText(timeFormatted);
			}			
		};
		
		timeView = (TextView) findViewById(R.id.cardio_time);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run()
			{
				time++;
				int hours = time / (60 * 60);
				int mins = (hours == 0) ? time / 60 : (time % (hours * 60*60)) / 60;
				int secs = (hours ==0 && mins == 0) ? time : time % ((hours*60*60) + (mins*60));
				
				if(hours == 0) timeFormatted = String.format("%02d:%02d", mins, secs).toString();
				else timeFormatted = String.format("%02d:%02d:%02d", hours, mins, secs).toString();
				
				timeHandler.sendEmptyMessage(0);
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
