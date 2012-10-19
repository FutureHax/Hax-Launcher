package com.t3hh4xx0r.haxlauncher.preferences;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.livepanel.PanelMenuActivity;

public class PreferencesFragment extends PreferenceFragment {
	
	 private static final String TAG = "Launcher.Preferences";
	    Preference folderBg;
	    Preference folderExpandedBg;
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    
	    boolean mDualPane;
		int mCurCheckPosition = 0;
	    

		@Override
		public void onActivityCreated (Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);



			// Check to see if we have a frame in which to embed the details
	        // fragment directly in the containing UI.

	        View detailsFrame = getActivity().findViewById(R.id.details);
	        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	        
	        //getView().findViewById(android.R.id.list);
	        if (mDualPane) {
	            // In dual-pane mode, the list view highlights the selected item.
	        	((ListView)getView().findViewById(android.R.id.list)).setChoiceMode(ListView.CHOICE_MODE_SINGLE);	          
	        	// Make sure our UI is in the correct state.
	           showDetails(mCurCheckPosition);
	        }else{
	        	((ListView)getView().findViewById(android.R.id.list)).setChoiceMode(ListView.CHOICE_MODE_NONE);
	        }

		}
		
		

		/**
	     * Helper function to show the details of a selected item, either by
	     * displaying a fragment in-place in the current UI, or starting a
	     * whole new activity in which it is displayed.
	     */
	    void showDetails(int index) {
	        mCurCheckPosition = index;

	        if (mDualPane) {
	        	boolean needReplace = false;
	        	Fragment details = getFragmentManager().findFragmentById(R.id.details);

	            FragmentTransaction ft = getFragmentManager().beginTransaction();


	            switch(index){
	    		
		    		case DetailsFragmentManager.Interface.HOMESCREEN:
		    			/*
		    			 * If the details fragment is the fragment we wish to use
		    			 * we dont need to do anything further 
		    			 */
	    	            if (!(details instanceof HomeScreenFragment)) {
	    	                // Make new fragment to show this selection.
	    	                details =  DetailsFragmentManager.newInstance(index);
	    	                needReplace = true;
	    	            }
		    		break;
		    		case DetailsFragmentManager.Interface.MENU:
		    			/*
		    			 * If the details fragment is the fragment we wish to use
		    			 * we dont need to do anything further 
		    			 */
	    	            if (!(details instanceof MenuFragment)) {
	    	                // Make new fragment to show this selection.
	    	                details =  DetailsFragmentManager.newInstance(index);
	    	                needReplace = true;
	    	            }
		    		break;
		    		case DetailsFragmentManager.General.ADVANCED:
		    			/*
		    			 * If the details fragment is the fragment we wish to use
		    			 * we dont need to do anything further 
		    			 */
	    	            if (!(details instanceof AdvancedFragment)) {
	    	                // Make new fragment to show this selection.
	    	                details =  DetailsFragmentManager.newInstance(index);
	    	                needReplace = true;
	    	            }
		    		break;
		    		case DetailsFragmentManager.Interface.FOLDERS:break;
		    		case DetailsFragmentManager.Interface.DOCK:break;
		    		case DetailsFragmentManager.Interface.DRAWER:break;
		    		case DetailsFragmentManager.Interface.ICON:break;
		    		case DetailsFragmentManager.General.PUSH:		    			
	    	            if (!(details instanceof PushFragment)) {
	    	                // Make new fragment to show this selection.
	    	                details =  DetailsFragmentManager.newInstance(index);
	    	                needReplace = true;
	    	            }
		    		break;
		    		case DetailsFragmentManager.ABOUT:		    			
	    	            if (!(details instanceof AboutFragment)) {
	    	                // Make new fragment to show this selection.
	    	                details =  DetailsFragmentManager.newInstance(index);
	    	                needReplace = true;
	    	            }
		    		break;		    		
	            }
	          
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
	            if(needReplace){
	            	ft.replace(R.id.details, details);
                	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                	ft.commit();
                }

	        } else {
	        	Intent intent = new Intent();
	        	intent.putExtra("index", index);
		        intent.setClass(getActivity(), SingleFragmentActivity.class);                
		        startActivity(intent);
	         }
	    }
	    
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences);
	   }
	   
	   public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {			
			String key = preference.getKey();
			if(key.equals("ui_homescreen")){
				showDetails(DetailsFragmentManager.Interface.HOMESCREEN);
			}
			if(key.equals("ui_menu")){
				showDetails(DetailsFragmentManager.Interface.MENU);
			}			
			if(key.equals("general_push")){
				showDetails(DetailsFragmentManager.General.PUSH);
			}
			if(key.equals("general_advanced")){
				showDetails(DetailsFragmentManager.General.ADVANCED);
			}
			if(key.equals("about")){
				showDetails(DetailsFragmentManager.ABOUT);
			}
			if(key.equals("panels_details")){
				Intent i = new Intent(this.getActivity(), PanelMenuActivity.class);
				startActivity(i);
			}
		    return false;
		}
}
