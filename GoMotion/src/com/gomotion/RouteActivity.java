package com.gomotion;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Screen which contains the Google Map used
 * to draw the tracked cardio exercise data on. Uses
 * the Google Maps application already installed on the device.
 * 
 * @author Jack Hindmarch
 *
 */
public class RouteActivity extends Activity
{
	private GoogleMap map;
	private int colour;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		int id = getIntent().getIntExtra(ListCardioExercisesActivity.EXERCISE_ID, -1);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String routeColour = sharedPref.getString(SettingsActivity.ROUTE_COLOUR, "3");
		
		int routeTransparency = (int) (255 * ((double) sharedPref.getInt(SettingsActivity.ROUTE_TRANSPARENCY, 80) / 100));
				
		switch(Integer.valueOf(routeColour))
		{
			case 1:
				colour = Color.argb(routeTransparency, 255, 0, 0);			
				break;
			case 2:
				colour = Color.argb(routeTransparency, 0, 255, 0);			
				break;
			case 3:
				colour = Color.argb(routeTransparency, 0, 0, 255);			
				break;
		}
				
		if(id != -1)
		{		
			OfflineDatabase db = new OfflineDatabase(this);
			Cursor waypoints = db.getWaypoints(id);
			
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	
	        CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(new LatLng(waypoints.getDouble(0), waypoints.getDouble(1)), 15);
	        map.moveCamera(cam);	

	        Marker start = map.addMarker(new MarkerOptions()
	        	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
	        	.position(new LatLng(waypoints.getDouble(0), waypoints.getDouble(1)))
	        	.title("Start"));
	        
	        waypoints.moveToLast();
	        
	        Marker finish = map.addMarker(new MarkerOptions()
        		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
	        	.position(new LatLng(waypoints.getDouble(0), waypoints.getDouble(1)))
	        	.title("Finish"));
	        
	        waypoints.moveToFirst();
	        
	        PolylineOptions lineOptions = new PolylineOptions();
	        do {
	        	double lat = waypoints.getDouble(0);
	        	double lng = waypoints.getDouble(1);
	        	
	        	lineOptions.add(new LatLng(lat, lng));
	        }
	        while(waypoints.moveToNext());
	        
	        lineOptions.color(colour);
	        lineOptions.width(15);
	
	        Polyline polyline = map.addPolyline(lineOptions);
		}
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
}
