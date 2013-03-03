package com.example.gomotion;

import com.example.gomotion.BodyWeightExercise.BodyWeightType;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class BodyWeightSettingsDialogFragment extends DialogFragment
{
	private BodyWeightType type;
	public final static String SET_CHOICE = "com.example.gomotion.SET_CHOICE";
	public final static String REP_CHOICE = "com.example.gomotion.REP_CHOICE";
	public final static String REST_TIME = "com.example.gomotion.REST_TIME";
	private Integer defaultSets = 10;
	private Integer defaultReps = 10;
	private Integer defaultRest = 30;

	public BodyWeightSettingsDialogFragment(BodyWeightType type)
	{
		this.type = type;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_body_weight_settings, null);
		
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
				 					
					EditText setsEditText = (EditText) v.findViewById(R.id.set_choice);
					EditText repsEditText = (EditText) v.findViewById(R.id.rep_choice);
					EditText restTimeText = (EditText) v.findViewById(R.id.rest_time);
					
					int sets = Integer.valueOf( setsEditText.getText().toString() );
					int reps = Integer.valueOf( repsEditText.getText().toString() );
					int restTime = Integer.valueOf( restTimeText.getText().toString() );
					
					// Error checking, if the values have not been set then it uses the default values - Done by BeN
					// Feel free to change it
					// It works when only one hasn't been changed, but does not work if all of them has not been entered
					
					if (sets <= 0 || reps <= 0 || restTime <= 0) {
						// close dialog
					} else {
						intent.putExtra(SET_CHOICE, sets);
						intent.putExtra(REP_CHOICE, reps);
						intent.putExtra(REST_TIME, restTime);
	
						startActivity(intent);
					}
					// End of error checking
				}
			})
			.setNeutralButton(R.string.defaultButton, new DialogInterface.OnClickListener()
			{				
				public void onClick(DialogInterface dialog, int which)
				{
					
					EditText setsEditText = (EditText) v.findViewById(R.id.set_choice);
					EditText repsEditText = (EditText) v.findViewById(R.id.rep_choice);
					EditText restTimeText = (EditText) v.findViewById(R.id.rest_time);
					
					setsEditText.setText(String.valueOf(defaultSets));
					repsEditText.setText(String.valueOf(defaultReps));
					restTimeText.setText(String.valueOf(defaultRest));
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
