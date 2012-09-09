/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t3hh4xx0r.haxlauncher.menu;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.Launcher;
import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.livepanel.music.MusicControlsLivePanel;
import com.t3hh4xx0r.haxlauncher.menu.livepanel.weather.WeatherLivePanel;

public class LauncherMenu extends RelativeLayout {
    SharedPreferences prefs;

    private Launcher mLauncher;
    private View root;
    private ViewFlipper flipper;
    RelativeLayout arrow;
    EditText searchBox;
    boolean attatched = false;
    static Context mContext;
    public LinearLayout dock;
    static String[] hotseatIntents;
    public DockItemView hotseat[];
    Object panel[];
    Cursor c;
    ArrayList<String> panels;
    ScrollView dHolder;
    
    public static final int WEATHER_PANEL_ID = 9999;
    public static final int MUSIC_PANEL_ID = 9998;
    public static final int JEFF_PANEL_ID = 9997;
    
    private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
    private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
    private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;
    private static final int XHIGH_DPI_STATUS_BAR_HEIGHT = 50;
    //TODO fix me
    private static final int TV_DPI_STATUS_BAR_HEIGHT = 50;
    
    private static Animation slideLeftIn;
    private static Animation slideLeftOut;
    private static Animation slideInDown;
    private static Animation slideRightIn;
    private static Animation slideRightOut;
    ViewGroup panelHolder;
    Editor e;
    int tCount = 0;
    boolean showJeff = true;
    RelativeLayout.LayoutParams lp;
    int height;
    ImageView left;
    ImageView right;
    GridView settings_grid;
    
