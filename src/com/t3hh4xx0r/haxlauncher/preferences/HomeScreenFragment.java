package com.t3hh4xx0r.haxlauncher.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxlauncher.R;

public class HomeScreenFragment extends PreferenceFragment {
	
	 private static final String TAG = "Launcher.HomeScreenFragment";
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    public static final String SEARCH = "com.t3hh4xx0r.haxlauncher.ui_homescreen_general_search"; 

	    
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences_homescreen);
	        

	        sharedPrefs = this.getActivity().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
	            editor = sharedPrefs.edit();
	            editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
	            editor.commit();	        
	   }

	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
		String key = preference.getKey();
		
		if (key.equals("ui_homescreen_general_search")){
			boolean checked = ((CheckBoxPreference)preference).isChecked();
			editor.putBoolean(SEARCH, checked);
	        editor.commit();
		}
		
	    return false;
	}
}
