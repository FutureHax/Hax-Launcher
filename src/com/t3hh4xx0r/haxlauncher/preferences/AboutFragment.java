package com.t3hh4xx0r.haxlauncher.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxlauncher.R;

public class AboutFragment extends PreferenceFragment {
	    
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.about_main);
	   }

	   public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
			String key = preference.getKey();
			
			if(key.equals("twitter")){
				String url = "https://twitter.com/r2DoesInc";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			if(key.equals("linkedin")){
				String url = "http://www.linkedin.com/pub/ken-kyger/35/246/a93";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			if(key.equals("gmail")){
				Intent i = new Intent(Intent.ACTION_SEND);  
				i.setType("message/rfc822") ; 
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"r2doesinc@gmail.com"});  
				startActivity(Intent.createChooser(i, "Select email application."));
			}
		    return false;
		}
}
