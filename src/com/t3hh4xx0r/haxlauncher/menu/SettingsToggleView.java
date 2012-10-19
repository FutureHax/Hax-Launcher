package com.t3hh4xx0r.haxlauncher.menu;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;

public class SettingsToggleView extends RelativeLayout implements OnClickListener {
	Context ctx;
	View root;
	ImageView icon;
	StyledTextFoo text;
	
    String enabledToggles[] = {"WiFi", "Sound", "Rotation", "Airplane"};
    int enabledTogglesId[] = {WIFI_TOGGLE, SOUND_TOGGLE, ROTATE_TOGGLE, AIRPLANE_TOGGLE};
    public static final int WIFI_TOGGLE = 00;
    public static final int AIRPLANE_TOGGLE = 01;
    public static final int ROTATE_TOGGLE = 02;
    public static final int SOUND_TOGGLE = 03;
    
	public SettingsToggleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	public SettingsToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SettingsToggleView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context c, AttributeSet a) {
		ctx = c;
		String t = null;
		if (this.isInEditMode()) {
			return;
		}
		if (a != null) {
			TypedArray tA = getContext().obtainStyledAttributes(a, R.styleable.SettingToggleView);
			t = tA.getString(R.styleable.SettingToggleView_title_text);
			tA.recycle();			
		}		
			
        LayoutInflater layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	root = layoutInflater.inflate(R.layout.setting_toggle_item, this);
    	root.setOnClickListener(this);
    	root.setHapticFeedbackEnabled(true);
    	icon = (ImageView) root.findViewById(R.id.icon);
    	text = (StyledTextFoo) root.findViewById(R.id.text);
    	text.setTextAppearance(ctx, android.R.style.TextAppearance_Medium);
    	int id = 420;
    	for (int i=0;i<enabledToggles.length;i++) {
    		if (enabledToggles[i].equals(t)) {
    			id = enabledTogglesId[i];
    		}
    	}
		setImageForSettingState((ImageView)root.findViewById(R.id.icon), id);
    	text.setText(t);
	}

	@Override
	public void onClick(View v) {
		performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
		Context ctx = v.getContext();
		switch (v.getId()) {
		case R.id.wifi:
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            boolean wifiEnabled = wifiManager.isWifiEnabled();
            wifiManager.setWifiEnabled(!wifiEnabled);
            setImageForSettingState((ImageView)v.findViewById(R.id.icon), WIFI_TOGGLE);
			break;
		case R.id.sound:
			AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            int mode = am.getRingerMode();
            if (mode == AudioManager.RINGER_MODE_SILENT) {
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else if (mode == AudioManager.RINGER_MODE_VIBRATE) {
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
            setImageForSettingState((ImageView)v.findViewById(R.id.icon), SOUND_TOGGLE);
			break;
		case R.id.rotation:
			boolean autorotate = (Settings.System.getInt(ctx.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 0) != 0);
			Settings.System.putInt(ctx.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, autorotate ?  0 : 1);
            setImageForSettingState((ImageView)v.findViewById(R.id.icon), ROTATE_TOGGLE);
			break;
		case R.id.airplane:
			boolean airplaneEnabled = (Settings.System.getInt(ctx.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1);
            Settings.System.putInt(ctx.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, airplaneEnabled ? 0 : 1);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", !airplaneEnabled);
            ctx.sendBroadcast(intent);
            setImageForSettingState((ImageView)v.findViewById(R.id.icon), AIRPLANE_TOGGLE);
			break;
		}
	}
	
	   private void setImageForSettingState(ImageView image, int id) {  
	    	Context ctx = image.getContext();
	    	Drawable d = null;

	    	switch (id) {
	    	case 420:
	    		break;
	    	case WIFI_TOGGLE:
	            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
	            boolean wifiEnabled = wifiManager.isWifiEnabled();
	            d = wifiEnabled ? getResources().getDrawable(R.drawable.wifi_off) : getResources().getDrawable(R.drawable.wifi_on);
		        image.setImageDrawable(d);
		    break;
	    	case SOUND_TOGGLE:
	    		AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
	    		int mode = am.getRingerMode();
	    		if (mode == AudioManager.RINGER_MODE_SILENT) {
	    			d = getResources().getDrawable(R.drawable.sound_off);
	    		} else if (mode == AudioManager.RINGER_MODE_VIBRATE) {
	    			d = getResources().getDrawable(R.drawable.vibrate_on);
	    		} else {
	    			d = getResources().getDrawable(R.drawable.sound_on);
	    		}
	    		if (d != null) {
	    	        image.setImageDrawable(d);
	    		}
	    	    break;
			case ROTATE_TOGGLE:
	    		boolean autorotate = (Settings.System.getInt(ctx.getContentResolver(),
	    		Settings.System.ACCELEROMETER_ROTATION, 0) != 0);
	    		d = getResources().getDrawable(autorotate ? R.drawable.auto_rotate_on : R.drawable.auto_rotate_off);
		        image.setImageDrawable(d);
			    break;
			case AIRPLANE_TOGGLE:
	    		boolean enabled = Settings.System.getInt(ctx.getContentResolver(),
	    		           Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	    		d = getResources().getDrawable(enabled ? R.drawable.airplane_on : R.drawable.airplane_off);
		        image.setImageDrawable(d);
			    break;
	    	}
		}	

}
