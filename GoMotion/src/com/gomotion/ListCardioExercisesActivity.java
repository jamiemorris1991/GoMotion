package com.gomotion;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.gomotion.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ListCardioExercisesActivity extends ListActivity 
{
	public static final String EXERCISE_ID = "com.gomotion.EXERCISE_ID";
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private static final String POST_ITEM = "postItem";
	
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
			String[] temp = {"View route", "Share route", "Delete"};
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
					else if(item == 1 && !online || item == 2) 
					{
						db.deleteCardioExercise(cid);
						adapter.changeCursor(db.getAllCardioExercises());
					}
					else if(item == 1 && online)
					{
						postItem = cid;
						postRoute();
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
    	    postRoute();
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        outState.putInt(POST_ITEM, postItem);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
    }
    
    public void postRoute()
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

	            System.out.println("Creating request.");
	            
	            OfflineDatabase db = new OfflineDatabase(this);
	            CardioExercise exercise = db.getCardioExercise(postItem);
	            Cursor waypoints = db.getWaypoints(exercise.getID());
	            
	            // Build URL for Google Maps
	            
	            while(waypoints.moveToNext())
	            {
	            	
	            }
	            
	            
				// make request to the /me API
				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

					// callback after Graph API response with user object
					public void onCompleted(GraphUser user, Response response) {
						if(user != null)
						{
							name = user.getFirstName();
						}
					}
				});
	            	            
	            String descriptionTemplate = "%s has just completed a %s, view the route they travelled here!";
	            String description = String.format(descriptionTemplate, name, formatExerciseType(exercise.getType().toString()));
	            
    	        Bundle postParams = new Bundle();
    	        postParams.putString("name", "GoMotion Fitness App for Android");
    	        postParams.putString("caption", "Cardio exercise completed");
    	        postParams.putString("description", description);
    	        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

    	        Request.Callback callback = new Request.Callback() {
    	            public void onCompleted(Response response) {
    	                JSONObject graphResponse = response
    	                                           .getGraphObject()
    	                                           .getInnerJSONObject();
    	                String postId = null;
    	                try {
    	                    postId = graphResponse.getString("id");
    	                } catch (JSONException e) {
    	                    Log.i("Error",
    	                        "JSON error "+ e.getMessage());
    	                }
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

    	        RequestAsyncTask task = new RequestAsyncTask(request);
    	        task.execute();
	            System.out.println("Status posted.");
    	    }
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
			String title = formatExerciseType(cursor.getString(4));
			TextView type = (TextView) view.getTag(R.id.cardio_type);
			type.setText(title);

			String date = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(new Date(cursor.getLong(1)));
			TextView completed = (TextView) view.getTag(R.id.cardio_completed);
			completed.setText("Completed: " +  date);

			String distance = cursor.getString(2);
			String timeStr = cursor.getString(3);
			
			int time = Integer.valueOf(timeStr);
			int hours = time / (60 * 60);
			int mins = (hours == 0) ? time / 60 : (time % (hours * 60*60)) / 60;
			int secs = (hours ==0 && mins == 0) ? time : time % ((hours*60*60) + (mins*60));
			
			String timeFormatted;
			if(hours == 0) timeFormatted = String.format("%02d:%02d", mins, secs).toString();
			else timeFormatted = String.format("%02d:%02d:%02d", hours, mins, secs).toString();
			
			double miles = Double.valueOf(distance) * 0.000621371192;
			double pace = ((double) time) / miles;						
			int paceMins = (int) (pace / 60);
			int paceSecs = (int) (pace % 60);
			String paceString = String.format("%02d:%02d", paceMins, paceSecs);


			TextView stats = (TextView) view.getTag(R.id.cardio_stats);
			stats.setText("Distance: " + distance + "m Time: " + timeFormatted + " Pace: " + paceString + " min/mile");	
		}
	}
}
