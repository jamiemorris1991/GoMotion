package com.gomotion;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gomotion.R;
import com.gomotion.BodyWeightExercise.BodyWeightType;


public class BodyWeightSettingsDialogFragment extends DialogFragment
{
	private BodyWeightType type;
	public final static String SET_CHOICE = "com.gomotion.SET_CHOICE";
	public final static String REP_CHOICE = "com.gomotion.REP_CHOICE";
	public final static String REST_TIME = "com.gomotion.REST_TIME";
	
	private Spinner setSpinner;
	private Spinner repSpinner;
	private Spinner restSpinner;
	
	private Integer defaultSets = 10;
	private Integer defaultReps = 10;
	private Integer defaultRest = 30;

	
	public BodyWeightSettingsDialogFragment()
	{
		this.type = BodyWeightType.PUSHUPS;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_body_weight_settings, null);
		
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
					Intent intent = null;
					
					switch(type)
					{
						case PUSHUPS: 
							intent = new Intent(getActivity(), PushUpsActivity.class);
							break;
					}
					

					
					int sets = Integer.valueOf( setSpinner.getSelectedItem().toString() );
					int reps = Integer.valueOf( repSpinner.getSelectedItem().toString() );
					int restTime = Integer.valueOf( restSpinner.getSelectedItem().toString() );
					
					intent.putExtra(SET_CHOICE, sets);
					intent.putExtra(REP_CHOICE, reps);
					intent.putExtra(REST_TIME, restTime);
					
					startActivity(intent);
					
				 					
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
					setSpinner.setSelection(defaultSets - 1);
					repSpinner.setSelection(defaultReps - 1);
					restSpinner.setSelection(defaultRest - 1);

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
