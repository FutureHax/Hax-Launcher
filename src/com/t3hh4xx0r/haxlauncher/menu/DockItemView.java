package com.t3hh4xx0r.haxlauncher.menu;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;

public class DockItemView extends RelativeLayout {
	
    private final Point mStartPoint = new Point();
    DockItemView item;
	private int id;
	private ViewGroup parent;
	private String data;
	private String pName;
	static Context ctx;
	ImageView icon;
	StyledTextFoo title;
	View root;
    
	public DockItemView(final Context context) {
		super(context);
		item = this;
		ctx = context;
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = layoutInflater.inflate(R.layout.dock_item, this);
        icon = (ImageView) root.findViewById(R.id.icon);
        title = (StyledTextFoo) root.findViewById(R.id.title);
        root.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startApplication(getPName());				
			}
        });
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {    	
			case MotionEvent.ACTION_CANCEL:
          		reset();
			case MotionEvent.ACTION_UP:
           		reset();
              	int diffYU = Math.abs(((int) event.getY()) - mStartPoint.y);
              	int diffXU = Math.abs(((int) event.getX()) - mStartPoint.x);
              	if (!handled) {
              		Log.d("UP!!!!!!!!", Integer.toString(diffYU)+":"+Integer.toString(diffXU));
              		if (diffYU < 10 && diffXU < 10) {              	
              			startApplication(getPName());
              		} else {
              			
              		}
              	} 
          		Log.d("UP!!!!!!!!", Boolean.toString(handled));
             return handled;
         	case MotionEvent.ACTION_MOVE:
              	int diffXM = ((int) event.getX()) - mStartPoint.x;
                scrollTo(-diffXM, 0);
                double alpha = (1-Math.abs(((double)diffXM))/50);
                this.setAlpha((float)alpha);
              	if (Math.abs(diffXM) >= (this.getWidth() * .75)) {
                   	parent.removeView(item);
                   	DBAdapter db = new DBAdapter(ctx);
                   	db.open();
                   	db.removeHotseat(getData());
                   	db.close();
                   	handled = true;
                   	return true;
              	}
            	break;
        	case MotionEvent.ACTION_DOWN:
              	mStartPoint.x = (int) event.getX();
              	mStartPoint.y = (int) event.getY();
              	break;              	
      	}
        return super.onTouchEvent(event);
    }

    public void setData(String d) {
    	this.data = d;
    }
    
    public String getData() {
    	return this.data;
    }
    
    public String getPName() {
    	return this.pName;
    }

    private void reset() {
        scrollTo(0, 0);
        this.setAlpha(1);
    }
    
    public void setId(int id) {
    	this.id = id;    	
    }

    public void setPName(String s) {
    	this.pName = s;    	
    }
    
    public int getNewId() {
    	return this.id;    	
    }
    
    public ImageView getIcon() {
    	return this.icon;
    }
    
    public StyledTextFoo getTitle() {
    	return this.title;
    }
    
    public void setParent(ViewGroup parent) {
    	this.parent = parent;    	
    }
    
	public static void startApplication(String packageName) {
	    try  {
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");
	        List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(intent, 0);

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
	    ctx.startActivity(intent);
	}	
 }
