package com.gomotion;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;


public class ListCardioExercisesActivity extends ListActivity 
{
	public static final String EXERCISE_ID = "com.gomotion.EXERCISE_ID";
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private static final String POST_ITEM = "postItem";

	protected static final int THESHOLD_ACCURACY = 20;
	
	private boolean pendingPublishReauthorization = false;
	private int postItem;
	private String name;
	private boolean online = false;
	
	private Session session;
	
	private OfflineDatabase db;
	private CardioAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.activity_list_body_weight_exercises);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		session = Session.getActiveSession();
		
		if(session != null)
		{			
			if (savedInstanceState != null)
			{
				postItem = savedInstanceState.getInt(POST_ITEM);
			    pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
			}
			
			session.addCallback(new Session.StatusCallback() {

				public void call(Session session, SessionState state, Exception exception) {
	
					onSessionStateChange(session, state, exception);
				}				
			});
		}

		db = new OfflineDatabase(this);
		Cursor exercises = db.getAllCardioExercises();
		adapter = new CardioAdapter(this, exercises);
		setListAdapter(adapter);
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{		
		final int cid = (int) id;
				
		String[] items = null;

		if(session != null && session.isOpened())
		{
			online = true;
			String[] temp = {"View route", "Share route on Facebook", "Share route on GoMotion", "Delete"};
			items = temp;
		} 
		else 
		{
			String[] temp = {"View route", "Delete"};
			items = temp;

		}		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Options")
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if(item == 0)
					{
						System.out.println(cid);

						Intent intent = new Intent(ListCardioExercisesActivity.this, RouteActivity.class);
						intent.putExtra(EXERCISE_ID, cid);
						startActivity(intent);
					}
					else if(item == 1 && online)
					{
						postItem = cid;
						postRouteFacebook();
					}
					else if(item == 2 && online)
					{
						postItem = cid;
						postRouteGoMotion();
					}
					else if((item == 1 && !online) || (online && item == 3)) 
					{
						db.deleteCardioExercise(cid);
						adapter.changeCursor(db.getAllCardioExercises());
					}
				}
			});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
    private void onSessionStateChange(Session session, SessionState state, Exception exception)
    {
    	System.out.println("State changed: " + session.getState());
    	if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) 
    	{
    	    pendingPublishReauthorization = false;
    	    postRouteFacebook();
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        outState.putInt(POST_ITEM, postItem);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
    }
    
    public void postRouteFacebook()
    {
    	    if (session != null){

    	        // Check for publish permissions    
    	        List<String> permissions = session.getPermissions();
    	        if (!isSubsetOf(PERMISSIONS, permissions))
    	        {
    	            pendingPublishReauthorization = true;
    	            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
    	            session.requestNewPublishPermissions(newPermissionsRequest);
    	            return;
    	        }

				// make request to the /me API
				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

					// callback after Graph API response with user object
					public void onCompleted(GraphUser user, Response response) {
						if(user != null)
						{
							name = user.getFirstName();

				            System.out.println("Creating request.");
				            
				            //Get database items
				            OfflineDatabase db = new OfflineDatabase(ListCardioExercisesActivity.this);
				            CardioExercise exercise = db.getCardioExercise(postItem);
				            
				            String mapURL = makeGoogleMapsString(db,
									exercise);
				            				
							String typeVerb = "";
							
							switch(exercise.getType())
							{
								case WALK:
									typeVerb = "walked";
									break;
								case RUN:
									typeVerb = "ran";
									break;
								case CYCLE:
									typeVerb = "cycled";
									break;
							}
							
							/**
							 * MUST ACCOUNT FOR HOURS AND IMPERIAL UNITS ALSO
							 * DON'T FORGET!
							 *  **/
							double distance = ((double) exercise.getDistance()) / 1000; // kilometres				
							int timeLength = exercise.getTimeLength();
							int mins = timeLength / 60;
							int secs = timeLength % 60;
				            	            
				            String descriptionTemplate = "%s has just %s %.2f km in %d:%d, view the route they travelled here!";
				            String description = String.format(descriptionTemplate, name, typeVerb, distance, mins, secs);

			    	        final Bundle postParams = new Bundle();
			    	        postParams.putString("name", "GoMotion Fitness App for Android");
			    	        postParams.putString("caption", "Cardio exercise completed");
			    	        postParams.putString("description", description);
			    	        postParams.putString("link", mapURL);
			    	        postParams.putString("picture", mapURL);
							
							Request.Callback callback = new Request.Callback() {
			    	            public void onCompleted(Response response) {

			    	                FacebookRequestError error = response.getError();
			    	                if (error != null) {
			    	                    Toast.makeText(ListCardioExercisesActivity.this,
			    	                         error.getErrorMessage(),
			    	                         Toast.LENGTH_SHORT).show();
			    	                    } else {
			    	                        Toast.makeText(ListCardioExercisesActivity.this, 
			    	                             "Post successful",
			    	                             Toast.LENGTH_LONG).show();
			    	                }
			    	            }
			    	        };

			    	        Request request = new Request(session, "me/feed", postParams, 
			    	                              HttpMethod.POST, callback);

			    	        final RequestAsyncTask task = new RequestAsyncTask(request);
			    	        task.execute();
						}
					}
				});

				System.out.println("Status posted.");
    	    }
    }
    
    public void postRouteGoMotion() {
		AsyncTask<CardioExercise, Void, Boolean> task;
		task = new AsyncTask<CardioExercise, Void, Boolean>() {
	
			@Override
			protected Boolean doInBackground(CardioExercise... params) {
				if(Session.getActiveSession() == null)
					return false;
				Request request = Request.newMeRequest(Session.getActiveSession(), null);
				Response response = request.executeAndWait();
				params[0].setUserID((String)response.getGraphObject().getProperty("id"));
				//#ADD URL
				return OnlineDatabase.add(params[0]);
			}
	
			@Override
			protected void onPostExecute(Boolean result) {
				if (result)
					ListCardioExercisesActivity.this
							.runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(
											ListCardioExercisesActivity.this,
											"Exercise post successful",
											Toast.LENGTH_SHORT).show();
								}
							});
				else
					ListCardioExercisesActivity.this
							.runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(
											ListCardioExercisesActivity.this,
											"Failed to communicate with database",
											Toast.LENGTH_SHORT).show();
								}
							});
	
			}
		};
		
		task.execute(db.getCardioExercise(postItem));
	}
    
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

	private String makeGoogleMapsString(OfflineDatabase db,
			CardioExercise exercise) {
		// Build URL for Google Maps
		StringBuilder params = new StringBuilder("http://maps.googleapis.com/maps/api/staticmap?");
		String size = "size=500x500";
		params.append(size);
		
		Cursor waypoints = db.getWaypoints(exercise.getID());
		
		waypoints.moveToPosition(waypoints.getCount() / 2);
		
		// Must center and zoom map until markers can be used
		params.append("&zoom=15");
		params.append("&center=" + waypoints.getDouble(0) + "," + waypoints.getDouble(1));
		
		waypoints.moveToFirst();
		
		// Can't use multiple markers as Facebook removes parameters with the same name
		/* waypoints.moveToFirst();
		Waypoint first = new Waypoint(waypoints.getDouble(0), waypoints.getDouble(1));
		waypoints.moveToLast();
		Waypoint last = new Waypoint(waypoints.getDouble(0), waypoints.getDouble(1));
		waypoints.moveToFirst();
		
		String startMarker = String.format("&markers=color:green|label:S|%f,%f", first.getLatitude(), first.getLongitude());
		String endMarker = String.format("&markers=color:red|label:F|%f,%f", last.getLatitude(), last.getLongitude());
		params.append(startMarker);
		params.append(endMarker);*/
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ListCardioExercisesActivity.this);
         
		String colour = sharedPref.getString(SettingsActivity.ROUTE_COLOUR, "3");
		
		int routeTransparency = (int) (255 * ((double) sharedPref.getInt(SettingsActivity.ROUTE_TRANSPARENCY, 80) / 100));
		String transparency = Integer.toHexString(routeTransparency);
						    		
		switch(Integer.valueOf(colour))
		{
			case 1:
				colour = "0xff0000";
				break;
			case 2:
				colour = "0x00ff00";
				break;
			case 3:
				colour = "0x0000ff";
				break;
		}
		
		String pathSettings = "&path=color:" + colour + transparency + "|weight:5";
		params.append(pathSettings);
		
		int n = 0;
		int threshold = (int) Math.ceil((waypoints.getCount() / THESHOLD_ACCURACY));
		do {
			n++;
			if(n % threshold == 0) params.append("|" + waypoints.getDouble(0) + "," + waypoints.getDouble(1));
			
		} while(waypoints.moveToNext());
		
		params.append("&sensor=false");
		return params.toString();
	}


	public static String formatExerciseType(String s)
    {
		char[] charArray = s.toLowerCase().toCharArray();
		charArray[0] = Character.toUpperCase(charArray[0]);
		String formatted = new String(charArray);
    	
    	return formatted;
    }

	public class CardioAdapter extends CursorAdapter
	{
		private final LayoutInflater mInflater;

		public CardioAdapter(Context context, Cursor cursor)
		{
			super(context, cursor, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) 
		{
			View view =  mInflater.inflate(R.layout.cardio_exercise_info, parent, false);

			view.setTag(R.id.cardio_type, view.findViewById(R.id.cardio_type));
			view.setTag(R.id.cardio_completed, view.findViewById(R.id.cardio_completed));
			view.setTag(R.id.cardio_stats, view.findViewById(R.id.cardio_stats));

			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) 
		{			
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ListCardioExercisesActivity.this);
			String units = sharedPref.getString(SettingsActivity.UNITS, "1");
			
			String title = formatExerciseType(cursor.getString(4));
			TextView type = (TextView) view.getTag(R.id.cardio_type);
			type.setText(title);

			String date = new SimpleDateFormat("dd/MM/yyyy, HH:mm").format(new Date(cursor.getLong(1)));
			TextView completed = (TextView) view.getTag(R.id.cardio_completed);
			completed.setText("Completed: " +  date);
			
			String timeStr = cursor.getString(3);
			
			int time = Integer.valueOf(timeStr);
			int hours = time / (60 * 60);
			int mins = (hours == 0) ? time / 60 : (time % (hours * 60*60)) / 60;
			int secs = (hours ==0 && mins == 0) ? time : time % ((hours*60*60) + (mins*60));
			
			String timeFormatted;
			if(hours == 0) timeFormatted = String.format("%02d:%02d", mins, secs).toString();
			else timeFormatted = String.format("%02d:%02d:%02d", hours, mins, secs).toString();
				
			
			double dist = Double.valueOf(cursor.getString(2)) / 1000;
			String distUnits = " km ";
			String paceUnits = " min/km";
			
			if(Integer.valueOf(units) == 2)
			{
				dist = dist * 0.000621371192;
				distUnits = " mi ";
				paceUnits = " min/mile";
			}			

			String distStr = String.format("%.2f", dist);
			
			double pace = time / dist;						
			int paceMins = (int) (pace / 60);
			int paceSecs = (int) (pace % 60);
			String paceString = String.format("%02d:%02d", paceMins, paceSecs);
			
			TextView stats = (TextView) view.getTag(R.id.cardio_stats);
			stats.setText("Distance: " + distStr + distUnits + "Time: " + timeFormatted + " Pace: " + paceString + paceUnits);	
		}
	}
}
