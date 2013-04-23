package com.gomotion;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gomotion.BodyWeightExercise.BodyWeightType;

public class HomeScreen extends Activity {
	public static final String CARDIO_TPYE = "com.gomotion.CARDIO_TYPE";
	public static final String BODY_WEIGHT_TYPE = "com.gomotion.BODY_WEIGHT_TYPE";

	private HashMap<String, FacebookUser> friends;
	//List<GraphUser> friends;

	private Session session;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		setSingleWallMessage("Communicating with Facebook");

		session = Session.getActiveSession();

//		if (session != null) {
//			// idList = new LinkedList<String>();
//			// idToName = new HashMap<String, String>();
//
//			// make request to the /me API
//			Request.executeMyFriendsRequestAsync(session, new Request.GraphUserListCallback() {
//
//				public void onCompleted(List<GraphUser> users, Response response) {
//					friends = users;
//					setSingleWallMessage("Communicating with database");
//					buildWall();
//				}
//			});
//		}

		if(session != null)
		{	
			friends = new HashMap<String, FacebookUser>();

			// make request to the /me API
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user object
				public void onCompleted(GraphUser user, Response response) {
					if(user != null)
					{
						final Bundle postParams = new Bundle();

						postParams.putString("fields", "name,installed,picture");

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

						final RequestAsyncTask task = new RequestAsyncTask(request);
						task.execute();
						
						setSingleWallMessage("Communicating with database");
						buildWall();
					}					
				}
			});
		}
	}

	private void buildWall()
	{

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				LinkedList<BodyWeightExercise> bwe = OnlineDatabase
						.getBodyWeightExercises(friends, 10);
				LinkedList<CardioExercise> ce = OnlineDatabase
						.getCardioExercises(friends, 10);

				if (bwe == null || ce == null) {
					setSingleWallMessageInMainThread("Failed communicate with database");
					return null;
				}

				List<Exercise> allExcercises = new LinkedList<Exercise>();
				allExcercises.addAll(bwe);
				allExcercises.addAll(ce);

				if (allExcercises.size() == 0) {
					setSingleWallMessageInMainThread("None of your friends are using GoMotion yet!");
					return null;
				}

				Collections.sort(allExcercises, new Comparator<Exercise>() {

					public int compare(Exercise lhs, Exercise rhs) {
						if (lhs.timeStamp > rhs.timeStamp)
							return -1;
						else if (lhs.timeStamp < rhs.timeStamp)
							return 1;
						else
							return 0;
					}
				});

				final List<Exercise> excercises = allExcercises.subList(0,
						Math.min(10, allExcercises.size()));

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
		LinearLayout wall = (LinearLayout) findViewById(R.id.wall);
		wall.removeAllViews();

		ListIterator<Exercise> i = exercises.listIterator();
		while (i.hasNext()) {
			final Exercise exercise = i.next();
			
			final LinearLayout post = new LinearLayout(this);
			post.setOrientation(LinearLayout.HORIZONTAL);
			
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					
					try	{
						System.out.println(friends.get(exercise.getUserID()).getPictureURL());
						URL newurl = new URL(friends.get(exercise.getUserID()).getPictureURL());
						final Bitmap bm = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
						System.out.println(bm.toString());
						
						runOnUiThread(new Runnable() {
							public void run() {
								System.out.println("Adding pic to post");
								ImageView profilePic = new ImageView(HomeScreen.this);	
								profilePic.setImageBitmap(bm);				
								post.addView(profilePic);
								
								System.out.println("Added");
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

			TextView text = new TextView(this);
			text.setTextColor(Color.BLACK);
			text.setTextSize(14);
			//text.setBackgroundColor(getResources().getColor(R.color.buttons));
			text.setPadding(4, 0, 4, 8);

			long time = System.currentTimeMillis() - exercise.getTimeStamp();

			String message = "";
			
			//DEBUG
			//message += "(Timestamp: " + exercise.getTimeStamp() + ")\n";
			
			message += friends.get( exercise.getUserID() ).getName() + " completed a \"";
			if(exercise instanceof BodyWeightExercise)
			{
				BodyWeightExercise b = (BodyWeightExercise) exercise;
				if(b.getType() == BodyWeightType.CUSTOM)
					message += b.getName().toLowerCase();
				else
					message += b.getType().toString().toLowerCase();
			}
			else
				message += ((CardioExercise)exercise).getType().toString().toLowerCase();
			
			message += "\" exercise " + getTimestampString(time) + " ago.\nStats: ";
			
			if(exercise instanceof BodyWeightExercise)
			{
				BodyWeightExercise b = (BodyWeightExercise) exercise;
				message += "Sets: " + b.getSets() + " Reps: " + b.getReps();
			}
			else
			{
				final CardioExercise c = (CardioExercise) exercise;
				message += "Distance: " + c.getDistance() + " Time: " + getCardioTimeString(c.getTimeLength());
				message += "\nClick to view route";
				
				text.setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
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
	
	
	private String getTimestampString(long millis) {
		// Seconds
		millis /= 1000;
		if (millis < 120) {
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

	private void setSingleWallMessageInMainThread(final String m) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				setSingleWallMessage(m);
			}
		});
	}

	private void setSingleWallMessage(String m) {
		LinearLayout wall = (LinearLayout) findViewById(R.id.wall);
		TextView text = new TextView(getApplicationContext());
		text.setText(m);
		wall.removeAllViews();
		wall.addView(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (session != null && session.isOpened())
			getMenuInflater().inflate(R.menu.activity_home_screen_online, menu);
		else
			getMenuInflater().inflate(R.menu.activity_home_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	public void outdoorOptions(View view) {
		final Item[] items = { 
				new Item("Walk", R.drawable.walk),
				new Item("Run", R.drawable.run),
				new Item("Cycle", R.drawable.bike),
				new Item("History", 0),
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
				})
		/*
		 * .setItems(items, new DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int i) {
		 * 
		 * switch(i) { case 0: doCardio(0); break; case 1: doCardio(1); break;
		 * case 2: doCardio(2); break; case 3: listCardioExercises(); break; }
		 * 
		 * } })
		 */;

		builder.show();
	}

	public void indoorOptions(View view) {
		String[] items = { "Push Ups", "Sit Ups", "Custom", "History" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Indoor options").setItems(items,
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

	public void doPushUps() {
		BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(BODY_WEIGHT_TYPE, 1);
		dialog.setArguments(bundle);

		dialog.show(getFragmentManager(), "push_ups_dialog");

	}

	public void doSitUps() {
		BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(BODY_WEIGHT_TYPE, 2);
		dialog.setArguments(bundle);

		dialog.show(getFragmentManager(), "sit_ups_dialog");
	}

	public void doCustomExercise() {
		BodyWeightSettingsDialogFragment dialog = new BodyWeightSettingsDialogFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(BODY_WEIGHT_TYPE, 3);
		dialog.setArguments(bundle);

		dialog.show(getFragmentManager(), "custom_exercise_dialog");
	}

	public void doCardio(int type) {
		Intent intent = new Intent(this, CardioActivity.class);
		intent.putExtra(CARDIO_TPYE, type);
		startActivity(intent);
	}

	public void listBodyWeightExercises() {
		Intent intent = new Intent(this, ListBodyWeightExercisesActivity.class);
		startActivity(intent);
	}

	public void listCardioExercises() {

		Intent intent = new Intent(this, ListCardioExercisesActivity.class);
		startActivity(intent);
	}

	public void viewRoute() {
		Intent intent = new Intent(this, RouteActivity.class);
		startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
