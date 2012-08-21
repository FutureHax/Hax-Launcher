package com.t3hh4xx0r.haxlauncher.menu;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ViewFlipper;

public class MenuFlipper extends ViewFlipper {
	DisplayMetrics displayMetrics;

	public MenuFlipper(Context context) {
		super(context);
		
	    Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
	    displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    int width = displayMetrics.widthPixels;
	    setMeasuredDimension((width/3)*1, heightMeasureSpec);
	}
}
