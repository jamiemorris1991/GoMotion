package com.example.gomotion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;

public class CardioActivity extends Activity 
{
	private List<Waypoint> waypoints;
	private LocationManager locationManager;
	private GpsStatus gpsStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardio);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startExercise(View view)
	{
		view.setEnabled(false);		

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		waypoints = new LinkedList<Waypoint>();
		
		locationManager.addGpsStatusListener( new GpsStatus.Listener()
		{
			public void onGpsStatusChanged(int event)
			{
				gpsStatus = locationManager.getGpsStatus(gpsStatus);
				
				switch (event) 
				{
			        case GpsStatus.GPS_EVENT_STARTED:
			            // Do Something with mStatus info
			            break;
	
			        case GpsStatus.GPS_EVENT_STOPPED:
			            // Do Something with mStatus info
			            break;
	
			        case GpsStatus.GPS_EVENT_FIRST_FIX:
			            // Do Something with mStatus info
			            break;
	
			        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			        	int satCount = 0;			        	
				        Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
				        Iterator<GpsSatellite> itr = sats.iterator();
				        
				        while(itr.hasNext()) {
				        	itr.next();
				        	satCount++;
				        }				        
				        
				        System.out.println(satCount);
				        break;
				}
		    }		
		});
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

			public void onLocationChanged(Location location) 
			{
				waypoints.add(new Waypoint(location.getLongitude(), location.getLatitude()));
			}

			public void onProviderDisabled(String provider)
			{
				System.out.println("GPS is not turned on.");
			}

			public void onProviderEnabled(String provider)
			{
				System.out.println("GPS is turned on.");
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
		});
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		System.out.println(waypoints);
	}
}
