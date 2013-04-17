package com.gomotion;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.view.ViewGroup;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class HomeScreen extends Activity 
{
	public static final String CARDIO_TPYE = "com.gomotion.CARDIO_TYPE";	
	public static final String BODY_WEIGHT_TYPE = "com.gomotion.BODY_WEIGHT_TYPE";
	
	private LinkedList<String> idList;
	
	private Session session;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		session = Session.getActiveSession();
		
		if(session != null)
		{					
			idList = new LinkedList<String>();
			
			// make request to the /me API
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user object
				public void onCompleted(GraphUser user, Response response) {
					if(user != null)
					{
						final Bundle postParams = new Bundle();
						
						postParams.putString("fields", "installed");

						Request.Callback callback = new Request.Callback() {
							public void onCompleted(Response response) {

								JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
								
								try {
									JSONArray jsonList = graphResponse.getJSONArray("data");
									
									for(int i = 0; i < jsonList.length(); i++)
									{
										String id = jsonList.getJSONObject(i).getString("id");
										idList.add(id);
									}
									
									System.out.println(idList);

								} catch (JSONException e) {
									Log.i("JSON Error",
											"JSON error "+ e.getMessage());
								}		    	            	
								FacebookRequestError error = response.getError();
								if (error != null) {
									System.out.println(error.getErrorMessage());
								} else {
									System.out.println("Friend IDs retrieved successfully");
								}
							}
						};

		    	        Request request = new Request(session, "me/friends", postParams, 
		    	                              HttpMethod.GET, callback);

		    	        final RequestAsyncTask task = new RequestAsyncTask(request);
		    	        task.execute();
					}
				}
			});
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	if(session != null && session.isOpened()) getMenuInflater().inflate(R.menu.activity_home_screen_online, menu);
    	else getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	Intent intent = new Intent(this, SettingsActivity.class);
            	startActivity(intent);
            	break;
            case R.id.menu_logout:
            	session.close();
            	invalidateOptionsMenu();
            	break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public void outdoorOptions(View view)
    {
    	final Item[] items = {
    		    new Item("Walk", R.drawable.walk),
    		    new Item("Run", R.drawable.run),
    		    new Item("Cycle", R.drawable.bike),
    		    new Item("History", 0),
    		};

    		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
    		    this,
    		    android.R.layout.select_dialog_item,
    		    android.R.id.text1,
    		    items){
    		        public View getView(int position, View convertView, ViewGroup viewGroup) {
    		            //User super class to create the View
    		            View v = super.getView(position, convertView, viewGroup);
    		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

    		            //Put the image on the TextView
    		            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

    		            //Add margin between image and text (support various screen densities)
    		            int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
    		            tv.setCompoundDrawablePadding(dp5);
    		            tv.setTextSize(18);

    		            return v;
    		        }
    		    };
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Outdoor options")
    	.setAdapter(adapter, new DialogInterface.OnClickListener() {

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
    	})
    	/*.setItems(items, new DialogInterface.OnClickListener() {
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
    	})*/;
    	
    	builder.show();
    }
    
    public void indoorOptions(View view)
    {
    	String[] items = {"Push Ups", "Sit Ups", "Custom", "History"};
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Indoor options")
    	.setItems(items, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int i) {
    			
    			switch(i)
    			{
    				case 0:
    					doPushUps();
    					break;
    				case 1:
    					doSitUps();
    					break;
    				case 2:
    					doCustomExercise();
    					break;
    				case 3:
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
    	
    	Bundle bundle = new Bundle();
    	bundle.putInt(BODY_WEIGHT_TYPE, 1);
    	dialog.setArguments(bundle);
    	
    	dialog.show(getFragmentManager(), "push_ups_dialog");

    }
    
    public void doSitUps()
    {
    	BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment();
    	    	
    	Bundle bundle = new Bundle();
    	bundle.putInt(BODY_WEIGHT_TYPE, 2);
    	dialog.setArguments(bundle);    	
    	
    	dialog.show(getFragmentManager(), "sit_ups_dialog");
    }
    
    public void doCustomExercise()
    {
    	BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment();
    	
    	Bundle bundle = new Bundle();
    	bundle.putInt(BODY_WEIGHT_TYPE, 3);
    	dialog.setArguments(bundle);    	
    	
    	dialog.show(getFragmentManager(), "custom_exercise_dialog");
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
    }
}
