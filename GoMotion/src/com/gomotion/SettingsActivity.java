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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {     
	    super.onCreate(savedInstanceState);        
	    addPreferencesFromResource(R.xml.preferences);  
	    
	    ListPreference units = (ListPreference) findPreference("units");
	    units.setSummary(units.getEntry());
	    
	    ListPreference colour = (ListPreference) findPreference("route_colour");
	    colour.setSummary(colour.getEntry());
	    
	    RouteAccuracyPreference accuracy = (RouteAccuracyPreference) findPreference("accuracy");
	    accuracy.setSummary(String.valueOf(accuracy.getProgress()) + "%");
	}
	
    protected void onResume() 
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

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
        else if(pref instanceof RouteAccuracyPreference) {
        	RouteAccuracyPreference rap = (RouteAccuracyPreference) pref;
            pref.setSummary(String.valueOf(rap.getProgress()) + "%");
        }
    }
}
