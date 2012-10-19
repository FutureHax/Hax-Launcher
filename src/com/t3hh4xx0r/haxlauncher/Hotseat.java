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

package com.t3hh4xx0r.haxlauncher;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.t3hh4xx0r.haxlauncher.menu.LauncherMenu;
import com.t3hh4xx0r.haxlauncher.preferences.PreferencesProvider;

public class Hotseat extends FrameLayout {
    private static final String TAG = "Hotseat";
    private static final int sMenuRank = 0; // In the left of the dock

    private Launcher mLauncher;
    private CellLayout mContent;

    int mCellCountX;
    private int mCellCountY;
    private boolean mIsLandscape;
    Context ctx;

    private final Point mStartPoint = new Point();

    public BubbleTextView mMenu;
    
    static List<RecentTaskInfo> lastApps = null;
    static ArrayList<String> removedApps = new ArrayList<String>();
    static List<RecentTaskInfo> apps = null;
    static List<RecentTaskInfo> staticApps = null;
    static List<RecentTaskInfo> staticLastApps = null;
    
    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Hotseat, defStyle, 0);
        if (LauncherApplication.isScreenLarge()) {
            mCellCountX = 9;
        } else {
            mCellCountX = 6;        	
        }
        mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
        mIsLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
        setMenuButton(mLauncher.phoneMenu.isVisible());
        setupRecents();
    }

    CellLayout getLayout() {
        return mContent;
    }
    
    BubbleTextView getMenu() {
    	return mMenu;
    }
    
    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return mIsLandscape ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return mIsLandscape ? 0 : rank;
    } 
    int getCellYFromOrder(int rank) {
        return mIsLandscape ? (mContent.getCountY() - (rank + 1)) : 0;
    }
    public static boolean isMenuButtonRank(int rank) {
    	return rank == sMenuRank;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCellCountY < 0) mCellCountY = LauncherModel.getCellCountY();
        mContent = (CellLayout) findViewById(R.id.layout);
        mContent.setGridSize(mCellCountX, mCellCountY);
        resetLayout();
    }

    void resetLayout() {
        setupRecents();
        // Add the Apps button
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        try {
	        ViewGroup vg = (ViewGroup) mMenu.getParent();
	        vg.removeView(mMenu);
        } catch (Exception e) {};
        mMenu = (BubbleTextView)
               inflater.inflate(R.layout.application, mContent, false);
         setMenuButton(mLauncher == null ? false : mLauncher.phoneMenu.isVisible());
         mMenu.setContentDescription(context.getString(R.string.all_apps_button_label));
         mMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                	mLauncher.onTouchDownMenuButton(v);
                }
                return false;
            }
        });

        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                	mLauncher.handleMenuClick(true);
                }
            }
        });
        
        mMenu.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
                if (mLauncher != null) {
                	mLauncher.showAllApps(true);
                }
				return true;
			}
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
        int x = getCellXFromOrder(sMenuRank);
        int y = getCellYFromOrder(sMenuRank);
        mContent.addViewToCellLayout(mMenu, -1, 0, new CellLayout.LayoutParams(x,y,1,1),
                true);
    }
    
    public void setMenuButton(boolean open) {
    	Drawable d = open ? this.getResources().getDrawable(R.drawable.startmenu_close) : 
			this.getResources().getDrawable(R.drawable.startmenu_open);
		mMenu.setCompoundDrawablesWithIntrinsicBounds(null,d, null, null);	
	}

	private void setupRecents() {
        final PackageManager pm = ctx.getPackageManager();
    	if (didMyRecentsUpdate() || didRealRecentsChange()) {
			mContent.removeAllViews();
	    	int added = 0;
	    	int skipped = 0;
	    	for (int i=0;i<apps.size();i++) {
	    		final int position = i;
	    		final ActivityManager.RecentTaskInfo info = apps.get(i);
	            Intent intent = new Intent(info.baseIntent);
	            if (info.origActivity != null) {
	                intent.setComponent(info.origActivity);
	            }
	
	            intent.setFlags((intent.getFlags()&~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
	                    | Intent.FLAG_ACTIVITY_NEW_TASK);
	            final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
	            if (resolveInfo != null) {
	                final ActivityInfo activityInfo = resolveInfo.activityInfo;
	                final Drawable icon = activityInfo.loadIcon(pm);
	                final ImageView image = new ImageView(ctx);
	                image.setImageDrawable(icon);
	                image.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							boolean handled = false;
							switch (event.getAction()) {    	
								case MotionEvent.ACTION_CANCEL:
					          		reset(image);
								case MotionEvent.ACTION_UP:
					              	int diffYU = Math.abs(((int) event.getY()) - mStartPoint.y);
					              	int diffXU = Math.abs(((int) event.getX()) - mStartPoint.x);
					              	if (!handled) {
					              		if (diffYU < 10 && diffXU < 10) {              	
					              			LauncherMenu.startApplication(activityInfo.packageName);
					              		} else {
							           		reset(image);
					              		}
					              	} 
					              	return handled;
					         	case MotionEvent.ACTION_MOVE:
					            	ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
					         		v.getParent().requestDisallowInterceptTouchEvent(true);
					              	double diffYM = ((int) event.getY()) - mStartPoint.y;
					              	double height = v.getHeight();
					                v.scrollTo(0, (int) -diffYM);
					                double alpha = 1-(Math.abs(diffYM)/ height);
					                v.setAlpha((float)alpha);
					              	if (Math.abs(diffYM) >= height) {
					              		handled = true;
					              		mContent.removeView(v);	
					                	removedApps.add(getPackageFromTaskList(info)[0]);
					                	apps.remove(position);
					                	resetLayout();
					                	for (RunningAppProcessInfo i : am.getRunningAppProcesses()){
					                		if (i.processName.equals(getPackageFromTaskList(info)[2])) {
					                			if (PreferencesProvider.General.Advanced.getDeviceHasRoot(ctx)) {
						                			if (PreferencesProvider.General.Advanced.getEnableRootProcessKiller(ctx)) {
						                				String cmds[] = {"echo " + i.pid, "kill -9 " + i.pid};
						                				try {
															runAsRoot(cmds);
														} catch (IOException e) {}						                				
						                			} else {
						                				Log.d("KILLING PROCESS "+i.processName, Integer.toString(i.pid));
							                			am.killBackgroundProcesses(getPackageFromTaskList(info)[1]);
							                			int pid;
							                			try {
							                				pid = isServiceRunning(ctx, getPackageFromTaskList(info)[1]);
							                				if (pid !=420) {
								                				Log.d("KILLING SERVICE", serviceClassName(ctx, getPackageFromTaskList(info)[1]));
									                			Intent service = new Intent(serviceClassName(ctx, getPackageFromTaskList(info)[1]));
									                			ctx.stopService(service);
							                				}
							                			} catch (NullPointerException e) {}
						                			}
					                			} else {
						                			Log.d("KILLING PROCESS "+i.processName, Integer.toString(i.pid));
						                			am.killBackgroundProcesses(getPackageFromTaskList(info)[1]);
						                			int pid;
						                			try {
						                				pid = isServiceRunning(ctx, getPackageFromTaskList(info)[1]);
						                				if (pid !=420) {
							                				Log.d("KILLING SERVICE", serviceClassName(ctx, getPackageFromTaskList(info)[1]));
								                			Intent service = new Intent(serviceClassName(ctx, getPackageFromTaskList(info)[1]));
								                			ctx.stopService(service);
						                				}
						                			} catch (NullPointerException e) {}
					                			}					    
					                			break;
					                		} 
					                	}
					                   	return true;
					              	}
					            	break;
					        	case MotionEvent.ACTION_DOWN:
					              	mStartPoint.x = (int) event.getX();
					              	mStartPoint.y = (int) event.getY();
					              	break;              	
					      	}
					        return true;					}
	                });
	                int x = getCellXFromOrder(skipped+added);
	                int y = getCellYFromOrder(skipped+added);
	                if (!activityInfo.packageName.equals("com.t3hh4xx0r.haxlauncher")) {  
	                	if (!removedApps.contains(getPackageFromTaskList(info)[0])) {
	                		added++;
	                		mContent.addViewToCellLayout(image, -1, 0, new CellLayout.LayoutParams(x,y,1,1), true);                	                	
	                	}
	                } else {
	                	skipped++;
	                }
	            }
	    	}
    	}
    }
	
    private boolean didMyRecentsUpdate() {
    	ActivityManager actvityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        boolean updated = false;
        if (didRealRecentsChange()) {
        	updated = true;
        	removedApps.clear();
        }
    	if (lastApps == null || apps == null) {    	
    		updated = true;
    	}
    	lastApps = apps;
    	apps = actvityManager.getRecentTasks(10, ActivityManager.RECENT_IGNORE_UNAVAILABLE);    
    	
        if (lastApps != null && apps != null) { 
        	if (lastApps.size() != apps.size()) {
        		updated = true;
        	} else {
		        for (int i=0;i<lastApps.size();i++) {    		
		        	try {
		        		if (!getPackageFromTaskList(apps.get(i))[0].equals(getPackageFromTaskList(lastApps.get(i))[0])) {
		        			if (removedApps.contains(getPackageFromTaskList(apps.get(i))[0])) {
		        				updated = true;
		        				break;
		        			} 
		        		}
		        	} catch (NullPointerException npe) {
		        		npe.printStackTrace();
		        		updated = true;
		        		break;
		        	}
		       	}   
        	}
        }
    	return updated;
	}
    
    private boolean didRealRecentsChange() {
    	ActivityManager actvityManager = (ActivityManager)
    	ctx.getSystemService(Context.ACTIVITY_SERVICE);
        boolean updated = false;
		if (staticLastApps == null || staticApps == null) {
			updated = true;
		}
		staticLastApps = staticApps;
		staticApps = actvityManager.getRecentTasks(10, ActivityManager.RECENT_IGNORE_UNAVAILABLE);    

        if (staticLastApps != null && staticApps != null) { 
	        for (int i=0;i<staticLastApps.size();i++) {		
	        	try {
	        		if (!getPackageFromTaskList(staticApps.get(i))[0].equals(getPackageFromTaskList(staticLastApps.get(i))[0])) {
	        			updated = true;
	        			break;
	        		}
	        	} catch (NullPointerException npe) {
	        		updated = true;
	        		npe.printStackTrace();
	        		break;
	        	}
	        }       
        }
        return updated;    	
    }

	private String[] getPackageFromTaskList(RecentTaskInfo info) {
        final PackageManager pm = ctx.getPackageManager();
        Intent intent = new Intent(info.baseIntent);
        if (info.origActivity != null) {
            intent.setComponent(info.origActivity);
        }

        intent.setFlags((intent.getFlags()&~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
        if (resolveInfo != null) {
            final ActivityInfo activityInfo = resolveInfo.activityInfo;
            return new String[] {activityInfo.name, activityInfo.packageName, activityInfo.processName};
        }
		return null;
	}

	private void reset(ImageView image) {
		image.scrollTo(0, 0);
        //image.setAlpha(1);
    }
	
	public static int isServiceRunning(Context context, String pName) {
	    ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	    for (int i = 0; i < services.size(); i++) {
	        if (pName.equals(services.get(i).service.getPackageName())){
	        	return services.get(i).pid;
	        }
	    }
	    return 420;
	}
	
	public String serviceClassName(Context c, String pName) {
		String cName = null;
		ActivityManager activityManager = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	    for (int i = 0; i < services.size(); i++) {
	        if (pName.equals(services.get(i).service.getPackageName())){
	        	return services.get(i).service.getClassName();
	        }
	    }
		return cName;
	}
	
	 public static void runAsRoot(String[] cmds) throws IOException{
         java.lang.Process p = Runtime.getRuntime().exec("su");
         DataOutputStream os = new DataOutputStream(p.getOutputStream());            
         for (String tmpCmd : cmds) {
                 os.writeBytes(tmpCmd+"\n");
         }           
         os.writeBytes("exit\n");  
         os.flush();
	 }
}
