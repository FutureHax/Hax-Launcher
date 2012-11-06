package com.t3hh4xx0r.haxlauncher.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.t3hh4xx0r.haxlauncher.R;

public class AdvancedFragment extends PreferenceFragment {
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    PreferenceScreen prefs;
	    CheckBoxPreference enableRootProcessKiller;
	    
	    public static final String ENABLE_ROOT_PROCESS_KILL = "com.t3hh4xx0r.haxlauncher.general_advanced_enable_root"; 
	    public static final String HAS_ROOT = "com.t3hh4xx0r.haxlauncher.general_advanced_has_root"; 
	   	    
	    boolean hasRoot;
	    
	    GestureDetector gestureDetector;
	    
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences_advanced);
	        
	        sharedPrefs = this.getActivity().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
	        prefs = getPreferenceScreen();
	        enableRootProcessKiller = (CheckBoxPreference) prefs.findPreference(ENABLE_ROOT_PROCESS_KILL);
	        editor = sharedPrefs.edit();
	      	editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
	      	editor.commit();	        
	      	
	      	setCurrentValues();
	      	
	        gestureDetector = new GestureDetector(this.getActivity(), new GestureListener());

	   }

	private void setCurrentValues() {
		hasRoot = PreferencesProvider.General.Advanced.getDeviceHasRoot(this.getActivity());
		
		if (hasRoot) {
			enableRootProcessKiller.setChecked(PreferencesProvider.General.Advanced.getEnableRootProcessKiller(this.getActivity()));
			if (PreferencesProvider.General.Advanced.getEnableRootProcessKiller(this.getActivity())) {
				enableRootProcessKiller.setSummary(R.string.preferences_general_advanced_enable_root_summary_on);
			} else {
				enableRootProcessKiller.setSummary(R.string.preferences_general_advanced_enable_root_summary_off);
			}

		} else {
			enableRootProcessKiller.setChecked(false);
			enableRootProcessKiller.setEnabled(false);
			enableRootProcessKiller.setSummary("No root access detected!");
		}
	}

	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
		String key = preference.getKey();
		
		if(key.equals(ENABLE_ROOT_PROCESS_KILL)){
			boolean checked = ((CheckBoxPreference)preference).isChecked();
			editor.putBoolean(ENABLE_ROOT_PROCESS_KILL, checked);
	        editor.commit();
	        
	        if (checked) {
	        	enableRootProcessKiller.setSummary(R.string.preferences_general_advanced_enable_root_summary_on);
			} else {
				enableRootProcessKiller.setSummary(R.string.preferences_general_advanced_enable_root_summary_off);				
	        }
		}
	    return false;
	}
	
	public boolean onTouchEvent(MotionEvent e) {
	    return gestureDetector.onTouchEvent(e);
	}


	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

	    @Override
	    public boolean onDown(MotionEvent e) {
	        return true;
	    }
	    // event when double tap occurs
	    @Override
	    public boolean onDoubleTap(MotionEvent e) {
	        float x = e.getX();
	        float y = e.getY();

	        Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

	        return true;
	    }
	}
}
