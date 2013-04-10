package com.gomotion;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	public static final String UNITS = "units";
	public static final String ROUTE_COLOUR = "route_colour";
	public static final String ROUTE_TRANSPARENCY = "transparency";
	public static final String ACCURACY = "accuracy";
	public static final String BODY_WEIGHT_VALUES = "body_weight_values";


	@Override
	public void onCreate(Bundle savedInstanceState) {     
	    super.onCreate(savedInstanceState);        
	    addPreferencesFromResource(R.xml.preferences);  
	    
	    ListPreference units = (ListPreference) findPreference("units");
	    units.setSummary(units.getEntry());
	    
	    ListPreference colour = (ListPreference) findPreference("route_colour");
	    colour.setSummary(colour.getEntry());
	    
	    SeekBarPreference transparency = (SeekBarPreference) findPreference("transparency");
	    transparency.setSummary(String.valueOf(transparency.getProgress()) + "%");
	    
	    SeekBarPreference accuracy = (SeekBarPreference) findPreference("accuracy");
	    accuracy.setSummary(String.valueOf(accuracy.getProgress()) + "%");
	    
	    DefaultBodyWeightPreference bwsettings = (DefaultBodyWeightPreference) findPreference("body_weight_values");
	    String[] bwvalues = bwsettings.getValues().split(",");
	    bwsettings.setSummary(String.format("Sets: %s, Reps: %s, Rest: %ss", bwvalues[0], bwvalues[1], bwvalues[2]));
	}
	
    @Override
	protected void onResume() 
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
	protected void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }
        else if(pref instanceof ListPreference) {
        	ListPreference lp = (ListPreference) pref;
            pref.setSummary(lp.getEntry());
        }
        else if(pref instanceof SeekBarPreference) {
        	SeekBarPreference rap = (SeekBarPreference) pref;
            pref.setSummary(String.valueOf(rap.getProgress()) + "%");
        }
        else if(pref instanceof DefaultBodyWeightPreference) {
        	DefaultBodyWeightPreference bw = (DefaultBodyWeightPreference) pref;
    	    String[] bwvalues = bw.getValues().split(",");
            pref.setSummary(String.format("Sets: %s, Reps: %s, Rest: %ss", bwvalues[0], bwvalues[1], bwvalues[2]));
        }
    }
}
