package com.t3hh4xx0r.haxlauncher.parse;

import android.content.Context;

import com.parse.Parse;
import com.parse.PushService;
import com.t3hh4xx0r.haxlauncher.Launcher;
import com.t3hh4xx0r.haxlauncher.preferences.PreferencesProvider;

public 	class ParseHelper {

	public static void init(Context c) {
		Parse.initialize(c, "SfsXKvpEwGkTKjQ9pGtReXL8kq3xZYtkDNu52cVh", "eYw2HSaoz2p6AlotN63JoHtXSOtFwX8ZnQ3KOVmf");
	}
	
	public static void registerForPush(Context c) {
		if (PreferencesProvider.General.Push.getPushChannels(c)[0]) {
			PushService.subscribe(c, "", Launcher.class);
			
			
			if (PreferencesProvider.General.Push.getPushChannels(c)[1]) {
	    		PushService.subscribe(c, "testing", Launcher.class);
			} else {
				PushService.unsubscribe(c, "testing");
			}
			
			
			if (PreferencesProvider.General.Push.getPushChannels(c)[2]) {
	    		PushService.subscribe(c, "updates", Launcher.class);
			} else {
				PushService.unsubscribe(c, "updates");
			}
			
			
		} else {
			PushService.unsubscribe(c, "updates");
			PushService.unsubscribe(c, "testing");
			PushService.unsubscribe(c, "");
		}
	}
}
