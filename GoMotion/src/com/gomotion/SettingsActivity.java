package com.gomotion;

import com.gomotion.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity
{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {     
	    super.onCreate(savedInstanceState);        
	    addPreferencesFromResource(R.xml.preferences);  
	    
//	    Preference accuracy = findPreference("accuracy");
//	    
//	    accuracy.setOnPreferenceClickListener(new OnPreferenceClickListener() {			
//			public boolean onPreferenceClick(Preference preference) {
//				RouteAccuracyPreference accuracyPref = new RouteAccuracyPreference(this, accuracy.get);
//				return false;
//			}
//		});
	}


}
