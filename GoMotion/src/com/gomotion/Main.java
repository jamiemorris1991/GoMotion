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
	
	static final String clientID = "31964a87954a6d5ff696b5ee3cd776d9";
	public static final String ONLINE_MODE = "com.gomotion.ONLINE_MODE";
		
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
    	// start Facebook Login
    	Session.openActiveSession(this, true, new Session.StatusCallback() {

    		// callback when session changes state
    		public void call(Session session, SessionState state, Exception exception) {
				
    			System.out.println("Call");
    			System.out.println(session.getState());
    			if(session.isClosed()) System.out.println("Session is closed");
				
    			if (session.isOpened()) 
    			{
					System.out.println("Session opened!");
    				
    				// make request to the /me API
    				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
    					
    					// callback after Graph API response with user object
    					public void onCompleted(GraphUser user, Response response) {
    						if(user != null)
    						{
    					    	Intent intent = new Intent(Main.this, HomeScreen.class);
    					    	intent.putExtra(ONLINE_MODE, true);
    					    	startActivity(intent);   
    						}
    					}
    					
    				});
    			}
    		}
    	});	
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
    	intent.putExtra(ONLINE_MODE, false);
    	startActivity(intent);
    }
	
////	static public void init()
////	{
////	}
//
////	static public void loginOnline(Activity activity)
////	{
////		
////		String permissions[] = {"publish_stream"};
////		
////		FBLoginManager manager
////		= new FBLoginManager(activity, R.layout.activity_home_screen,
////				clientID, permissions);
////		
////		if(manager.existsSavedFacebook())
////		{
////			manager.loadFacebook();
////		}
////		else
////		{
////			manager.login();
////		}
////		
////		manager.displayToast("LOL DOG");
////	}
//	
//	static public void loginOffline(String name)
//	{
//		if(name == null)
//		{
//			//Get username from file?
//		}
//		else
//			user = new User(name);
//	}
//	
}
