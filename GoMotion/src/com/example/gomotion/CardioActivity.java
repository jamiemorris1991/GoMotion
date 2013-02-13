package com.example.gomotion;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;

public class CardioActivity extends Activity 
{
	private List<Integer> waypoints;
	
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

		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		waypoints = new LinkedList<Integer>();
		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, new LocationListener() {

			public void onLocationChanged(Location location) 
			{
				System.out.println(location.getLongitude() + ", " + location.getLatitude());
			}

			public void onProviderDisabled(String provider) {
				
			}

			public void onProviderEnabled(String provider) {
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				
			}			
		});
	}
}
