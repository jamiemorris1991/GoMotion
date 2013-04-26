package com.gomotion;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

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

	protected static final int THESHOLD_ACCURACY = 60;
	
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
       
    public void postRouteGoMotion()
    {
		AsyncTask<CardioExercise, Void, Boolean> task;
		task = new AsyncTask<CardioExercise, Void, Boolean>() {
	
			@Override
			protected Boolean doInBackground(CardioExercise... params) {
				if(Session.getActiveSession() == null)
					return false;
				Request request = Request.newMeRequest(Session.getActiveSession(), null);
				Response response = request.executeAndWait();
				params[0].setUserID((String)response.getGraphObject().getProperty("id"));
				String url = makeShortUrl(makeGoogleMapsString(params[0]));
				params[0].setMapURL(url);
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
    
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset)
    {
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
    
    public void postRouteFacebook()
    {
    	if (session != null)
    	{
    		// Check for publish permissions    
    		List<String> permissions = session.getPermissions();
    		if (!isSubsetOf(PERMISSIONS, permissions))
    		{
    			pendingPublishReauthorization = true;
    			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(ListCardioExercisesActivity.this, PERMISSIONS);
    			session.requestNewPublishPermissions(newPermissionsRequest);
    			return;
    		}
    		
			//Get database items
			OfflineDatabase db = new OfflineDatabase(ListCardioExercisesActivity.this);
			final CardioExercise exercise = db.getCardioExercise(postItem);
			final String longUrl = makeGoogleMapsString(exercise);

    		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
    			
    			@Override			
    			protected String doInBackground(Void... params) {
    				
    				return makeShortUrl(longUrl);
    				
    			}

    			@Override
    			protected void onPostExecute(final String shortUrl) {
    				super.onPostExecute(shortUrl);

    				// make request to the /me API
    				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

    					// callback after Graph API response with user object
    					public void onCompleted(GraphUser user, Response response) {
    						if(user != null)
    						{
    							name = user.getFirstName();

    							System.out.println("Creating request.");
    							
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

    							int timeLength = exercise.getTimeLength();
    							int hours = timeLength / (60 * 60);
    							int mins = (hours == 0) ? timeLength / 60 : (timeLength % (hours * 60*60)) / 60;
    							int secs = (hours ==0 && mins == 0) ? timeLength : timeLength % ((hours*60*60) + (mins*60));

    							String timeLengthFormatted = "";							
    							if(hours == 0) timeLengthFormatted = String.format("%02d:%02d", mins, secs).toString();
    							else timeLengthFormatted = String.format("%02d:%02d:%02d", hours, mins, secs).toString();
    							
    							SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ListCardioExercisesActivity.this);
    							String units = sharedPref.getString(SettingsActivity.UNITS, "1");
    							
    							double dist = (double) (exercise.getDistance()) / 1000;
    							String distUnits = "km";
    							
    							if(Integer.valueOf(units) == 2)
    							{
    								dist = dist * 0.621371192;
    								distUnits = " mi";
    							}			

    							String distStr = String.format("%.2f", dist);

    							String descriptionTemplate = "%s has just %s %s %s in %s, view the route they travelled here!";
    							String description = String.format(descriptionTemplate, name, typeVerb, distStr, distUnits, timeLengthFormatted);

    							final Bundle postParams = new Bundle();
    							postParams.putString("name", "GoMotion Fitness App for Android");
    							postParams.putString("caption", "Cardio exercise completed");
    							postParams.putString("description", description);
    							postParams.putString("link", shortUrl);
    							postParams.putString("picture", shortUrl);

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
    		};

    		task.execute();
    	}
    }
    
    public String makeShortUrl(String longUrl)
    {
    	String shortUrl = "";
    	
		HttpClient httpClient = new DefaultHttpClient();

		try {
			System.out.println("Starting shortener request");
			HttpPost request = new HttpPost("https://www.googleapis.com/urlshortener/v1/url");
			request.addHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");  

			JSONObject obj = new JSONObject();  
			obj.put("longUrl", longUrl);  
			request.setEntity(new StringEntity(obj.toString(), "UTF-8"));  

			HttpResponse resp = httpClient.execute(request);
			System.out.println("Requested shortener URL");


			if ( resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK )  
			{  
				ByteArrayOutputStream out = new ByteArrayOutputStream();  
				resp.getEntity().writeTo(out);  
				out.close();       
				shortUrl   = new JSONObject(out.toString()).getString("id");
			}  
			else
			{
				System.out.println(resp.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		    				
		return shortUrl;
    }

	public String makeGoogleMapsString(CardioExercise exercise)
	{
		OfflineDatabase db = new OfflineDatabase(this);
		
		// Build URL for Google Maps
		StringBuilder params = new StringBuilder("http://maps.googleapis.com/maps/api/staticmap?");
		String size = "size=500x500";
		params.append(size);
		
		Cursor waypoints = db.getWaypoints(exercise.getID());
		//System.out.println(waypoints.getCount());
				
		waypoints.moveToFirst();
		Waypoint first = new Waypoint(waypoints.getDouble(0), waypoints.getDouble(1));
		waypoints.moveToLast();
		Waypoint last = new Waypoint(waypoints.getDouble(0), waypoints.getDouble(1));
		waypoints.moveToFirst();
		
		String markers = String.format("&markers=color:blue|%f,%f|%f,%f", first.getLatitude(), first.getLongitude(), last.getLatitude(), last.getLongitude());
		
		params.append(markers);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
         
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
		
		String pathSettings = "&path=color:" + colour + transparency + "|weight:5|enc:";

		params.append(pathSettings);
		
		Waypoint lastPoint = new Waypoint(0, 0);
		int n = -1;
		int threshold = (int) Math.ceil((waypoints.getCount() / THESHOLD_ACCURACY));
		do {
			n++;
			if(threshold == 0 || n % threshold == 0) 
			{			
				String lat = "";
				String lng = "";
				
				lat = encodeSignedNumber(floor1e5(waypoints.getDouble(0) - lastPoint.getLatitude())).replace("\\\\", "\\");
				lng = encodeSignedNumber(floor1e5(waypoints.getDouble(1) - lastPoint.getLongitude())).replace("\\\\", "\\");
				
				System.out.println(n + ":" + lat + lng);
				params.append(lat + lng);
				lastPoint = new Waypoint(waypoints.getDouble(0), waypoints.getDouble(1));
				
			}
		} while(waypoints.moveToNext());

		System.out.println(params.toString());
		
		params.append("&sensor=false");
		final String longUrl = params.toString();
		
		return longUrl;
	}
	
    private static int floor1e5(double coordinate) {
        return (int)(Math.round(coordinate * 1e5));
    }

    private static String encodeSignedNumber(int num) {
        int sgn_num = num << 1;
        if (num < 0) {
            sgn_num = ~(sgn_num);
        }
        return(encodeNumber(sgn_num));
    }

    private static String encodeNumber(int num) {

        StringBuffer encodeString = new StringBuffer();

        while (num >= 0x20) {
                int nextValue = (0x20 | (num & 0x1f)) + 63;
                if (nextValue == 92) {
                        encodeString.append((char)(nextValue));
                }
                encodeString.append((char)(nextValue));
            num >>= 5;
        }

        num += 63;
        if (num == 92) {
                encodeString.append((char)(num));
        }

        encodeString.append((char)(num));

        return encodeString.toString();

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
			
			String accuracy = sharedPref.getString(SettingsActivity.ACCURACY, "1");
			
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
			String distUnits = "km ";
			String paceUnits = " min/km";
			
			if(Integer.valueOf(units) == 2)
			{
				dist = dist * 0.621371192;
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
