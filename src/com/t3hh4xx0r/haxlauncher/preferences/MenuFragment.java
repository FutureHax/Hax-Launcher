package com.t3hh4xx0r.haxlauncher.preferences;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxlauncher.R;

public class MenuFragment extends PreferenceFragment implements OnPreferenceChangeListener{
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    PreferenceScreen prefs;
	    public static final String SHADE_COLOR = "com.t3hh4xx0r.haxlauncher.ui_menu_shadeColor"; 
	    public static final String SHADE_SHOW = "com.t3hh4xx0r.haxlauncher.ui_menu_showOnRestore"; 

	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences_menu);
	        
	        sharedPrefs = this.getActivity().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
	        prefs = getPreferenceScreen();
	        editor = sharedPrefs.edit();
	      	editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
	      	editor.commit();	        
	      	
			ColorPickerPreference color = (ColorPickerPreference) prefs.findPreference(SHADE_COLOR);
			color.setOnPreferenceChangeListener(this);
			color.setAlphaSliderEnabled(true);
			color.setSummary(ColorPickerPreference.convertToARGB(sharedPrefs.getInt(SHADE_COLOR, 00000)));
			
			CheckBoxPreference show = (CheckBoxPreference) prefs.findPreference(SHADE_SHOW);
			show.setOnPreferenceChangeListener(this);
			if (sharedPrefs.getBoolean(SHADE_SHOW, false)) {
				show.setSummary(getResources().getString(R.string.preferences_interface_menu_show_restore_summary_on));
			} else {
				show.setSummary(getResources().getString(R.string.preferences_interface_menu_show_restore_summary_off));
			}
	   }

	   @Override
	   public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		if (key.equals(SHADE_COLOR)) {
			preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
			editor.putInt(SHADE_COLOR,  Color.parseColor(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)))));
			editor.commit();
		}
		if (key.equals(SHADE_SHOW)) {
			if (((CheckBoxPreference) preference).isChecked()) {
				preference.setSummary(getResources().getString(R.string.preferences_interface_menu_show_restore_summary_on));
				editor.putBoolean(SHADE_SHOW, true);
			} else {
				preference.setSummary(getResources().getString(R.string.preferences_interface_menu_show_restore_summary_on));
				editor.putBoolean(SHADE_SHOW, false);
			}
			editor.commit();
		}
		return true;
	}

}
