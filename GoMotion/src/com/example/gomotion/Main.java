package com.example.gomotion;

import javax.net.ssl.ManagerFactoryParameters;

import android.app.Activity;
import android.content.Context;
import com.easy.facebook.android.apicall.GraphApi;
import com.easy.facebook.android.facebook.*;

//import com.easy.facebook.android.apicall.GraphApi;
//import com.easy.facebook.android.facebook.*;


public class Main
{
	static public User user;
	static public Activity currentActivity;
	
	static final String clientID = "31964a87954a6d5ff696b5ee3cd776d9";
	
	/** To be called at the start of the app
	 */
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
}
