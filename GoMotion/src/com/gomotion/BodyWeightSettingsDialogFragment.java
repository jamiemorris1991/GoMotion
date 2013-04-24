package com.gomotion;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gomotion.R;
import com.gomotion.BodyWeightExercise.BodyWeightType;


public class BodyWeightSettingsDialogFragment extends DialogFragment
{
	private int type;
	public final static String SET_CHOICE = "com.gomotion.SET_CHOICE";
	public final static String REP_CHOICE = "com.gomotion.REP_CHOICE";
	public final static String REST_TIME = "com.gomotion.REST_TIME";
	public final static String EXERCISE_NAME = "com.gomotion.EXERCISE_NAME";

	private Spinner setSpinner;
	private Spinner repSpinner;
	private Spinner restSpinner;
	
	private View v;
		
	private Intent intent;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		
		type = getArguments().getInt(HomeScreen.BODY_WEIGHT_TYPE);
				
		switch(type)
		{
			case 1: 
				intent = new Intent(getActivity(), PushUpsActivity.class);
				break;
			case 2:
				intent = new Intent(getActivity(), SitUpsActivity.class);
				break;
			case 3:
				intent = new Intent(getActivity(), PushUpsActivity.class);
				break;
		}
				
		if(type == 3) v = inflater.inflate(R.layout.dialog_body_weight_settings_custom, null);
		else v = inflater.inflate(R.layout.dialog_body_weight_settings, null);
				
		setSpinner = (Spinner) v.findViewById(R.id.set_spinner);
		repSpinner = (Spinner) v.findViewById(R.id.rep_spinner);
		restSpinner = (Spinner) v.findViewById(R.id.rest_spinner);
		
		/** Add numbers to set chooser **/
		List<String> setNums = new LinkedList<String>();
		for(int i = 1; i <= 50; i++)
		{
			setNums.add(String.valueOf(i));
		}
		
		ArrayAdapter<String> setList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, setNums);
		setList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		/** Add numbers to rep chooser **/
		List<String> repNums = new LinkedList<String>();
		for(int i = 1; i <= 200; i++)
		{
			repNums.add(String.valueOf(i));
		}
		
		ArrayAdapter<String> repList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, repNums);
		repList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		/** Add numbers to rest chooser **/
		List<String> restNums = new LinkedList<String>();
		for(int i = 1; i <= 300; i++)
		{
			restNums.add(String.valueOf(i));
		}
		
		ArrayAdapter<String> restList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, restNums);
		restList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		setSpinner.setAdapter(setList);
		repSpinner.setAdapter(repList);
		restSpinner.setAdapter(restList);		
		
		builder.setView(v)
			.setTitle("Push Up Settings")
			.setPositiveButton(R.string.start, new DialogInterface.OnClickListener()
			{	
				public void onClick(DialogInterface dialog, int which)
				{					
					int sets = Integer.valueOf( setSpinner.getSelectedItem().toString() );
					int reps = Integer.valueOf( repSpinner.getSelectedItem().toString() );
					int restTime = Integer.valueOf( restSpinner.getSelectedItem().toString() );
					
					intent.putExtra(SET_CHOICE, sets);
					intent.putExtra(REP_CHOICE, reps);
					intent.putExtra(REST_TIME, restTime);
					
					if(type == 3)
					{
						EditText field = (EditText) v.findViewById(R.id.custom_exercise_name);
						String exerciseName = field.getText().toString();
						if(exerciseName.length() < 3) 
						{
							new AlertDialog.Builder(BodyWeightSettingsDialogFragment.this.getActivity())
							.setTitle("Error")
							.setMessage("Custom exercises must have a name over 3 characters long.")
							.setCancelable(true)
							.setNegativeButton("Cancel", null)
							.show();
						}
						else
						{
							intent.putExtra(EXERCISE_NAME, exerciseName);
							startActivity(intent);
						}
					}
					else startActivity(intent);
					
				 					
//					EditText setsEditText = (EditText) v.findViewById(R.id.set_choice);
//					EditText repsEditText = (EditText) v.findViewById(R.id.rep_choice);
//					EditText restTimeText = (EditText) v.findViewById(R.id.rest_time);
//					
//					int sets = Integer.valueOf( setsEditText.getText().toString() );
//					int reps = Integer.valueOf( repsEditText.getText().toString() );
//					int restTime = Integer.valueOf( restTimeText.getText().toString() );
					
					// Error checking, if the values have not been set then it uses the default values - Done by BeN
					// Feel free to change it
					// It works when only one hasn't been changed, but does not work if all of them has not been entered
					
//					if (sets <= 0 || reps <= 0 || restTime <= 0) {
//						// close dialog
//					} else {
//						intent.putExtra(SET_CHOICE, sets);
//						intent.putExtra(REP_CHOICE, reps);
//						intent.putExtra(REST_TIME, restTime);
//	
//						startActivity(intent);
//					}
					// End of error checking
				}
			})
			.setNeutralButton(R.string.defaultButton, new DialogInterface.OnClickListener()
			{				
				public void onClick(DialogInterface dialog, int which)
				{
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String[] defaultValues = sharedPref.getString(SettingsActivity.BODY_WEIGHT_VALUES, "5,10,60").split(",");
					
					setSpinner.setSelection(Integer.valueOf(defaultValues[0]) - 1);
					repSpinner.setSelection(Integer.valueOf(defaultValues[1]) - 1);
					restSpinner.setSelection(Integer.valueOf(defaultValues[2]) - 1);
					
					int sets = Integer.valueOf( setSpinner.getSelectedItem().toString() );
					int reps = Integer.valueOf( repSpinner.getSelectedItem().toString() );
					int restTime = Integer.valueOf( restSpinner.getSelectedItem().toString() );
					
					intent.putExtra(SET_CHOICE, sets);
					intent.putExtra(REP_CHOICE, reps);
					intent.putExtra(REST_TIME, restTime);
					
					if(type == 3)
					{
						EditText field = (EditText) v.findViewById(R.id.custom_exercise_name);
						String exerciseName = field.getText().toString();
						intent.putExtra(EXERCISE_NAME, exerciseName);
					}
					
					startActivity(intent);

//					setsEditText.setText(String.valueOf(defaultSets));
//					repsEditText.setText(String.valueOf(defaultReps));
//					restTimeText.setText(String.valueOf(defaultRest));
				}
			})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{				
			public void onClick(DialogInterface dialog, int which)
			{
				BodyWeightSettingsDialogFragment.this.getDialog().cancel();				
			}
		});
		
		return builder.create();
	}
}
