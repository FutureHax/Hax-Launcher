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

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.LauncherMenu;

public class Hotseat extends FrameLayout {
    private static final String TAG = "Hotseat";
    private static final int sMenuRank = 0; // In the left of the dock

    private Launcher mLauncher;
    private CellLayout mContent;

    int mCellCountX;
    private int mCellCountY;
    private boolean mIsLandscape;
    Context ctx;

    BubbleTextView mMenu;
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
        mContent.removeAllViewsInLayout();
        setupRecents();

        // Add the Apps button
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
         mMenu = (BubbleTextView)
                inflater.inflate(R.layout.application, mContent, false);
         mMenu.setCompoundDrawablesWithIntrinsicBounds(null,
                context.getResources().getDrawable(R.drawable.startmenu_open), null, null);
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
    
    private void setupRecents() {
    	ActivityManager actvityManager = (ActivityManager)
    	ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager pm = ctx.getPackageManager();
    	List<RecentTaskInfo> apps = actvityManager.getRecentTasks(10, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

    	int added = 0;
    	int skipped = 0;
    	for (int i=0;i<apps.size();i++) {
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
                ImageView image = new ImageView(ctx);
                image.setImageDrawable(icon);
                int x = getCellXFromOrder(added+skipped);
                int y = getCellYFromOrder(skipped+added);
                if (!activityInfo.packageName.equals("com.t3hh4xx0r.haxlauncher")) {       
                	added++;
                	mContent.addViewToCellLayout(image, -1, 0, new CellLayout.LayoutParams(x,y,1,1), true);                
                	image.setOnClickListener(new OnClickListener() {
                		@Override
                		public void onClick(View v) {
                			LauncherMenu.startApplication(activityInfo.packageName);
                		}
                	});
                } else {
                	skipped++;
                }
            }
    	}
    }
}
