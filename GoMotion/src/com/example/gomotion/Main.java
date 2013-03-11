package com.example.gomotion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class Main extends Activity
{
	static public User user;
	static public Activity currentActivity;
	
	static final String clientID = "31964a87954a6d5ff696b5ee3cd776d9";
	
	/** To be called at the start of the app
	 */
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        OfflineDatabase db = new OfflineDatabase(this);
        db.getAllBodyWeightExercises();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
	
	static public void init()
	{
	}

//	static public void loginOnline(Activity activity)
//	{
//		
//		String permissions[] = {"publish_stream"};
//		
//		FBLoginManager manager
//		= new FBLoginManager(activity, R.layout.activity_home_screen,
//				clientID, permissions);
//		
//		if(manager.existsSavedFacebook())
//		{
//			manager.loadFacebook();
//		}
//		else
//		{
//			manager.login();
//		}
//		
//		manager.displayToast("LOL DOG");
//	}
	
	static public void loginOffline(String name)
	{
		if(name == null)
		{
			//Get username from file?
		}
		else
			user = new User(name);
	}
	
	static public Context getContext()
	{
		//TODO: Make this return the current window's context
		return null;
	}
	
	
    public void doHomeScreen(View view)
    {
    	Intent intent = new Intent(this, HomeScreen.class);
    	startActivity(intent);
    }
	
}
