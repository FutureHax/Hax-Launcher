package com.t3hh4xx0r.haxlauncher.parse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.preferences.PreferencesProvider;

public class ParseUnsubscribeConfirmActivity extends Activity {
    public static final String GLOBAL = "com.t3hh4xx0r.haxlauncher.general_push_enable"; 
    public static final String TEST = "com.t3hh4xx0r.haxlauncher.general_push_enable_test"; 
    public static final String UPDATES = "com.t3hh4xx0r.haxlauncher.general_push_enable_updates"; 
    
    public static final String GLOBAL_PLAIN = "All push notifications."; 
    public static final String TEST_PLAIN = "Testing push notifications."; 
    public static final String UPDATES_PLAIN = "Update push notifications."; 
    
    public static String[] types = {TEST, UPDATES, GLOBAL};
    public static String[] typesPlain = {TEST_PLAIN, UPDATES_PLAIN, GLOBAL_PLAIN};

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unsubscribe);
		
		final int channel = getIntent().getExtras().getInt("channel");
		Button yes = (Button) findViewById(R.id.yes);
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences p = v.getContext().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
				p.edit().putBoolean(types[channel], false).apply();
				ParseHelper.registerForPush(v.getContext());
				finish();				
			}
		});
		
		Button no = (Button) findViewById(R.id.no);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		TextView channelText = (TextView) findViewById(R.id.channel);
		channelText.setText(typesPlain[channel]);
	}

}
