package com.gomotion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;


public class Main extends Activity
{
	static public User user;
	
	private Session.StatusCallback callback;
			
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

    public void loginFacebook(View view)
    {
    	callback = new Session.StatusCallback() {

    		// callback when session changes state
    		public void call(final Session session, SessionState state, Exception exception) {

    			System.out.println("Call");
    			System.out.println(session.getState());
    			if(session.isClosed()) System.out.println("Session is closed");

    			if (session.isOpened()) 
    			{
    				System.out.println("Session opened!");

					session.removeCallback(callback);
					Intent intent = new Intent(Main.this, HomeScreen.class);
					startActivity(intent);
    			}
    		}
    	};


    	// start Facebook Login
    	Session.openActiveSession(this, true, callback);
    }    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    public void doHomeScreen(View view)
    {
    	Intent intent = new Intent(this, HomeScreen.class);
    	startActivity(intent);
    }
}
