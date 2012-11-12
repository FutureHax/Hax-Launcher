package com.t3hh4xx0r.haxlauncher.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.t3hh4xx0r.haxlauncher.R;

public class AddActivity extends FragmentActivity {
	ViewPager pager;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_activity);
	   		
		DockItemView.canLaunch = true;
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
        
        pager.setCurrentItem(0);
	}
	
	public class ExamplePagerAdapter extends FragmentPagerAdapter {

		public ExamplePagerAdapter(FragmentManager fm) {
	       super(fm);
		}
			
		@Override
		public int getCount() {
		    return 1;
		}
		
		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new AddFragment();
			} else {
				return null;
			}
	    }
		
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	return "title";
	    }
		
	}
	
}