    public LauncherMenu(final Context context) {
        this(context, null, 0);
        mLauncher = (Launcher) context;
        mContext = context;
        
        slideInDown = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_down);
        slideLeftIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
        slideLeftOut = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_left);
        slideRightIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
        slideRightOut = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right);

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = layoutInflater.inflate(R.layout.launcher_menu, this);
    	root.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        Animation slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        root.startAnimation(slideLeftIn);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getHeight();
		lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
				(int) + (height - 5 - getResources().getDimension(R.dimen.button_bar_height)));
		
        root.setLayoutParams(lp);
        

        dHolder = (ScrollView) root.findViewById(R.id.dock_holder);
        left = (ImageView) root.findViewById(R.id.left);
        left.setOnClickListener(listener);
        right = (ImageView) root.findViewById(R.id.right);
        right.setOnClickListener(listener);
        ImageView mainSearch = (ImageView) root.findViewById(R.id.main_search);
        mainSearch.setOnClickListener(listener);
        ImageView mainSettings = (ImageView) root.findViewById(R.id.main_settings);
        mainSettings.setOnClickListener(listener);

        settings_grid = (GridView) root.findViewById(R.id.gridview);
        MenuSettingsAdapter gridA = new MenuSettingsAdapter(mContext);
        settings_grid.setAdapter(gridA);
        searchBox = (EditText) root.findViewById(R.id.search_box);
        final ListView list = (ListView) root.findViewById(R.id.list);
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> v1, View v,
					int pos, long id) {
				boolean found = false;
				 List<ApplicationInfo> applications = v.getContext().getPackageManager().getInstalledApplications(0);
				 if (pos != 0) {
					 for (int n=0; n < applications.size(); n++) {
						if (applications.get(n).loadLabel(v.getContext().getPackageManager()).equals((list.getItemAtPosition(pos)))) {							
							startApplication(applications.get(n).packageName);
							found = true;
							break;
						}
					 }
					 
					 if(!found) {
						 Intent i = new Intent(); 
						 i.setAction(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT); 
						 i.setData(Uri.fromParts("tel", parsePhone(list.getItemAtPosition(pos).toString()), null)); 
						 v.getContext().startActivity(i);
					 }
				 } else {
					 Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
					 intent.putExtra(SearchManager.QUERY, list.getItemAtPosition(pos).toString().replaceAll("\"", "").replace("Search the web for", ""));
					 v.getContext().startActivity(intent);
				 }
			}
        });
        final String lv_arr[]= getSearchableItems(mContext);
        final ArrayList<String> arr_sort= new ArrayList<String>();
        final SearchAdapter a = new SearchAdapter(mContext, arr_sort, (Activity) mContext);
        list.setAdapter(a);
        searchBox.addTextChangedListener(new TextWatcher() {
        	public void afterTextChanged(Editable s) {
        	}

        	public void beforeTextChanged(CharSequence s, int start, int count,
        			int after) {
        	}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				int length = searchBox.getText().length();
				arr_sort.clear();
				if (length != 0) {
					arr_sort.add("Search the web for \""+s.toString()+"\"");
				}
				for(int i=0;i<lv_arr.length;i++){
					if(length<=lv_arr[i].length() && length != 0){
						if(searchBox.getText().toString().equalsIgnoreCase((String) lv_arr[i].subSequence(0, length))){
							arr_sort.add(lv_arr[i]);
						}
					}										
				}
				a.notifyDataSetChanged();
			}					
        });
        flipper = (ViewFlipper) root.findViewById(R.id.flipper);
        android.widget.LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        flipper.setLayoutParams(lp3);
        dock = (LinearLayout) root.findViewById(R.id.dock);
        panelHolder = (ViewGroup) root.findViewById(R.id.panels);
        ImageView apps = (ImageView) findViewById(R.id.all_apps);
        apps.setOnClickListener(listener);
    }

    public LauncherMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LauncherMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    public boolean onTouchEvent(android.view.MotionEvent event) {
//    	super.onTouchEvent(event);
//    	return false;
//    }
    

    public boolean isVisible() {
    	return attatched;
    }
    
    @Override
    protected void onAttachedToWindow() {
    	flipper.setDisplayedChild(1);
    	prefs = PreferenceManager.getDefaultSharedPreferences(mContext);    	
    	e = prefs.edit();
    	attatched = true;
        tCount = prefs.getInt("_interalCount", 0)+1;
        e.putInt("_interalCount", tCount);
        e.apply();   
    	setupHotseats();
        setupLivePanels(mContext);
        Animation slideLeftIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
        final Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_fast);
        final ImageView shade = (ImageView) mLauncher.findViewById(R.id.shade);
        shade.setLayoutParams(getCustomLayoutParams());
        root.startAnimation(slideLeftIn);	        
		shade.startAnimation(fadeIn);
		shade.setVisibility(View.VISIBLE);
		
        LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,  
        		//TODO fix me
				(int) + (height - getResources().getDimension(R.dimen.button_bar_height) - getSBHeight() - 10));
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        dHolder.setLayoutParams(lp2);
		if (dock.getChildCount() != 0) {
			AnimateDockTask d = new AnimateDockTask();
			Object[] dArray;		
			dArray = new Object[dock.getChildCount()];
			dArray[0] = 0;
			for (int i=0;i<dock.getChildCount();i++) {	
				dArray[i] = dock.getChildAt(i);
			}		
			d.execute(dArray);
		}
		
		left.setVisibility(View.GONE);
		right.setVisibility(View.GONE);
		super.onAttachedToWindow();
    }
    
    private FrameLayout.LayoutParams getCustomLayoutParams() {
    	    	
    	//TODO Fix me
    	int indicatorHeight = 5;
    	    	
    	return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 
				(int) + (height - getResources().getDimension(R.dimen.button_bar_height) - getSBHeight() - indicatorHeight));
	}

	static int getSBHeight() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
    	((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		int statusBarHeight;

    	switch (displayMetrics.densityDpi) {
    	    case DisplayMetrics.DENSITY_HIGH:
    	        statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
    	        break;
    	    case DisplayMetrics.DENSITY_MEDIUM:
    	        statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
    	        break;
    	    case DisplayMetrics.DENSITY_LOW:
    	        statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
    	        break;
    	    case DisplayMetrics.DENSITY_TV:
    	        statusBarHeight = TV_DPI_STATUS_BAR_HEIGHT;
    	    	break;
    	    case DisplayMetrics.DENSITY_XHIGH:
    	        statusBarHeight = XHIGH_DPI_STATUS_BAR_HEIGHT;
    	    	break;
    	    default:
    	        statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
    	}
    	return statusBarHeight;
	}

	@Override
    protected void onDetachedFromWindow() {
    	dock.removeAllViews();
    	panelHolder.removeAllViews();
    	attatched = false;        
    	super.onDetachedFromWindow();
    }  
    
	private void setupLivePanels(Context context) {                
    	panels = new ArrayList<String>();
    	if (!panels.isEmpty()) {
    		panels.clear();
    	}
		panelHolder.removeAllViews();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 25, 0, 0);
		int count = getPanelCount(context);
		panel = new Object[count];

		for (int i=0;i<count;i++) {
			if (panels.get(i).equals("weather")) {
				panel[i] = new WeatherLivePanel(context);
				((WeatherLivePanel) panel[i]).setId(WEATHER_PANEL_ID);
				panelHolder.addView(((WeatherLivePanel) panel[i]), lp);
				((WeatherLivePanel) panel[i]).setOnClickListener(listener);
			} else if (panels.get(i).equals("music")) {
				panel[i] = new MusicControlsLivePanel(context);
				((MusicControlsLivePanel) panel[i]).setId(MUSIC_PANEL_ID);
				panelHolder.addView(((MusicControlsLivePanel) panel[i]), lp);
				((MusicControlsLivePanel) panel[i]).setOnClickListener(listener);
			} else if (panels.get(i).equals("jeff")) {
		        e.putInt("_lastJeff", tCount);
		        e.commit();
				panel[i] = new TextView(context);
				((TextView) panel[i]).setText("Hey fuck you man!");
				((TextView) panel[i]).setTextSize(25);
				((TextView) panel[i]).setTextColor(getResources().getColor(android.R.color.holo_red_light));
				((TextView) panel[i]).setGravity(Gravity.CENTER);
				panelHolder.addView((TextView) panel[i], lp);
			}
		}

	}
	
	private int getPanelCount(Context c) {
		panels.add("weather");
	    Calendar calendar = Calendar.getInstance();	    
    	AudioManager aM = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
		if (aM.isWiredHeadsetOn() || aM.isMusicActive()) {
			panels.add("music");
		} 
		if (calendar.get(Calendar.HOUR) > 10 && calendar.get(Calendar.HOUR) < 20 && showJeff) {
			if (tCount == 0) {
				panels.add("jeff");
			} else {
				int last = prefs.getInt("_lastJeff", 0);
				if (last+10 == tCount) {
					panels.add("jeff");
				}
			}
		}
		Log.d("LIVEPANEL", "Panel Count Is "+Integer.toString(panels.size()));
		
		return panels.size();
	}

	public void setupHotseats() {
		dock.removeAllViews();		 
		DBAdapter db = new DBAdapter(mContext);
		db.open();
		c = db.getAllHotseats();
   		hotseat = new DockItemView[c.getCount()];
		hotseatIntents = new String[c.getCount()];
		while (c.moveToNext()) {
			hotseatIntents[c.getPosition()] = c.getString(c.getColumnIndex("intent"));
			hotseat[c.getPosition()] = new DockItemView(mContext);
			hotseat[c.getPosition()].setId(c.getPosition());
			hotseat[c.getPosition()].setData(c.getString(c.getColumnIndex("intent")));
			hotseat[c.getPosition()].setParent(dock);
			byte[] icon = c.getBlob(c.getColumnIndex("icon"));
			Bitmap b = BitmapFactory.decodeByteArray(icon, 0, icon.length);
						
			hotseat[c.getPosition()].getIcon().setImageBitmap(b);
			hotseat[c.getPosition()].setPadding(5, 5, 5, 5);
			hotseat[c.getPosition()].getIcon().setScaleType(ImageView.ScaleType.CENTER);
			hotseat[c.getPosition()].getTitle().setText(c.getString(c.getColumnIndex("name")));
			hotseat[c.getPosition()].setPName(hotseatIntents[c.getPosition()]);

			ScrollView.LayoutParams lp = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
			dock.addView(hotseat[c.getPosition()], lp);
			dock.getChildAt(c.getPosition()).setVisibility(View.INVISIBLE);
		}
		c.close();
		db.close();
		if (dock.getChildCount() < 1) {
			dock.setVisibility(View.GONE);
		}
	}
    
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.main_search) {
				searchBox.setText("");
				flipTo(2);
			} else if (v.getId() == R.id.all_apps) {
				mLauncher.showAllApps(true);
			} else if (v.getId() == R.id.main_settings) {
				flipTo(0);
			} else if (v.getId() == R.id.left) {
				searchBox.setText("");
				flipTo(flipper.getDisplayedChild()-1);
			} else if (v.getId() == R.id.right) {
				flipTo(flipper.getDisplayedChild()+1);
			} else if (v.getId() == WEATHER_PANEL_ID) {
				if (!((WeatherLivePanel)panel[0]).mHandler.hasMessages(WeatherLivePanel.QUERY_WEATHER)) {
					((WeatherLivePanel)panel[0]).mHandler.sendEmptyMessage(WeatherLivePanel.QUERY_WEATHER);
	            }
			}
		}
	};

	public static String getIntent(DockItemView v) {
		return hotseatIntents[v.getNewId()];
	}
	
	public static void startApplication(String packageName) {
	    try  {
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");
	        List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(intent, 0);

	        for(ResolveInfo info : resolveInfoList)
	            if(info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
	                launchComponent(info.activityInfo.packageName, info.activityInfo.name);
	                return;
	            }

	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	private static void launchComponent(String packageName, String name) {
	    Intent intent = new Intent("android.intent.action.MAIN");
	    intent.addCategory("android.intent.category.LAUNCHER");
	    intent.setComponent(new ComponentName(packageName, name));
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    mContext.startActivity(intent);
	}
	
	private void inFromRight() {
		flipper.setInAnimation(slideRightIn);
		flipper.setOutAnimation(slideLeftOut);
	}
	
	private void inFromLeft() {
		flipper.setInAnimation(slideLeftIn);
		flipper.setOutAnimation(slideRightOut);			
	}
	
	public void flipTo(int where) {
		int home = 1;
		
		if (where != flipper.getDisplayedChild()) {
			if (where > flipper.getDisplayedChild()) {
				inFromRight();		
				left.setVisibility(View.VISIBLE);
				right.setVisibility(View.GONE);
				
			} else {
				inFromLeft();
				left.setVisibility(View.GONE);
				right.setVisibility(View.VISIBLE);

			}
			
			if (where == home) {
				left.setVisibility(View.GONE);
				right.setVisibility(View.GONE);
			}
			flipper.setDisplayedChild(where);
		}
	}		
	
	protected String parsePhone(String full) {
		String phone = null;
		if (Character.isLetter(full.charAt(0))) {
			phone = full.split("- ")[1];
		} else {
			if (full.startsWith("+")) {
				full.replace("+", "");
			}
			phone = full;
		}
		return phone;
	}
	
	private String[] getSearchableItems(Context context) {
		ArrayList<String> list = new ArrayList<String>();
		Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		final PackageManager pm = context.getPackageManager();
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String num = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			if (name != null && !name.equals("") && num != null && !num.equals("")) {
				list.add(name + " - " + num);
			}
		}
	
		List<ApplicationInfo> applications = context.getPackageManager().getInstalledApplications(0);
		for (int n=0; n < applications.size(); n++) {
			if ((applications.get(n).flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
		        list.add(applications.get(n).loadLabel(pm).toString());
		    }
	    }			
		Collection<String> itemsFinal = new TreeSet<String>(Collator.getInstance());
		for (int i=0;i<list.size();i++) {
			itemsFinal.add(list.get(i));
		}
		return (String[]) itemsFinal.toArray(new String[itemsFinal.size()]);	
	}	
	
	public class AnimateDockTask extends AsyncTask<Object, View, Void> {
		
		   @Override
		   protected Void doInBackground(Object... vg) {
			   for (int i=0;i<vg.length;i++) {				  
					   publishProgress((View) vg[i]);									   
				   try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			   return null;
		   }
		   
		   @Override
		   public void onProgressUpdate(View... v) {
			   v[0].startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_down));
			   v[0].setVisibility(View.VISIBLE);			   
		   }
	}

}

