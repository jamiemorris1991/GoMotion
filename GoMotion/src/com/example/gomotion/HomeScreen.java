package com.example.gomotion;

import com.example.gomotion.BodyWeightExercise.BodyWeightType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class HomeScreen extends Activity 
{

   @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_screen);
        
        OfflineDatabase db = new OfflineDatabase(this);
        db.getAllBodyWeightExercises();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
 
    public void doPushUps(View view)
    {
    	BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment(BodyWeightType.PUSHUPS);
    	dialog.show(getFragmentManager(), "pushups_dialog");

    }
    
    public void doCardio(View view)
    {
    	Intent intent = new Intent(this, CardioActivity.class);
    	startActivity(intent);
    }
    
    public void listBodyWeightExercises(View view)
    {
    	Intent intent = new Intent(this, ListBodyWeightExercisesActivity.class);
    	startActivity(intent);
    }
    
    public void listCardioExercises(View view)
    {
    	
    	Intent intent = new Intent(this, ListCardioExercisesActivity.class);
    	startActivity(intent);
    }
    
    public void viewRoute(View view)
    {
    	Intent intent = new Intent(this, RouteActivity.class);
    	startActivity(intent);
    }
}
