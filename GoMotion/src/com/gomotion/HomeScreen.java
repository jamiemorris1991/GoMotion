package com.gomotion;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gomotion.BodyWeightExercise.BodyWeightType;
import com.gomotion.CardioExercise.CardioType;

public class HomeScreen extends Activity {
	public static final String CARDIO_TPYE = "com.gomotion.CARDIO_TYPE";
	public static final String BODY_WEIGHT_TYPE = "com.gomotion.BODY_WEIGHT_TYPE";
	public static final int POSTS_TO_SHOW = 16;
		
	public static enum LeaderboardType {DISTANCE, SPEED, REPS};
	public static enum ExerciseType {WALK, RUN, CYCLE, PUSHUPS, SITUPS};
	
	public static String[] greetings = {"How about going for a run?", "How about doing a few sets of push ups?", "How about doing a few sets of sit ups?",
										"How about going for a walk?", "How about going out for a bike ride?", "How about seeing if you can beat a personal best?"};
	
	private HashMap<String, FacebookUser> friends;
	private HashMap<String, Bitmap> profilePics;
	
	private Session session;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);		

		session = Session.getActiveSession();

		if(session == null || (session != null && session.isClosed()))
		{
			ImageView logo = (ImageView) findViewById(R.id.logo);
			TextView greeting = (TextView) findViewById(R.id.greeting);
			
			String greetingStr = greetings[new Random().nextInt(greetings.length)];
			
			greeting.setText(greetingStr);
			
			logo.setVisibility(View.VISIBLE);
			greeting.setVisibility(View.VISIBLE);
		}
		else
		{	
			friends = new HashMap<String, FacebookUser>();
			profilePics = new HashMap<String, Bitmap>();

			// make request to the /me API
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user object
				public void onCompleted(GraphUser user, Response response) {
					if(user != null)
					{
						final Bundle postParams = new Bundle();
						postParams.putString("fields", "name,installed,picture");
						
						Request meRequest = new Request(session, "me", postParams, HttpMethod.GET, new Request.Callback() {
							
							public void onCompleted(Response response) {
								JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();

								try {

									String id = graphResponse.getString("id");
									String name = graphResponse.getString("name");
									String picURL = graphResponse.getJSONObject("picture").getJSONObject("data").getString("url");
									
									friends.put(id, new FacebookUser(id, name, picURL));
									
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
						});

						Request.Callback callback = new Request.Callback() {
							public void onCompleted(Response response) {

								JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();

								try {
									JSONArray jsonList = graphResponse.getJSONArray("data");

									for(int i = 0; i < jsonList.length(); i++)
									{
										JSONObject obj = jsonList.getJSONObject(i);

										if(!obj.isNull("installed")) 
										{						
											String id = obj.getString("id");
											String name = obj.getString("name");										
											String pictureURL = obj.getJSONObject("picture").getJSONObject("data").getString("url");
											
											friends.put(id, new FacebookUser(id, name, pictureURL));
										}
									}

									System.out.println(friends);

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

						final RequestAsyncTask meTask = new RequestAsyncTask(meRequest);
						final RequestAsyncTask task = new RequestAsyncTask(request);
						meTask.execute();
						task.execute();
						
						setSingleWallMessage("Communicating with database", true);
						buildWall();
					}					
				}
			});
		}
	}

	private void buildWall()
	{
		setSingleWallMessageInMainThread("Communicating with Facebook", true);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				LinkedList<BodyWeightExercise> bwe = OnlineDatabase
						.getBodyWeightExercises(friends, POSTS_TO_SHOW);
				
				LinkedList<CardioExercise> ce = OnlineDatabase
						.getCardioExercises(friends, POSTS_TO_SHOW);
				

				if (bwe == null || ce == null) {
					setSingleWallMessageInMainThread("Failed to communicate with database", false);
					return null;
				}

				List<Exercise> allExcercises = new LinkedList<Exercise>();
				allExcercises.addAll(bwe);
				allExcercises.addAll(ce);

				if (allExcercises.size() == 0) {
					setSingleWallMessageInMainThread("None of your friends are using GoMotion yet!", false);
					return null;
				}
				
				
				Collections.sort(allExcercises, new Comparator<Exercise>() {

					public int compare(Exercise lhs, Exercise rhs) {
						if (lhs.dbTimestamp > rhs.dbTimestamp)
							return -1;
						else if (lhs.dbTimestamp < rhs.dbTimestamp)
							return 1;
						else
							return 0;
					}
				});

				final List<Exercise> excercises = allExcercises.subList(0,
						Math.min(POSTS_TO_SHOW, allExcercises.size()));

				runOnUiThread(new Runnable() {

					public void run() {
						buildWallFromExcercises(excercises);
					}
				});

				return null;
			}

		};
		
		task.execute();
	}

	private void buildWallFromExcercises(List<Exercise> exercises)
	{
		final LinearLayout wall = (LinearLayout) findViewById(R.id.wall);
		wall.removeAllViews();
		
		final ListIterator<Exercise> i = exercises.listIterator();
		while (i.hasNext()) 
		{			
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

				Exercise exercise = i.next();
				Bitmap bm;
				String message = "";
				
				@Override
				protected Void doInBackground(Void... params) {
					
					try	{
						
						if(profilePics.get(exercise.getUserID()) == null)
						{
							URL newurl = new URL(friends.get(exercise.getUserID()).getPictureURL());
							bm = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
							profilePics.put(exercise.getUserID(), bm);
						}
						else 
						{
							bm = profilePics.get(exercise.getUserID());
						}
						
						runOnUiThread(new Runnable() {
							public void run() {
								
								LinearLayout post = new LinearLayout(HomeScreen.this);	
								post.setOrientation(LinearLayout.HORIZONTAL);
								post.setPadding(10, 10, 10, 10);
								
								ImageView profilePic = new ImageView(HomeScreen.this);	
								profilePic.setImageBitmap(bm);	
								profilePic.setScaleX(1.5f);
								profilePic.setScaleY(1.5f);								
								post.addView(profilePic);
								
								long time = System.currentTimeMillis() - exercise.getTimeStamp();
								
								if(exercise instanceof BodyWeightExercise)
								{
									BodyWeightExercise bwe = (BodyWeightExercise) exercise;
									String format = "%s completed %d sets of %d %s %s ago.";
									String name = friends.get(bwe.getUserID()).getName();
									int sets = bwe.getSets();
									int reps = bwe.getReps();
									
									String exerciseType = "";

									switch (bwe.getType()) {
									case PUSHUPS:
										exerciseType = "push ups";
										break;
									case SITUPS:
										exerciseType = "sit ups";
										break;
									case CUSTOM:
										exerciseType = bwe.getName().toLowerCase();
										break;
									}

									String timeString = getTimestampString(time);
									
									message = String.format(format, name, sets, reps, exerciseType, timeString);
								}
								else
								{
									CardioExercise ce = (CardioExercise) exercise;
									String format = "%s %s %s%s in %s, %s ago. Click here to view the route travelled.";
									String name = friends.get(ce.getUserID()).getName();
									double dist = (double) (ce.getDistance()) / 1000; // kilometres
									int timeLength = ce.getTimeLength();
									
									String typeVerb = "";
									
									switch(ce.getType())
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
									
									int hours = timeLength / (60 * 60);
									int mins = (hours == 0) ? timeLength / 60 : (timeLength % (hours * 60*60)) / 60;
									int secs = (hours ==0 && mins == 0) ? timeLength : timeLength % ((hours*60*60) + (mins*60));

									String timeLengthFormatted = "";
									
									if(hours == 0) timeLengthFormatted = String.format("%02d:%02d", mins, secs).toString();
									else timeLengthFormatted = String.format("%02d:%02d:%02d", hours, mins, secs).toString();
									
									SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HomeScreen.this);
									String units = sharedPref.getString(SettingsActivity.UNITS, "1");
									
									String distUnits = "km";
									
									if(Integer.valueOf(units) == 2)
									{
										dist = dist * 0.621371192;
										distUnits = " mi";
									}			

									String distStr = String.format("%.2f", dist);									
									String timeString = getTimestampString(time + (timeLength * 1000));
									
									message = String.format(format, name, typeVerb, distStr, distUnits, timeLengthFormatted, timeString);
								}
								
								TextView text = new TextView(HomeScreen.this);
								text.setTextColor(Color.BLACK);
								text.setTextSize(14);
								text.setPadding(20, 0, 0, 8);
								
								if(exercise instanceof CardioExercise)
								{
									final CardioExercise c = (CardioExercise) exercise;
									text.setOnClickListener(new OnClickListener() {									
										public void onClick(View arg0) {
											System.out.println(c.getMapURL());
											Intent browserIntent = new Intent(Intent.ACTION_VIEW,
													Uri.parse(c.getMapURL()));
											startActivity(browserIntent);
										}
									});
								}
								text.setText(message);
								
								post.addView(text);
								wall.addView(post);

							}
						});
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e)	{
						e.printStackTrace();
					}
					
					return null;
				} 
			};
			
			task.execute();
		}
	}

	
	private String getCardioTimeString(int time)
	{
		time /= 1000;
		int seconds = time % 60;
		
		time /= 60;
		int minutes = time % 60;
		
		time /= 60;
		int hours = time;
		
		String out = "";
		if(hours > 0) out += hours + " hours, ";
		if(minutes > 0) out += minutes + " minutes, ";
		out += seconds + " seconds";
		
		return out;
	}
	
	
	private String getTimestampString(long millis)
	{
		// Seconds
		millis /= 1000;
		if (millis < 60) {
			return millis + " second" + (millis == 1 ? "" : "s");
		}

		// Minutes
		millis /= 60;
		if (millis < 60) {
			return millis + " minute" + (millis == 1 ? "" : "s");
		}
		
		//Hours
		millis /= 60;
		if(millis < 30)
		{
			return millis + " hour" + (millis == 1 ? "" : "s");
		}
		
		//Days
		millis /= 24;
		if(millis < 28)
		{
			return millis + " day" + (millis == 1 ? "" : "s");
		}

		// Months
		millis /= 31;
		return millis + " month" + (millis == 1 ? "" : "s");
	}

	private void setSingleWallMessageInMainThread(final String m, final Boolean l)
	{
		this.runOnUiThread(new Runnable() {
			public void run() {
				setSingleWallMessage(m, l);
			}
		});
	}

	private void setSingleWallMessage(String m, boolean loading)
	{
		LinearLayout wall = (LinearLayout) findViewById(R.id.wall);
		wall.removeAllViews();
		
		if(loading)
		{
			LinearLayout barContainer = new LinearLayout(getApplicationContext());
			barContainer.setLayoutParams(
					new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			barContainer.setGravity(Gravity.CENTER);
			
			ProgressBar bar = new ProgressBar(this.getApplicationContext());
			
			barContainer.addView(bar);
			wall.addView(barContainer);
		}
		//TextView text = new TextView(getApplicationContext());
		//text.setText(m);
		//wall.addView(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{		
	    try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
	    
		if (session != null && session.isOpened())
			getMenuInflater().inflate(R.menu.activity_home_screen_online, menu);
		else
			getMenuInflater().inflate(R.menu.activity_home_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.menu_leaderboards:
			leaderboardOptions();
			break;
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_refresh:
			recreate();
			break;
		case R.id.menu_logout:
			session.closeAndClearTokenInformation();
			recreate();
			break;
		case R.id.menu_help:
			Intent helpIntent = new Intent(this, HelpListActivity.class);
			startActivity(helpIntent);
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
				new Item("History", R.drawable.history),
			};

		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1, items) {
			public View getView(int position, View convertView,
					ViewGroup viewGroup) {
				// User super class to create the View
				View v = super.getView(position, convertView, viewGroup);
				TextView tv = (TextView) v.findViewById(android.R.id.text1);

				// Put the image on the TextView
				tv.setCompoundDrawablesWithIntrinsicBounds(
						items[position].icon, 0, 0, 0);

				// Add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				tv.setCompoundDrawablePadding(dp5);
				tv.setTextSize(18);

				return v;
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Outdoor options").setAdapter(adapter,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int i) {

						switch (i) {
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

	public void indoorOptions(View view) {
		final Item[] items = { 
				new Item("Push Ups", R.drawable.pressup),
				new Item("Sit Ups", R.drawable.situp),
				new Item("Custom", R.drawable.custom),
				new Item("History", R.drawable.history),
			};
		
		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1, items) {
			public View getView(int position, View convertView,
					ViewGroup viewGroup) {
				// User super class to create the View
				View v = super.getView(position, convertView, viewGroup);
				TextView tv = (TextView) v.findViewById(android.R.id.text1);

				// Put the image on the TextView
				tv.setCompoundDrawablesWithIntrinsicBounds(
						items[position].icon, 0, 0, 0);

				// Add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				tv.setCompoundDrawablePadding(dp5);
				tv.setTextSize(18);

				return v;
			}
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Indoor options").setAdapter(adapter,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int i) {

						switch (i) {
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

	
	public void leaderboardOptions()
	{
		final Item[] items = { 
				new Item("News Feed", R.drawable.history),
				new Item("Indoor - Most Reps", R.drawable.situp),
				new Item("Outdoor - Distance Travelled", R.drawable.walk),
				new Item("Outdoor - Speed", R.drawable.bike),
			};

		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1, items) {
			public View getView(int position, View convertView,
					ViewGroup viewGroup) {
				// User super class to create the View
				View v = super.getView(position, convertView, viewGroup);
				TextView tv = (TextView) v.findViewById(android.R.id.text1);

				// Put the image on the TextView
				tv.setCompoundDrawablesWithIntrinsicBounds(
						items[position].icon, 0, 0, 0);

				// Add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				tv.setCompoundDrawablePadding(dp5);
				tv.setTextSize(18);

				return v;
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Leaderboard options").setAdapter(adapter,	new DialogInterface.OnClickListener() {

			LeaderboardType type;
			ExerciseType exerciseType;
			
			public void onClick(DialogInterface dialog, int i) {

				switch(i)
				{
					case 0:
						buildWall();
						break;
					case 1:
						type = LeaderboardType.REPS;
						String[] choices = {"Push Ups", "Sit Ups"};
						AlertDialog.Builder outdoorChoices = new AlertDialog.Builder(HomeScreen.this);

						outdoorChoices
						.setTitle("Exercise Choices")
						.setItems(choices, new DialogInterface.OnClickListener()
						{										
							public void onClick(DialogInterface dialog, int which)
							{
								switch(which)
								{
									case 0:
										exerciseType = ExerciseType.PUSHUPS;
										break;
									case 1:
										exerciseType = ExerciseType.SITUPS;
										break;
								}								
								buildLeaderboard(type, exerciseType);
							}
						});
						
						outdoorChoices.show();
						
						break;
					case 2:
						type = LeaderboardType.DISTANCE;
						buildOutdoorChoices();
						break;
					case 3:
						type = LeaderboardType.SPEED;
						buildOutdoorChoices();
						break;
				}
			}
			
			public void buildOutdoorChoices()
			{
				String[] choices = {"Walking", "Running", "Cycling"};
				AlertDialog.Builder outdoorChoices = new AlertDialog.Builder(HomeScreen.this);

				outdoorChoices
				.setTitle("Exercise Choices")
				.setItems(choices, new DialogInterface.OnClickListener()
				{										
					public void onClick(DialogInterface dialog, int which)
					{
						switch(which)
						{
							case 0:
								exerciseType = ExerciseType.WALK;
								break;
							case 1:
								exerciseType = ExerciseType.RUN;
								break;
							case 2:
								exerciseType = ExerciseType.CYCLE;
								break;
						}
						buildLeaderboard(type, exerciseType);
					}
				});
				outdoorChoices.show();
			}
		});

		builder.show();
	}
	
	public void buildLeaderboard(final LeaderboardType type, final ExerciseType exerciseType)
	{
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			LinkedList<FacebookUser> users = null;
			
			@Override
			protected Void doInBackground(Void... params) {
				
				if(type == LeaderboardType.DISTANCE || type == LeaderboardType.SPEED) users = OnlineDatabase.getCardioExercisesLeaderboard(friends, POSTS_TO_SHOW, type, exerciseType);
				else users = OnlineDatabase.getBodyWeightExercisesLeaderboard(friends, POSTS_TO_SHOW, type, exerciseType);				

				if ((users == null && type == LeaderboardType.REPS)|| (users == null && (type == LeaderboardType.DISTANCE || type == LeaderboardType.SPEED))) {
					setSingleWallMessageInMainThread("Failed to communicate with database", false);
					return null;
				}

				runOnUiThread(new Runnable() {

					public void run() {
						buildLeaderboardFromExercises(users, type, exerciseType);
					}
				});

				return null;
			}

		};
		
		task.execute();
	}
	
	public void buildLeaderboardFromExercises(LinkedList<FacebookUser> users, final LeaderboardType type, final ExerciseType exerciseType)
	{
		final LinearLayout wall = (LinearLayout) findViewById(R.id.wall);
		wall.removeAllViews();
		
		final ListIterator<FacebookUser> i = users.listIterator();
		while (i.hasNext()) 
		{	
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

				FacebookUser user = i.next();
				Bitmap bm;
				String message = "";
				
				@Override
				protected Void doInBackground(Void... params) {
					
					try	{
						
						if(profilePics.get(user.getId()) == null)
						{
							URL newurl = new URL(friends.get(user.getId()).getPictureURL());
							bm = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
							profilePics.put(user.getId(), bm);
						}
						else 
						{
							bm = profilePics.get(user.getId());
						}
						
						runOnUiThread(new Runnable() {
							public void run() {
								
								LinearLayout post = new LinearLayout(HomeScreen.this);	
								post.setOrientation(LinearLayout.HORIZONTAL);
								post.setPadding(10, 10, 10, 10);
								
								ImageView profilePic = new ImageView(HomeScreen.this);	
								profilePic.setImageBitmap(bm);	
								profilePic.setScaleX(1.5f);
								profilePic.setScaleY(1.5f);								
								post.addView(profilePic);
								
								if(type == LeaderboardType.DISTANCE)
								{
									String format = "%s has %s a total distance of %s%s.";
									
									String typeVerb = "";
									
									switch(exerciseType)
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
																		
									SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HomeScreen.this);
									String units = sharedPref.getString(SettingsActivity.UNITS, "1");
									
									double dist = (double) (user.getData()) / 1000;
									String distUnits = "km";
									
									if(Integer.valueOf(units) == 2)
									{
										dist = dist * 0.621371192;
										distUnits = " mi";
									}			

									String distStr = String.format("%.2f", dist);		
									
									message = String.format(format, user.getName(), typeVerb, distStr, distUnits);
								}
								else if(type == LeaderboardType.SPEED)
								{
									message = " " + user.getName();
								}
								else if(type == LeaderboardType.REPS)
								{
									String format = "%s has performed a total number of %d %s.";
									System.out.println(user.getNum());
									String exer = "";
									switch(exerciseType)
									{
										case PUSHUPS:
											exer = "push ups";
											break;
										case SITUPS:
											exer = "sit ups";
											break;
									}
									
									message = String.format(format, user.getName(), user.getData(), exer);
								}
																
								TextView num = new TextView(HomeScreen.this);
								num.setTextColor(Color.BLACK);
								num.setTextSize(14);
								num.setPadding(20, 0, 0, 8);
								
								TextView text = new TextView(HomeScreen.this);
								text.setTextColor(Color.BLACK);
								text.setTextSize(14);
								text.setPadding(5, 0, 0, 8);
								
								num.setText(String.valueOf(user.getNum()) + ".");
								text.setText(message);
								post.addView(num);
								post.addView(text);
								wall.addView(post);

							}
						});
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e)	{
						e.printStackTrace();
					}
					
					return null;
				} 
			};
			
			task.execute();
		}
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
