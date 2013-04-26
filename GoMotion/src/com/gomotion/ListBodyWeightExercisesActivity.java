package com.gomotion;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Screen where past indoor exercises can be viewed and deleted.
 * Can also be shared with Facebook so friends can see them.
 * 
 * @author Jack Hindmarch & Jamie Sterling
 *
 */
public class ListBodyWeightExercisesActivity extends ListActivity
{
	private OfflineDatabase db;
	private BodyWeightAdapter adapter;

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private static final String POST_ITEM = "postItem";

	private boolean pendingPublishReauthorization = false;
	private int postItem;
	private String name;
	private boolean online = false;

	private Session session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_body_weight_exercises);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		session = Session.getActiveSession();

		if (session != null) {
			if (savedInstanceState != null) {
				postItem = savedInstanceState.getInt(POST_ITEM);
				pendingPublishReauthorization = savedInstanceState.getBoolean(
						PENDING_PUBLISH_KEY, false);
			}

			session.addCallback(new Session.StatusCallback() {

				public void call(Session session, SessionState state,
						Exception exception) {

					onSessionStateChange(session, state, exception);
				}
			});
		}

		db = new OfflineDatabase(this);
		Cursor exercises = db.getAllBodyWeightExercises();
		adapter = new BodyWeightAdapter(this, exercises);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final int bwid = (int) id;
		String[] items = null;

		if (session != null && session.isOpened()) {
			online = true;
			String[] temp = { "Share", "Delete" };
			items = temp;
		} else {
			String[] temp = { "Delete" };
			items = temp;

		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Options").setItems(items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if ((!online && item == 0) || (online && item == 1)) {
							db.deleteBodyWeightExercise(bwid);
							adapter.changeCursor(db.getAllBodyWeightExercises());
						} else if (online && item == 0) {
							postItem = bwid;
							postExerciseFacebook();
							postExerciseGoMotion();
						} 
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		System.out.println("State changed: " + session.getState());
		if (pendingPublishReauthorization
				&& state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			pendingPublishReauthorization = false;
			postExerciseFacebook();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(POST_ITEM, postItem);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
	}

	public void postExerciseFacebook() {
		if (session != null) {

			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			// make request to the /me API
			Request.executeMeRequestAsync(session,
					new Request.GraphUserCallback() {

						// callback after Graph API response with user object
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								name = user.getFirstName();

								System.out.println("Creating request.");

								OfflineDatabase db = new OfflineDatabase(
										ListBodyWeightExercisesActivity.this);
								BodyWeightExercise exercise = db
										.getBodyWeightExercise(postItem);

								int setCount = exercise.getSets();
								int repCount = exercise.getReps();

								String exerciseType = "";

								switch (exercise.getType()) {
								case PUSHUPS:
									exerciseType = "push ups";
									break;
								case SITUPS:
									exerciseType = "sit ups";
									break;
								case CUSTOM:
									exerciseType = exercise.getName().toLowerCase();
									break;
								}

								String descriptionTemplate = "%s has just completed %d sets of %d %s!";
								String description = String.format(
										descriptionTemplate, name, setCount,
										repCount, exerciseType);

								final Bundle postParams = new Bundle();
								postParams.putString("name",
										"GoMotion Fitness App for Android");
								postParams.putString("caption",
										"Body weight exercise completed");
								postParams
										.putString("description", description);
								postParams.putString("picture",
										"http://i.imgur.com/ABwcmW9.png");

								Request.Callback callback = new Request.Callback() {
									public void onCompleted(Response response) {

										FacebookRequestError error = response
												.getError();
										if (error != null) {
											Toast.makeText(
													ListBodyWeightExercisesActivity.this,
													error.getErrorMessage(),
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													ListBodyWeightExercisesActivity.this,
													"Post successful",
													Toast.LENGTH_LONG).show();
										}
									}
								};

								Request request = new Request(session,
										"me/feed", postParams, HttpMethod.POST,
										callback);

								final RequestAsyncTask task = new RequestAsyncTask(
										request);
								task.execute();
							}
						}
					});

			System.out.println("Status posted.");
		}
	}

	public void postExerciseGoMotion() {
		AsyncTask<BodyWeightExercise, Void, Boolean> task;
		task = new AsyncTask<BodyWeightExercise, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(BodyWeightExercise... params) {
				if(Session.getActiveSession() == null)
					return false;
				Request request = Request.newMeRequest(Session.getActiveSession(), null);
				Response response = request.executeAndWait();
				params[0].setUserID((String)response.getGraphObject().getProperty("id"));
				return OnlineDatabase.add(params[0]);
			}

//			@Override
//			protected void onPostExecute(Boolean result) {
//				if (result)
//					ListBodyWeightExercisesActivity.this
//							.runOnUiThread(new Runnable() {
//								public void run() {
//									Toast.makeText(
//											ListBodyWeightExercisesActivity.this,
//											"Exercise post successful",
//											Toast.LENGTH_SHORT).show();
//								}
//							});
//				else
//					ListBodyWeightExercisesActivity.this
//							.runOnUiThread(new Runnable() {
//								public void run() {
//									Toast.makeText(
//											ListBodyWeightExercisesActivity.this,
//											"Failed to communicate with database",
//											Toast.LENGTH_SHORT).show();
//								}
//							});
//
//			}
		};
		task.execute(db.getBodyWeightExercise(postItem));
	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	public static String formatExerciseType(String s) {


		char[] charArray = s.toLowerCase().toCharArray();
		charArray[0] = Character.toUpperCase(charArray[0]);
		String formatted = new String(charArray);

		return formatted;
	}

	public class BodyWeightAdapter extends CursorAdapter {
		private final LayoutInflater mInflater;

		public BodyWeightAdapter(Context context, Cursor cursor) {
			super(context, cursor, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.body_weight_exercise_info,
					parent, false);

			view.setTag(R.id.body_weight_type,
					view.findViewById(R.id.body_weight_type));
			view.setTag(R.id.body_weight_completed,
					view.findViewById(R.id.body_weight_completed));
			view.setTag(R.id.body_weight_stats,
					view.findViewById(R.id.body_weight_stats));

			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			String s = cursor.getString(4);
			String title = "";
			
			if (s.equals("PUSHUPS")) title = "Push Ups";
			else if (s.equals("SITUPS")) title = "Sit Ups";
			else if(s.equals("CUSTOM")) title = cursor.getString(5);

			TextView type = (TextView) view.getTag(R.id.body_weight_type);
			type.setText(title);

			String date = new SimpleDateFormat("dd/MM/yyyy, HH:mm")
					.format(new Date(cursor.getLong(1)));
			TextView completed = (TextView) view
					.getTag(R.id.body_weight_completed);
			completed.setText("Completed: " + date);

			String sets = cursor.getString(2);
			String reps = cursor.getString(3);

			TextView stats = (TextView) view.getTag(R.id.body_weight_stats);
			stats.setText("Sets: " + sets + " Reps: " + reps);
		}
	}
}
