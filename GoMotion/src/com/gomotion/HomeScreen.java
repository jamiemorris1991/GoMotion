package com.gomotion;

import com.gomotion.R;
import com.gomotion.CardioExercise.CardioType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeScreen extends Activity 
{

	public static final String CARDIO_TPYE = "com.gomotion.CARDIO_TYPE";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	Intent intent = new Intent(this, SettingsActivity.class);
            	startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void outdoorOptions(View view)
    {
    	String[] items = {"Walk", "Run", "Cycle", "History"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Outdoor options")
    	.setItems(items, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int i) {
    			
    			switch(i)
    			{
    				case 0:
    					doCardio(0);
    					break;
    				case 1:
    					doCardio(1);
    					break;
    				case 2:
    					doCardio(2);
    					break;
    				case 3:
    					listCardioExercises();
    					break;
    			}
    			
    		}
    	});
    	
    	builder.show();
    }
    
    public void indoorOptions(View view)
    {
    	String[] items = {"Push Ups", "Sit Ups", "Dips", "Custom", "History"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Indoor options")
    	.setItems(items, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int i) {
    			
    			switch(i)
    			{
    				case 0:
    					doPushUps();
    					break;
    				case 4:
    					listBodyWeightExercises();
    					break;
    			}    			
    		}
    	});
    	
    	builder.show();
    }
 
    public void doPushUps()
    {
    	BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment();
    	dialog.show(getFragmentManager(), "pushups_dialog");

    }
    
    public void doCardio(int type)
    {
    	Intent intent = new Intent(this, CardioActivity.class);
    	intent.putExtra(CARDIO_TPYE, type);
    	startActivity(intent);
    }
    
    public void listBodyWeightExercises()
    {
    	Intent intent = new Intent(this, ListBodyWeightExercisesActivity.class);
    	startActivity(intent);
    }
    
    public void listCardioExercises()
    {
    	
    	Intent intent = new Intent(this, ListCardioExercisesActivity.class);
    	startActivity(intent);
    }
    
    public void viewRoute()
    {
    	Intent intent = new Intent(this, RouteActivity.class);
    	startActivity(intent);
    }
}
