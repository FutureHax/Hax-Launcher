/*
O * Copyright (C) 2011 The Android Open Source Project
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.Launcher;
import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.preferences.PreferencesProvider;

import dalvik.system.PathClassLoader;

public class LauncherMenu extends RelativeLayout {
	SharedPreferences prefs;

    private Launcher mLauncher;
    private View root;
    private ViewFlipper flipper;
    EditText searchBox;
    boolean attatched = false;
    static Context mContext;
    public LinearLayout dock;
    static String[] hotseatIntents;
    public DockItemView hotseat[];
    Object panel[];
    Cursor c;
    ScrollView dHolder;
    
    ListView list;
    static final String ACTION_UPDATE = "com.t3hh4xx0r.haxlauncher.PANEL_UPDATE";
    static final String ACTION_NEW_PACKAGES = "com.t3hh4xx0r.haxlauncher.NEW_PACKAGES";
    static final String ACTION_NEW_CONTACTS = "com.t3hh4xx0r.haxlauncher.NEW_CONTACTS";
    
    public static final int MUSIC_PANEL_ID = 9998;
    
    public static final int WIFI_TOGGLE = 00;
    public static final int AIRPLANE_TOGGLE = 01;
    public static final int ROTATE_TOGGLE = 02;
    public static final int SOUND_TOGGLE = 03;
    
    private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
    private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
    private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;
    private static final int XHIGH_DPI_STATUS_BAR_HEIGHT = 50;
    //TODO fix me
    private static final int TV_DPI_STATUS_BAR_HEIGHT = 50;
    public static final String PREFERENCES_KEY = "plugin_preferences";

    private static Animation slideLeftIn;
    private static Animation slideLeftOut;
    private static Animation slideRightIn;
    private static Animation slideRightOut;
    ViewGroup panelHolder;
    Editor e;
    int tCount = 0;
    RelativeLayout.LayoutParams lp;
    int height;
    ImageView left;
    ImageView right;
    
    String enabledToggles[] = {"WiFi", "Sound", "Rotation", "Airplane"};
    int enabledTogglesId[] = {WIFI_TOGGLE, SOUND_TOGGLE, ROTATE_TOGGLE, AIRPLANE_TOGGLE};
    
    LinearLayout.LayoutParams flipperShort = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 2f);
    LinearLayout.LayoutParams flipperWide = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 3f);
    LinearLayout.LayoutParams holderShort = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
    LinearLayout.LayoutParams holderWide = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0f);
    
    @SuppressWarnings("deprecation")
	public LauncherMenu(final Context context) {
        this(context, null, 0);
        mLauncher = (Launcher) context;
        mContext = context;
        
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
        ImageView system_s = (ImageView) findViewById(R.id.system_settings);
        ImageView launcher_S = (ImageView) findViewById(R.id.launcher_settings);
        system_s.setOnClickListener(listener);
        launcher_S.setOnClickListener(listener);
        searchBox = (EditText) root.findViewById(R.id.search_box);
        list = (ListView) root.findViewById(R.id.list);
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
        flipper = (ViewFlipper) root.findViewById(R.id.flipper);
        android.widget.LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        flipper.setLayoutParams(lp3);
        dock = (LinearLayout) root.findViewById(R.id.dock);
        panelHolder = (ViewGroup) root.findViewById(R.id.panels);
        ImageView apps = (ImageView) findViewById(R.id.all_apps);
        apps.setOnClickListener(listener);
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE);
        filter.addAction(ACTION_NEW_PACKAGES);
        filter.addAction(ACTION_NEW_CONTACTS);
        UpdateReciever uR = new UpdateReciever();
        context.registerReceiver(uR, filter);

        ContactsObserver contactsObserver = new ContactsObserver(new Handler());       
        contactsObserver.register(context);
    }

	public LauncherMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LauncherMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
    	super.onTouchEvent(event);
    	return false;
    }
    

    public boolean isVisible() {
    	return attatched;
    }
    
    public int getMenuPage() {
    	return flipper.getDisplayedChild();
    }
    
    @Override
    protected void onAttachedToWindow() {
    	flipper.setDisplayedChild(1);
    	prefs = PreferenceManager.getDefaultSharedPreferences(mContext);    
        final ImageView shade = (ImageView) mLauncher.findViewById(R.id.shade);
        shade.setBackgroundColor(PreferencesProvider.Interface.Menu.getMenuColor(mContext));
    	e = prefs.edit();
    	attatched = true;
        tCount = prefs.getInt("_interalCount", 0)+1;
        e.putInt("_interalCount", tCount);
        e.commit();   
    	setupHotseats();
        setupLivePanels(mContext);
        Animation slideLeftIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
        final Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_fast);
        shade.setLayoutParams(getCustomLayoutParams());
        root.startAnimation(slideLeftIn);	        
		shade.startAnimation(fadeIn);
		shade.setVisibility(View.VISIBLE);
				
		left.setVisibility(View.GONE);
		right.setVisibility(View.GONE);
		mLauncher.mHotseat.setMenuButton(attatched);
		
		((LinearLayout) findViewById(R.id.lame_wrapper)).setLayoutParams(holderShort);
		((LinearLayout) findViewById(R.id.cool_wrapper)).setLayoutParams(flipperShort);
		
        final ArrayList<String> searchables = getSearchableItems(mContext);
        final ArrayList<String> displayItems = new ArrayList<String>();
        final SearchAdapter a = new SearchAdapter(mContext, displayItems, (Activity) mContext, searchBox);
        list.setAdapter(a);
        searchBox.addTextChangedListener(new TextWatcher() {
        	public void afterTextChanged(Editable s) {
				a.notifyDataSetChanged();
        	}

        	public void beforeTextChanged(CharSequence s, int start, int count,
        			int after) {
        		displayItems.clear();
        	}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
        		displayItems.clear();
        		if (s.length() > 0) {
        			displayItems.add("Search the web for \""+s.toString()+"\"");
        		}
        		
				int length = searchBox.getText().length();	
				//TODO:paginate through, load 10 items and a show more item that loads 10 more
				for (int i=0;i<searchables.size();i++) {
					if (searchables.get(i).toLowerCase().contains(searchBox.getText().toString().toLowerCase()) && length != 0) {
						if (displayItems.size() <= 10) {
							displayItems.add(searchables.get(i));
						} else {
							//displayItems.add("View next 10");
							break;
						}
					} 
				}
			}					
        });
        
		super.onAttachedToWindow();
    }
    
    private FrameLayout.LayoutParams getCustomLayoutParams() {    	    	
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
		mLauncher.mHotseat.setMenuButton(attatched);
		super.onDetachedFromWindow();
    }  
    
	private void setupLivePanels(Context context) {                
		panelHolder.removeAllViews();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 25, 0, 0);		
		getExternalPlugins();
	}

	public void setupHotseats() {
		dock.removeAllViews();		 
		DBAdapter db = new DBAdapter(mContext);
		db.open();
		c = db.getAllHotseats();
		Object[] dArray;	
		dArray = new Object[c.getCount()+1];
		dArray[0] = 0;
	   	hotseat = new DockItemView[c.getCount()];
		hotseatIntents = new String[c.getCount()];
		ScrollView.LayoutParams lp = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
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
		
			dock.addView(hotseat[c.getPosition()], lp);
			dArray[c.getPosition()] = hotseat[c.getPosition()];
	
			dock.getChildAt(c.getPosition()).setVisibility(View.INVISIBLE);
		}
		
		DockItemView addCard = new DockItemView(mContext);
		addCard.getTitle().setText("ADDD");
		addCard.setPName("420");
		dock.addView(addCard, lp);
		dock.getChildAt(dock.getChildCount()-1).setVisibility(View.INVISIBLE);
		dArray[dock.getChildCount()-1] = addCard;
						
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  
	       		//TODO fix me
				(int) + (height - getResources().getDimension(R.dimen.button_bar_height) - getSBHeight() - 10));
	    dHolder.setLayoutParams(lp2);
	       
		AnimateDockTask d = new AnimateDockTask();
		d.execute(dArray);		
		
		c.close();
		db.close();
	}
    
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Context ctx = v.getContext();
			switch (v.getId()) {
			case R.id.main_search:
				searchBox.setText("");
				((LinearLayout) findViewById(R.id.lame_wrapper)).setLayoutParams(holderWide);
				((LinearLayout) findViewById(R.id.cool_wrapper)).setLayoutParams(flipperWide);
				flipTo(2);
				break;
			case R.id.all_apps:
				mLauncher.showAllApps(true);
				break;
			case R.id.main_settings:
				((LinearLayout) findViewById(R.id.lame_wrapper)).setLayoutParams(holderWide);
				((LinearLayout) findViewById(R.id.cool_wrapper)).setLayoutParams(flipperWide);
				flipTo(0);				
				break;
			case R.id.left:
				((LinearLayout) findViewById(R.id.lame_wrapper)).setLayoutParams(holderShort);
				((LinearLayout) findViewById(R.id.cool_wrapper)).setLayoutParams(flipperShort);
				searchBox.setText("");
				flipTo(flipper.getDisplayedChild()-1);
				break;
			case R.id.right:
				((LinearLayout) findViewById(R.id.lame_wrapper)).setLayoutParams(holderShort);
				((LinearLayout) findViewById(R.id.cool_wrapper)).setLayoutParams(flipperShort);
				flipTo(flipper.getDisplayedChild()+1);
				break;					
			case R.id.system_settings:
				Intent i = new Intent();
			    i.setAction(Intent.ACTION_VIEW);
			    i.setClassName("com.android.settings", "com.android.settings.Settings");
			    ctx.startActivity(i);
			    break;			
			case R.id.launcher_settings:
				Intent i2 = new Intent();
			    i2.setAction(Intent.ACTION_VIEW);
			    i2.setClassName("com.t3hh4xx0r.haxlauncher", "com.t3hh4xx0r.haxlauncher.preferences.Preferences");
			    ctx.startActivity(i2);
			    break;
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
	
	private static ArrayList<String> getSearchableItems(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllSearchables();
		if (c.getCount() == 0) {
			setSearchableItems(ctx, new boolean[]{true, true});
			return getSearchableItems(ctx);
		} else {
			ArrayList<String> res = new ArrayList<String>();
			while (c.moveToNext()) {
				res.add(c.getString(c.getColumnIndex("text")));	
			}
			c.close();
			db.close();
			return res;
		}
	}
	
	public static void setSearchableItems(Context context, boolean[] items) {
		DBAdapter db = new DBAdapter(context);
		db.open();
		if (items[0]) {
			Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			while (c.moveToNext()) {
				String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String num = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if (name != null && !name.equals("") && num != null && !num.equals("")) {
					db.insertSearchble(name + " - " + num, null);
				}
			}
		}
		if (items[1]) {
			final PackageManager pm = context.getPackageManager();	
			List<ApplicationInfo> applications = context.getPackageManager().getInstalledApplications(0);
			for (int n=0; n < applications.size(); n++) {
				if ((applications.get(n).flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
					db.insertSearchble(applications.get(n).loadLabel(pm).toString(), applications.get(n).packageName);
			    }
		    }	
		}
		db.close();
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

	public void getExternalPlugins() {
		DBAdapter db = new DBAdapter(mContext);
		db.open();
		Cursor c = db.getAllPanels();
		while (c.moveToNext()) {
			AddExternalsTask t = new AddExternalsTask();
			String params[] = {c.getString(c.getColumnIndex("package")), c.getString(c.getColumnIndex("status"))};
			t.execute(params);
		}
		c.close();
		db.close();
        
	}
	
	@SuppressLint("WorldWriteableFiles")
	public class AddExternalsTask extends AsyncTask<String, Object, Void> {        
		   @Override
		   protected Void doInBackground(String... o) {
			   while (isVisible()) {
					String packageName = o[0];
					boolean status = Boolean.parseBoolean(o[1]);
					Log.d("STATUS", Boolean.toString(status));
					Log.d("NAME", o[0]);
			        Context forgeinContext;
					try {	
						forgeinContext = mContext.getApplicationContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
						SharedPreferences forgeinPrefs = forgeinContext.getSharedPreferences(packageName+"_plugin_preferences", Context.MODE_WORLD_WRITEABLE);    
						boolean shouldShowPanel = forgeinPrefs.getBoolean("shouldShow", true);
						Log.d("shouldShowPanel", Boolean.toString(shouldShowPanel));
	
				        String className = packageName+".PanelView";			        
				        String apkName = mLauncher.getPackageManager().getApplicationInfo(
				                packageName, 0).sourceDir;
				        PathClassLoader myClassLoader = new dalvik.system.PathClassLoader(
				                apkName, ClassLoader.getSystemClassLoader());
				        Class<?> handler = Class.forName(className, true, myClassLoader);
				        Constructor<?>[] m = handler.getConstructors();
				        for (int i = 0; i < m.length; i++) {
				            if (m[i].getName().contains("PanelView")) {			            								
				            	if (status &&
				            			shouldShowPanel) {	
				            		LivePanel lp = new LivePanel(mContext);
				            		lp.setTag(o[0]);
					            	try {
					            		View panel = (View)m[i].newInstance(new Object[] {forgeinContext});				            	
					            		lp.addView(panel); 
					            		publishProgress(lp);
					            	} catch (Exception e) {
					            		e.printStackTrace();
					            	}
				            	}			            	
				            } 
				        }	
					} catch (NameNotFoundException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} 
			    					
				   return null;
			   }
			return null;
		   }
		   
		   @Override
		   public void onProgressUpdate(Object... v) {
			   View p = (View) v[0];	   
			   int index = 0;
			   p.setVisibility(View.GONE);
			   if (panelHolder.getChildCount() != 0) {
				   for (int i=0;i!=panelHolder.getChildCount();i++) {
					   if (panelHolder.getChildAt(i).getTag().equals(p.getTag())) {						   
						   panelHolder.removeViewAt(i);
						   index = i;
						   break;
					   }
				   }
		       	   panelHolder.addView(p, index);

			   } else {
		       	   panelHolder.addView(p);
			   }
			   p.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left_fast));
			   p.setVisibility(View.VISIBLE);
		   }
	}

	public class UpdateReciever extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String a = intent.getAction();
			String name = intent.getExtras().getString("name");
			String status = null;
			
			if (a.equals(ACTION_UPDATE)) {
				DBAdapter db = new DBAdapter(context);
				db.open();
				Cursor cur = db.getAllPanels();
				while (cur.moveToNext()) {
					if (cur.getString(cur.getColumnIndex("package")).equals(name)) {
						status = cur.getString(cur.getColumnIndex("status"));
						break;
					}
				}
				cur.close();
				db.close();
				
				if (status != null) {
					AddExternalsTask t = new AddExternalsTask();
					String params[] = {name, status};
					t.execute(params);
				}
			} else if (a.equals(ACTION_NEW_PACKAGES)) {
				updateSearchablePackages(context, intent.getStringExtra("text"), intent.getIntExtra("whatToDo", 2));
			} else if (a.equals(ACTION_NEW_CONTACTS)) {
				updateSearchableContacts(context, intent.getStringExtra("text"), intent.getIntExtra("whatToDo", 2));
			}
		}

		private void updateSearchablePackages(Context c, String display, int whatToDo) {
			final int UPDATE = 0;
			final int REMOVE = 1;
			final int ADD = 2;
			String appName = null;
			final PackageManager pm = c.getPackageManager();
			try {
				appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(display, PackageManager.GET_UNINSTALLED_PACKAGES));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DBAdapter db = new DBAdapter(c);
			db.open();
			
			if (whatToDo == ADD) {
				long success = db.insertSearchble(appName, display);
				Log.d("ADDDING SEARCHABLES =========================================", "ADDED "+ display+(success != -1 ? " SUCCESSFULLY : " + success  :  " UNSUCCESSFULLY : "+success));
			} else if (whatToDo == UPDATE) {
				long success = db.updateSearchablePackage(display, appName);
				Log.d("UPDATING SEARCHABLES =========================================", "UPDATED "+ display+(success != 0 ? " SUCCESSFULLY  : " + success  :  " UNSUCCESSFULLY : "+success));
			} else if (whatToDo == REMOVE) {
				long success = db.removeSearchablePackage(display);
				Log.d("REMOVING SEARCHABLES =========================================", "REMOVED "+ display+(success != -1 ? " SUCCESSFULLY : " + success  :  " UNSUCCESSFULLY : "+success));
			}
			db.close();
	}
		
	private void updateSearchableContacts(Context c, String display, int whatToDo) {
		final int UPDATE = 0;
		final int REMOVE = 1;
		final int ADD = 2;
		
		DBAdapter db = new DBAdapter(c);
		db.open();
		
		if (whatToDo == ADD) {
			long success = db.insertSearchble(display, null);
			Log.d("ADDDING SEARCHABLES =========================================", "ADDED "+ display+(success != -1 ? " SUCCESSFULLY : " + success  :  " UNSUCCESSFULLY : "+success));
		} else if (whatToDo == UPDATE) {
			long success = db.updateSearchablePackage(null, display);
			Log.d("UPDATING SEARCHABLES =========================================", "UPDATED "+ display+(success != 0 ? " SUCCESSFULLY  : " + success  :  " UNSUCCESSFULLY : "+success));
		} else if (whatToDo == REMOVE) {
			long success = db.removeSearchableContact(display);
			Log.d("REMOVING SEARCHABLES =========================================", "REMOVED "+ display+(success != -1 ? " SUCCESSFULLY : " + success  :  " UNSUCCESSFULLY : "+success));
		}
		db.close();
	}
}

	public static class ContactsObserver extends ContentObserver {
		static Context ctx;
		String name, number;
		Cursor common;

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			common = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			long start, end;
			start = System.currentTimeMillis();
			ArrayList<String> items = getSearchableItems(ctx);
			while(common.moveToNext()) {				
				name = common.getString(common.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
				number = common.getString(common.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Pattern p = Pattern.compile("[a-zA-Z]");  
				Matcher m = p.matcher(number);  
				boolean hasNumber = number != null && !number.equals("") && !m.find();
				if (name != null && 
						!name.equals("") &&
						hasNumber &&
						!items.contains(name + " - " + number)) {
					Intent i = new Intent();
					i.setAction(ACTION_NEW_CONTACTS);
					Bundle b = new Bundle();
					b.putBooleanArray("items", new boolean[]{true, false});
					b.putString("text", name + " - " + number);
					i.putExtras(b);
					ctx.sendBroadcast(i);
				} else {
					continue;
				}
			}
			end = System.currentTimeMillis();
			//Log.d("TIME", (end-start)+"");
		}

		public ContactsObserver(Handler handler) {
			super(handler);
		}
		
		public void register(Context c) {
			ContactsObserver.ctx = c;			
			ContactsObserver contentObserver = new ContactsObserver(new Handler());
			c.getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, contentObserver);
		}
	} 
}

