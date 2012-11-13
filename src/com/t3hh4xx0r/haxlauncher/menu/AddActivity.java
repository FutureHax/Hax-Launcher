package com.t3hh4xx0r.haxlauncher.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.t3hh4xx0r.haxlauncher.R;

public class AddActivity extends FragmentActivity {
	static ViewPager pager;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_activity);
	   		
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {				
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {				
			}
			@Override
			public void onPageSelected(int p) {
				if (p == 1) {
					try {
						ReorderFragment.parseSelected(AddActivity.this);
					} catch (Exception e) {}
				}
			}
        	
        });
        pager.setCurrentItem(0);
	}
	
	public class ExamplePagerAdapter extends FragmentPagerAdapter {

		public ExamplePagerAdapter(FragmentManager fm) {
	       super(fm);
		}
			
		@Override
		public int getCount() {
		    return 2;
		}
		
		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new AddFragment();
			} else {
				return new ReorderFragment();
			}
	    }
		
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	return "title";
	    }
		
	}
	
}
