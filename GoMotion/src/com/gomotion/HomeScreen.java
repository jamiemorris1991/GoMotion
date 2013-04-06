package com.gomotion;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class HomeScreen extends Activity 
{
	public static final String CARDIO_TPYE = "com.gomotion.CARDIO_TYPE";
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	private boolean online;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		getIntent().getBooleanExtra(Main.ONLINE_MODE, false);
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
    
    public void testPost(View view)
    {
    	 Session session = Session.getActiveSession();

    	    if (session != null){

    	        // Check for publish permissions    
    	        List<String> permissions = session.getPermissions();
    	        if (!isSubsetOf(PERMISSIONS, permissions)) {
    	            pendingPublishReauthorization = true;
    	            Session.NewPermissionsRequest newPermissionsRequest = new Session
    	                    .NewPermissionsRequest(this, PERMISSIONS);
    	        session.requestNewPublishPermissions(newPermissionsRequest);
    	            return;
    	        }

    	        Bundle postParams = new Bundle();
    	        postParams.putString("name", "Facebook SDK for Android");
    	        postParams.putString("caption", "Build great social apps and get more installs.");
    	        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
    	        postParams.putString("link", "https://developers.facebook.com/android");
    	        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

    	        Request.Callback callback= new Request.Callback() {
    	            public void onCompleted(Response response) {
    	                JSONObject graphResponse = response
    	                                           .getGraphObject()
    	                                           .getInnerJSONObject();
    	                String postId = null;
    	                try {
    	                    postId = graphResponse.getString("id");
    	                } catch (JSONException e) {
    	                    Log.i(TAG,
    	                        "JSON error "+ e.getMessage());
    	                }
    	                FacebookRequestError error = response.getError();
    	                if (error != null) {
    	                    Toast.makeText(getActivity()
    	                         .getApplicationContext(),
    	                         error.getErrorMessage(),
    	                         Toast.LENGTH_SHORT).show();
    	                    } else {
    	                        Toast.makeText(getActivity()
    	                             .getApplicationContext(), 
    	                             postId,
    	                             Toast.LENGTH_LONG).show();
    	                }
    	            }
    	        };

    	        Request request = new Request(session, "me/feed", postParams, 
    	                              HttpMethod.POST, callback);

    	        RequestAsyncTask task = new RequestAsyncTask(request);
    	        task.execute();
    	    }
    }
}
