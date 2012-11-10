package com.t3hh4xx0r.haxlauncher.menu;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;
import android.widget.Toast;

import com.t3hh4xx0r.haxlauncher.R;

public class MenuSettingsAdapter extends BaseAdapter implements OnClickListener{
	
	int[] buttons = {R.layout.wifi_button, R.layout.sound_button,
			R.layout.rotation_button, R.layout.airplane_button, };
    
	String[] button_tags = {"wifi",  "sound", "rotatation", "airplane"};
    /* Buttons */
	private ImageButton mWifiButton;
    private ImageButton mDataButton;
    private ImageButton mSoundButton;
    private ImageButton mRotationButton;
    private ImageButton mAirplaneButton;
    private ImageButton mLteButton;
    private ImageButton mTetherButton;
    private ImageButton mBluetoothButton;
    private ImageButton mGpsButton;
    private ImageButton mBrightnessButton;
    private ImageButton mTorchButton;
    private ImageButton mSyncButton;

    /* Button Descriptions */
    private StyledTextFoo mWifiDesc;
    private StyledTextFoo mDataDesc;
    private StyledTextFoo mSoundDesc;
    private StyledTextFoo mRotationDesc;
    private StyledTextFoo mAirplaneDesc;
    private StyledTextFoo mLteDesc;
    private StyledTextFoo mTetherDesc;
    private StyledTextFoo mBluetoothDesc;
    private StyledTextFoo mGpsDesc;
    private StyledTextFoo mBrightnessDesc;
    private StyledTextFoo mTorchDesc;
    private StyledTextFoo mSyncDesc;
    int mWifiApEnabled;
    int mRingerMode;
    int mNetworkMode;
    int mBrightnessLevel;
    boolean mRotationEnabled;
    boolean mAirplaneEnabled;
    boolean mAutoBrightEnabled;
    boolean syncValue;
    
    private Context ctx;
    public MenuSettingsAdapter(Context c) {
        ctx = c;
    }

    public int getCount() {
        return buttons.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	View view;
        if(convertView == null) {
        	LayoutInflater layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		view = layoutInflater.inflate(buttons[position], parent, false);  
    		view.setOnClickListener(this);
    		view.setTag(button_tags[position]);
        } else {
        	view = convertView;
        }
       
        return view;
    }

