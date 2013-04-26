package com.gomotion;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * A settings dialog that allows the user to use a seek bar 
 * to select a number percentage (between 20-100)
 * 
 * @author Jack Hindmarch
 *
 */
public class SeekBarPreference extends DialogPreference
{
	private final int DEFAULT_VALUE = 100;
	private SeekBar seekBar;
	private int value;

    public SeekBarPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        setDialogLayoutResource(R.layout.route_accuracy_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        
        setDialogIcon(null);
    }
    
    public int getProgress()
    {
    	return value;
    }
        
    @Override
	protected void onBindDialogView(View view)
    {
    	seekBar = (SeekBar) view.findViewById(R.id.route_accuracy);
    	seekBar.setProgress(value);

    	seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(progress < 20) seekBar.setProgress(20);
			}
		});
    	
		super.onBindDialogView(view);
	}

	@Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInteger(index, DEFAULT_VALUE);
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
    {
        if (restorePersistedValue) {
            // Restore existing state
            value = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            value = (Integer) defaultValue;
            persistInt(value);
        }
    }    

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        // When the user selects "OK", persist the new value
        if (positiveResult)
        {
        	value = seekBar.getProgress();
            persistInt(value);
        }
    }

}
