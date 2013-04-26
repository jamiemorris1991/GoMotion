package com.gomotion;

import com.gomotion.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Dialog used for telling the user if they a GPS signal or not.
 * If they don't, then they cannot close the dialog and start the exercise.
 * 
 * @author Jack Hindmarch
 *
 */
public class SignalDialog extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Builder builder = new AlertDialog.Builder(getActivity());
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_signal, null);
		
		builder.setView(view)
			.setTitle("Waiting for GPS fix")
			.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					getActivity().finish();
				}
			});
		
		return builder.create();
	}
	
}