	@Override
	public void onClick(View v) {
		String tag = (String) v.getTag();
		Context ctx = v.getContext();
        if (tag.equals("wifi")) {
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            boolean wifiEnabled = wifiManager.isWifiEnabled();
            Toast.makeText(ctx, !wifiEnabled ? "Enabling WiFi" : "Disabling WiFi", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(!wifiEnabled);
                     
        } else if (tag.equals("data")) {
//            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService
//                    (Context.CONNECTIVITY_SERVICE);
//
//            boolean dataEnabled = cm.getMobileDataEnabled();
//            cm.setMobileDataEnabled(!dataEnabled);
//            if (!dataEnabled) {
//                mDataButton.setImageResource(R.drawable.data_on);
//                Toast.makeText(ctx, "Enabling mobile data", Toast.LENGTH_SHORT).show();
//            } else {
//                mDataButton.setImageResource(R.drawable.data_off);
//                Toast.makeText(ctx, "Disabling mobile data", Toast.LENGTH_SHORT).show();
//            }
        } else if (tag.equals("sound")) {
            AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            int mode = am.getRingerMode();
            if (mode == AudioManager.RINGER_MODE_SILENT) {
                Toast.makeText(ctx, "Changing Ringer Mode: Vibrate", Toast.LENGTH_SHORT).show();
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else if (mode == AudioManager.RINGER_MODE_VIBRATE) {
                Toast.makeText(ctx, "Changing Ringer Mode: Normal", Toast.LENGTH_SHORT).show();
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                Toast.makeText(ctx, "Changing Ringer Mode: Silent", Toast.LENGTH_SHORT).show();
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
                   
        } else if (tag.equals("rotation")) {
            boolean autorotate = (Settings.System.getInt(ctx.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) != 0);
            Toast.makeText(ctx, !autorotate ? "Enabling auto-rotation" : "Disabling auto-rotation", Toast.LENGTH_SHORT).show();
            Settings.System.putInt(ctx.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, autorotate ?  0 : 1);
           
        } else if (tag.equals("airplane")) {
            boolean airplaneEnabled = (Settings.System.getInt(ctx.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1);
            Toast.makeText(ctx, !airplaneEnabled ? "Enabling airplane mode" : "Disabling airplane mode", Toast.LENGTH_SHORT).show();
            Settings.System.putInt(ctx.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, airplaneEnabled ? 0 : 1);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", !airplaneEnabled);
            ctx.sendBroadcast(intent);

        } else if (tag.equals("bluetooth")) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                Toast.makeText(ctx, "Enabling Bluetooth", Toast.LENGTH_SHORT).show();
                mBluetoothButton.setImageResource(R.drawable.bt_on);
            } else {
                mBluetoothAdapter.disable();
                Toast.makeText(ctx, "Disabling Bluetooth", Toast.LENGTH_SHORT).show();
                mBluetoothButton.setImageResource(R.drawable.bt_off);
            }
        } else if (tag.equals("gps")) {
            LocationManager mLocationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(ctx, "Enabling GPS", Toast.LENGTH_SHORT).show();
                Settings.Secure.setLocationProviderEnabled(ctx.getContentResolver(), LocationManager.GPS_PROVIDER, true);
            } else {
                Toast.makeText(ctx, "Disabling GPS", Toast.LENGTH_SHORT).show();
                Settings.Secure.setLocationProviderEnabled(ctx.getContentResolver(), LocationManager.GPS_PROVIDER, false);
            }
           
        } else if (tag.equals("brightness")) {
//            try {
//                IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
//                int newBrightnessLevel = 10;
//                int brightnessMode = Settings.System.getInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
//                int brightnessLevel = Settings.System.getInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//                if (brightnessMode == 1) {
//                    Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
//                    Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 10);
//                    Toast.makeText(ctx, "New brightness level: 10", Toast.LENGTH_SHORT).show();
//                    newBrightnessLevel = 10;
//                } else {
//                    if (brightnessLevel == 10) {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 50);
//                        Toast.makeText(ctx, "New brightness level: 50", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 50;
//                    } else if (brightnessLevel == 50) {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 100);
//                        Toast.makeText(ctx, "New brightness level: 100", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 100;
//                    } else if (brightnessLevel == 100) {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 150);
//                        Toast.makeText(ctx, "New brightness level: 150", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 150;
//                    } else if (brightnessLevel == 150) {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 200);
//                        Toast.makeText(ctx, "New brightness level: 200", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 200;
//                    } else if (brightnessLevel == 200) {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
//                        Toast.makeText(ctx, "New brightness level: MAX", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 255;
//                    } else if (brightnessLevel == 255) {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
//                        Toast.makeText(ctx, "New brightness level: AUTO", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 10;
//                    } else {
//                        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 10);
//                        Toast.makeText(ctx, "New brightness level: 10", Toast.LENGTH_SHORT).show();
//                        newBrightnessLevel = 10;
//                    }
//                }
//                try {
//                    power.setBacklightBrightness(newBrightnessLevel);
//                } catch (RemoteException r) {
//                }
//            } catch (SettingNotFoundException e) {
//            }
//           
        } else if (tag.equals("torch")) {
//            Intent intent = new Intent(INTENT_TORCH_ON);
//            startActivity(intent);
//           
        } else if (tag.equals("sync")) {
            boolean syncMode = ContentResolver.getMasterSyncAutomatically();
            if (syncMode) {
                Toast.makeText(ctx, "Disabling Sync", Toast.LENGTH_SHORT).show();
                ContentResolver.setMasterSyncAutomatically(false);
            } else {
                Toast.makeText(ctx, "Enabling Sync", Toast.LENGTH_SHORT).show();
                ContentResolver.setMasterSyncAutomatically(true);
            }
           
        }      
	}

	private void updateButtonState() {
        try {

            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            LocationManager mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

            /* set images based on values */
            int wifiState = wifiManager.getWifiState();
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                case WifiManager.WIFI_STATE_ENABLING:
                    mWifiButton.setImageResource(R.drawable.wifi_on);
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                case WifiManager.WIFI_STATE_DISABLED:
                    mWifiButton.setImageResource(R.drawable.wifi_off);
                    break;
            }

            mRotationEnabled = (Settings.System.getInt(
                    ctx.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
            mRotationButton.setImageResource(mRotationEnabled ?
                    R.drawable.auto_rotate_on : R.drawable.auto_rotate_off);

            mRingerMode = am.getRingerMode();
            if (mRingerMode == AudioManager.RINGER_MODE_SILENT) {
                mSoundButton.setImageResource(R.drawable.sound_off);
            } else if (mRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                mSoundButton.setImageResource(R.drawable.vibrate_on);
            } else if (mRingerMode == AudioManager.RINGER_MODE_NORMAL) {
                mSoundButton.setImageResource(R.drawable.sound_on);
            }

            mAirplaneEnabled = (Settings.System.getInt(
                    ctx.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) == 1);
            mAirplaneButton.setImageResource(mAirplaneEnabled ?
                    R.drawable.airplane_on : R.drawable.airplane_off);

            mBluetoothButton.setImageResource(mBluetoothAdapter.isEnabled() ?
                    R.drawable.bt_on : R.drawable.bt_off);

            mGpsButton.setImageResource(mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER) ?
                    R.drawable.gps_on : R.drawable.gps_off);

            mBrightnessLevel = Settings.System.getInt(
                    ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            mAutoBrightEnabled = (Settings.System.getInt(
                    ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, 0) == 1);
            if (mAutoBrightEnabled) {
                mBrightnessButton.setImageResource(
                        R.drawable.brightness_auto);
            } else if (mBrightnessLevel == 255) {
                mBrightnessButton.setImageResource(
                        R.drawable.brightness_full);
            } else {
                mBrightnessButton.setImageResource(
                        R.drawable.brightness_half);
            }

            syncValue = ContentResolver.getMasterSyncAutomatically();
            mSyncButton.setImageResource(syncValue ?
                    R.drawable.sync_on : R.drawable.sync_off);

            /* This will always show as off..just the way it is */
            mTorchButton.setImageResource(R.drawable.torch_off);

        } catch (SettingNotFoundException b) {
            // Don't need logging yet.
        }		
	}
  
}