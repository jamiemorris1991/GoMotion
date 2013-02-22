package com.example.gomotion;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ListCardioExercisesActivity extends ListActivity 
{
	public static final String EXERCISE_ID = "com.example.gomotion.EXERCISE_ID";
	
	private OfflineDatabase db;
	private CardioAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.activity_list_body_weight_exercises);
		getActionBar().setDisplayHomeAsUpEnabled(true);

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
		String items[] = {"Show Route", "Delete"};
		
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
					else if(item == 1) 
					{
						db.deleteCardioExercise(cid);
						adapter.changeCursor(db.getAllCardioExercises());
					}
				}
			});
		
		AlertDialog dialog = builder.create();
		dialog.show();
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
			char[] charArray = cursor.getString(4).toLowerCase().toCharArray();
			charArray[0] = Character.toUpperCase(charArray[0]);
			String title = new String(charArray);
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
