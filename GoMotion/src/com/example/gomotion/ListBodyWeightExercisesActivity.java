package com.example.gomotion;

import java.text.SimpleDateFormat;

import java.util.Date;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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


public class ListBodyWeightExercisesActivity extends ListActivity 
{
	private OfflineDatabase db;
	private BodyWeightAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.activity_list_body_weight_exercises);
		getActionBar().setDisplayHomeAsUpEnabled(true);

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
	protected void onListItemClick(ListView l, View v, int position, final long id)
	{		
		String items[] = {"Delete"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Options")
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if(item == 0) 
					{
						db.deleteBodyWeightExercise((int) id);
						adapter.changeCursor(db.getAllBodyWeightExercises());
					}
				}
			});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public class BodyWeightAdapter extends CursorAdapter
	{
		private final LayoutInflater mInflater;

		public BodyWeightAdapter(Context context, Cursor cursor)
		{
			super(context, cursor, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) 
		{
			View view =  mInflater.inflate(R.layout.body_weight_exercise_info, parent, false);

			view.setTag(R.id.body_weight_type, view.findViewById(R.id.body_weight_type));
			view.setTag(R.id.body_weight_completed, view.findViewById(R.id.body_weight_completed));
			view.setTag(R.id.body_weight_stats, view.findViewById(R.id.body_weight_stats));

			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) 
		{
			char[] charArray = cursor.getString(4).toLowerCase().toCharArray();
			charArray[0] = Character.toUpperCase(charArray[0]);
			String title = new String(charArray);
			TextView type = (TextView) view.getTag(R.id.body_weight_type);
			type.setText(title);

			String date = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(new Date(cursor.getLong(1)));
			TextView completed = (TextView) view.getTag(R.id.body_weight_completed);
			completed.setText("Completed: " +  date);

			String sets = cursor.getString(2);
			String reps = cursor.getString(3);

			TextView stats = (TextView) view.getTag(R.id.body_weight_stats);
			stats.setText("Sets: " + sets + " Reps: " + reps);	
		}
	}
}
