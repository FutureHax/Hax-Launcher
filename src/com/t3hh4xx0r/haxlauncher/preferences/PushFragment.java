package com.t3hh4xx0r.haxlauncher.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.parse.ParseHelper;

public class PushFragment extends PreferenceFragment {
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    PreferenceScreen prefs;
	    public static final String ENABLE_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable"; 
	    public static final String ENABLE_TEST_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable_test"; 
	    public static final String ENABLE_UPDATES_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable_updates"; 

	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences_push);
	        
	        sharedPrefs = this.getActivity().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
	        prefs = getPreferenceScreen();
	        editor = sharedPrefs.edit();
	      	editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
	      	editor.commit();	        
	      	
	      	setCurrentValues();
	   }

	private void setCurrentValues() {
		CheckBoxPreference enable = (CheckBoxPreference) prefs.findPreference(ENABLE_PUSH);
		enable.setChecked(sharedPrefs.getBoolean(ENABLE_PUSH, true));
		
		CheckBoxPreference enableTest = (CheckBoxPreference) prefs.findPreference(ENABLE_TEST_PUSH);
		enableTest.setChecked(sharedPrefs.getBoolean(ENABLE_TEST_PUSH, false));
		
		CheckBoxPreference enableUpdates = (CheckBoxPreference) prefs.findPreference(ENABLE_UPDATES_PUSH);
		enableUpdates.setChecked(sharedPrefs.getBoolean(ENABLE_UPDATES_PUSH, true));		
	}

	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
		String key = preference.getKey();
		boolean value = ((CheckBoxPreference)preference).isChecked();
		editor.putBoolean(key, value);
		ParseHelper.registerForPush(this.getActivity());
		return false;
	}
}
