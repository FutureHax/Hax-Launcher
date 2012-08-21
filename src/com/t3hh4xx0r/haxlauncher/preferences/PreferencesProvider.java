package com.t3hh4xx0r.haxlauncher.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferencesProvider {
    public static final String PREFERENCES_KEY = "com.t3hh4xx0r.haxlauncher_preferences";
    public static final String PREFERENCES_CHANGED = "preferences_changed";

    public static class Interface {
        public static class Homescreen {
        	public static boolean getShowSearchBar(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getBoolean("ui_homescreen_general_search", true);
        	}
        }

        public static class Drawer {

        }

        public static class Dock {

        }

        public static class Icons {

        }

		public static class  LivePanel {
        	public static boolean getEnableWeather(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getBoolean("ui_live_weather", true);
        	}
        	public static int getWeatherInterval(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getInt("ui_live_weather_interval", 60);
        	}        	
        	public static boolean getUseMetric(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getBoolean("ui_live_weather_metric", false);
        	}        	
		}
    }

    public static class General {
      	public static boolean[] getPushChannels(Context context) {
      		boolean values[] = new boolean[2];
    		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
    		values[0] = preferences.getBoolean("_push_enable", true);    		
    		values[1] = preferences.getBoolean("_push_channel_test", true);    		
    		return values;
    	}
    }
}
