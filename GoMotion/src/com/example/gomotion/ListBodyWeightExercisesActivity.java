package com.example.gomotion;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
public class ListBodyWeightExercisesActivity extends ListActivity 
{

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.activity_list_body_weight_exercises);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		OfflineDatabase db = new OfflineDatabase(this);
		Cursor exercises = db.getAllBodyWeightExercises();
		setListAdapter(new BodyWeightAdapter(this, exercises));
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
	//
	//	@Override
	//	protected void onListItemClick(ListView l, View v, int position, long id)
	//	{
	//		super.onListItemClick(l, v, position, id);
	//		System.out.println("ListView: " + l);
	//		System.out.println("View: " + v);
	//		System.out.println("Pos: " + position);
	//		System.out.println("id: " + id);
	//	}

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
