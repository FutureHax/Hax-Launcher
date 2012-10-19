package com.t3hh4xx0r.haxlauncher.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.livepanel.PanelReceiver;

public class AdvancedFragment extends PreferenceFragment {
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    PreferenceScreen prefs;
	    CheckBoxPreference enableRootProcessKiller;
	    
	    public static final String ENABLE_ROOT_PROCESS_KILL = "com.t3hh4xx0r.haxlauncher.general_advanced_enable_root"; 
	    public static final String HAS_ROOT = "com.t3hh4xx0r.haxlauncher.general_advanced_has_root"; 
	   	    
	    boolean hasRoot;
	    
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
}
