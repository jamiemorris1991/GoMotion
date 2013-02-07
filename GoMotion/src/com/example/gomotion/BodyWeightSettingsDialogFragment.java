package com.example.gomotion;

import com.example.gomotion.BodyWeightExcercise.BodyweightType;

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
	private BodyweightType type;
	public final static String SET_CHOICE = "com.example.gomotion.SET_CHOICE";
	public final static String REP_CHOICE = "com.example.gomotion.REP_CHOICE";

	public BodyWeightSettingsDialogFragment(BodyweightType type)
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
					
					String sets = setsEditText.getText().toString();
					String reps = repsEditText.getText().toString();
					
					intent.putExtra(SET_CHOICE, sets);
					intent.putExtra(REP_CHOICE, reps);

					startActivity(intent);
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
