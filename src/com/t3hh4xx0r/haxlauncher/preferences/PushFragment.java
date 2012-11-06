package com.t3hh4xx0r.haxlauncher.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.parse.ClientPushActivity;
import com.t3hh4xx0r.haxlauncher.parse.ParseHelper;
import com.t3hh4xx0r.haxlauncher.parse.PushLogin;

public class PushFragment extends PreferenceFragment {
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    PreferenceScreen prefs;
	    public static final String ENABLE_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable"; 
	    public static final String ENABLE_TEST_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable_test"; 
	    public static final String ENABLE_UPDATES_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable_updates"; 
	    public static final String REGISTER_CLIENT_PUSH = "com.t3hh4xx0r.haxlauncher.register_client_push";
	    public static final String VIEW_CLIENT_PUSH = "com.t3hh4xx0r.haxlauncher.view_client_push";
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences_push);
	        
	        sharedPrefs = this.getActivity().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
	        prefs = getPreferenceScreen();
	        editor = sharedPrefs.edit();
	      	editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
	      	editor.commit();	        
	      	
	      	if (PushLogin.PrivatePushPreferencesProvider.getHasClientPush(this.getActivity())) { 
	      		Preference p = prefs.findPreference(VIEW_CLIENT_PUSH);
	      		p.setEnabled(true);
	      	}
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
		if (key.equals(ENABLE_PUSH) ||
				key.equals(ENABLE_TEST_PUSH) ||
				key.equals(ENABLE_UPDATES_PUSH)) {
			boolean value = ((CheckBoxPreference)preference).isChecked();
			editor.putBoolean(key, value);
			ParseHelper.registerForPush(this.getActivity());
		} else if (key.equals(REGISTER_CLIENT_PUSH)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
	     	builder.setTitle("You wanna register?");
	     	builder.setMessage("This is a new feature I'll be rolling out to the masses here in a bit.\n" +
	     			"If you'd like early access, you can let me know why you'd like it here. Only a few " +
	     			"people will get access to this service early.");
	     	builder.setIcon(R.drawable.ic_launcher_application);
	     	builder.setPositiveButton("Yes please!", new android.content.DialogInterface.OnClickListener() {
	 			@Override
	 			public void onClick(DialogInterface dialog, int which) {
	 				dialog.cancel();
	 		        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	 		        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"r2doesinc@gmail.com"});
	 		        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, new String[]{"RE:Client Push Request"});
	 		        emailIntent.setType("plain/text");
	 		        startActivity(Intent.createChooser(emailIntent, "Send via"));
	 			}
	     	});
	     	builder.setNegativeButton("No thanks!", new android.content.DialogInterface.OnClickListener() {
	 			@Override
	 			public void onClick(DialogInterface dialog, int which) {
	 				dialog.cancel();
	 			}
	     	});
	     	builder.setNeutralButton("I'm VIP!", new android.content.DialogInterface.OnClickListener() {
	 			@Override
	 			public void onClick(DialogInterface dialog, int which) {	 				
	 				dialog.cancel();
	 				Intent i = new Intent(builder.getContext(), PushLogin.class);
	 				startActivity(i);	 				
	 			}
	     	});
	     	builder.show();	
		} else if (key.equals(VIEW_CLIENT_PUSH) && 
				PushLogin.PrivatePushPreferencesProvider.getHasClientPush(screen.getContext())) {
			Intent i = new Intent(screen.getContext(), ClientPushActivity.class);
			startActivity(i);	
		}
		return false;
	}
		
	@Override
	public void onResume(){
		super.onResume();
		
		if (PushLogin.PrivatePushPreferencesProvider.getHasClientPush(this.getActivity())) { 
      		Preference p = prefs.findPreference(VIEW_CLIENT_PUSH);
      		p.setEnabled(true);
      	}
      	setCurrentValues();
	}
}
