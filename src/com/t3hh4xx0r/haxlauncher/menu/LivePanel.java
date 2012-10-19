package com.t3hh4xx0r.haxlauncher.menu;

import com.t3hh4xx0r.haxlauncher.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class LivePanel extends RelativeLayout {

	DisplayMetrics displayMetrics;
	
	public LivePanel(Context ctx) {
		super(ctx);		
	    
	    Display display = ((Activity) ctx).getWindowManager().getDefaultDisplay();
	    displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);
		this.setBackgroundResource(R.drawable.icon_frame);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    int width = displayMetrics.widthPixels;
//	    setMeasuredDimension(150, 150);
//	    setMeasuredDimension((width/3)*2, 150);
	}
	
}
