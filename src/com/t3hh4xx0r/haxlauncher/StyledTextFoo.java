package com.t3hh4xx0r.haxlauncher;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class StyledTextFoo extends TextView{

	public StyledTextFoo(Context context) {
		super(context);
        init(context);
	}
	
    public StyledTextFoo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StyledTextFoo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

	private void init(Context c) {
	     String otfName = "cabnd.otf";
	     Typeface font = Typeface.createFromAsset(c.getAssets(), otfName);
	     this.setTypeface(font);
	}
	
	
}
