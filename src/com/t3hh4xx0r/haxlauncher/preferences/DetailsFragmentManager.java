package com.t3hh4xx0r.haxlauncher.preferences;

import android.app.Fragment;
import android.os.Bundle;

public class DetailsFragmentManager {

	/**
	 * The Entries here coresspond with res/xml/preferences.xml
	 * They are the PreferenceScreens entries under the category with
	 * android:title="@string/preferences_interface_title"
	 * The order of the screen determines the numbering
	 * @author John Weyrauch
	 * @version 0.1
	 * 
	 */
	public static final int ABOUT = 420;

	public static final class Interface{	
		/**
		 * Coresponds to the key "ui_homescreen"
		 */
		public static final int HOMESCREEN = 0;
		/**
		 * Coresponds to the key "ui_menu"
		 */
		public static final int MENU = 1;
		/**
		 * Coresponds to the key "ui_folders"
		 */
		public static final int FOLDERS = 2;
		/**
		 * Coresponds to the key "ui_drawer"
		 */
		public static final int DRAWER = 3;
		/**
		 * Coresponds to the key "ui_dock"
		 */
		public static final int DOCK = 4;
		/**
		 * Coresponds to the key "ui_icons"
		 */
		public static final int ICON = 5;	
				
	}
	
	public static final class General{	
		/**
		 * Coresponds to the key "general_push"
		 */
		public static final int PUSH = 6;
		/**
		 * Coresponds to the key "general_advanced"
		 */
		public static final int ADVANCED = 7;
	}
	
	
	
	
	
	 public static Fragment newInstance(int index) { 
		 Fragment mShownFragment = null;
		 switch(index){
			 		
			 		case Interface.HOMESCREEN:
			 			HomeScreenFragment hsf = new HomeScreenFragment();
				        // Supply index input as an argument.
				        Bundle args = new Bundle();
				        args.putInt("index", index);
				        hsf.setArguments(args);
				        mShownFragment = hsf;
			 		break;
			 		case Interface.FOLDERS:
			 			/**
			 			 * TODO:
			 			 */
			 		break;
			 		case Interface.DRAWER:
			 			/**
			 			 * TODO:
			 			 */
			 		break;
			 		case Interface.DOCK:
			 			/**
			 			 * TODO:
			 			 */
			 		break;
			 		case Interface.ICON:
			 			/**
			 			 * TODO:
			 			 */
			 		break;
			 		case General.PUSH:
			 			PushFragment pf = new PushFragment();
				        // Supply index input as an argument.
				        Bundle b = new Bundle();
				        b.putInt("index", index);
				        pf.setArguments(b);
				        mShownFragment = pf;
			 		break;
			 		case General.ADVANCED:
			 			AdvancedFragment af = new AdvancedFragment();
				        // Supply index input as an argument.
				        Bundle b2 = new Bundle();
				        b2.putInt("index", index);
				        af.setArguments(b2);
				        mShownFragment = af;
			 		break;
			 		case Interface.MENU:
			 			MenuFragment mf = new MenuFragment();
				        // Supply index input as an argument.
				        Bundle b1 = new Bundle();
				        b1.putInt("index", index);
				        mf.setArguments(b1);
				        mShownFragment = mf;
				    break;
			 		case ABOUT:
			 			AboutFragment about = new AboutFragment();
				        // Supply index input as an argument.
				        Bundle aboutBundle = new Bundle();
				        aboutBundle.putInt("index", index);
				        about.setArguments(aboutBundle);
				        mShownFragment = about;
				    break;
			     }
	    	

	        return mShownFragment;
	    }
	
}
