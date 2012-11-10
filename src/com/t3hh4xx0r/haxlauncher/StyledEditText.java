package com.t3hh4xx0r.haxlauncher;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class StyledEditText extends EditText {

	public StyledEditText(Context context) {
		super(context);
        init(context);
	}
	
    public StyledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StyledEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

	private void init(Context c) {
		if (this.isInEditMode()) {
			return;
		}
	     String otfName = "cabnd.otf";
	     Typeface font = Typeface.createFromAsset(c.getAssets(), otfName);
	     this.setTypeface(font);
	}

}
