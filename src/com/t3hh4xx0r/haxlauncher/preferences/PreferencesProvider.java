package com.t3hh4xx0r.haxlauncher.preferences;

import com.t3hh4xx0r.haxlauncher.RootChecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

public final class PreferencesProvider {
    public static final String PREFERENCES_KEY = "com.t3hh4xx0r.haxlauncher_preferences";
    public static final String PREFERENCES_CHANGED = "preferences_changed";

    public static final String ENABLE_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable"; 
    public static final String ENABLE_TEST_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable_test"; 
    public static final String ENABLE_UPDATES_PUSH = "com.t3hh4xx0r.haxlauncher.general_push_enable_updates"; 

    public static final String SEARCH = "com.t3hh4xx0r.haxlauncher.ui_homescreen_general_search"; 

    public static final String SHADE_COLOR = "com.t3hh4xx0r.haxlauncher.ui_menu_shadeColor"; 
    public static final String SHADE_SHOW = "com.t3hh4xx0r.haxlauncher.ui_menu_showOnRestore"; 
    
    public static final String MENU_OPEN = "com.t3hh4xx0r.haxlauncher.ui_menu_open"; 
    
    public static final String ENABLE_ROOT_PROCESS_KILL = "com.t3hh4xx0r.haxlauncher.general_advanced_enable_root"; 
    public static final String HAS_ROOT = "com.t3hh4xx0r.haxlauncher.general_advanced_has_root"; 

    public static class Interface {
        public static class Homescreen {
        	public static boolean getShowSearchBar(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getBoolean(SEARCH, true);
        	}
        }

        public static class Menu {
        	public static int getMenuColor(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getInt(SHADE_COLOR, -1744830464);
        	}
        	
        	public static boolean showMenuOnRestore(Context c) {
        		final SharedPreferences preferences = c.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getBoolean(SHADE_SHOW, false);
        	}
        	
//        	public static boolean menuOpen(Context c) {
//        		final SharedPreferences preferences = c.getSharedPreferences(PREFERENCES_KEY, 0);
//        		return preferences.getBoolean(MENU_OPEN, false);
//        	}
//        	
//        	public static void setMenuOpenStatus(boolean open, Context c) {
//        		 SharedPreferences.Editor e = c.getSharedPreferences(PREFERENCES_KEY, 0).edit();
//        		 e.putBoolean(MENU_OPEN, open);
//        		 e.commit();
//        	}
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
        		return preferences.getBoolean("com.t3hh4xx0r.haxlauncher.ui_live_weather", true);
        	}
        	public static int getWeatherInterval(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getInt("com.t3hh4xx0r.haxlauncher.ui_live_weather_interval", 60);
        	}        	
        	public static boolean getUseMetric(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		return preferences.getBoolean("com.t3hh4xx0r.haxlauncher.ui_live_weather_metric", false);
        	}        	
		}
    }

    public static class General {
        public static class Push {
	      	public static boolean[] getPushChannels(Context context) {
	      		boolean values[] = new boolean[3];
	    		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
	    		values[0] = preferences.getBoolean(ENABLE_PUSH, true);    		
	    		values[1] = preferences.getBoolean(ENABLE_TEST_PUSH, false); 
	    		values[2] = preferences.getBoolean(ENABLE_UPDATES_PUSH, true); 		    		
	    		return values;
	    	}
        }
        
        public static class Advanced {
	      	public static boolean getEnableRootProcessKiller(Context context) {
	    		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);	    		   		
	    		return preferences.getBoolean(ENABLE_ROOT_PROCESS_KILL, preferences.getBoolean(HAS_ROOT, false)); 
	    	}
	      	
	      	public void enableRootProcessKiller(Context c, boolean enable) {
	    		final SharedPreferences preferences = c.getSharedPreferences(PREFERENCES_KEY, 0);	    		   		
	    		preferences.edit().putBoolean(ENABLE_ROOT_PROCESS_KILL, enable).commit();    		
	      	}
	      	
	      	public static boolean getDeviceHasRoot(Context context) {
	    		return RootChecker.isDeviceRooted();
	      	}
        }
    }
}
