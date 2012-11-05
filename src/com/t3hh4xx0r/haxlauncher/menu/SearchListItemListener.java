package com.t3hh4xx0r.haxlauncher.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class SearchListItemListener extends BroadcastReceiver {
    static final String ACTION_NEW_PACKAGES = "com.t3hh4xx0r.haxlauncher.NEW_PACKAGES";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_PACKAGE_ADDED) ||
				action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
			    action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String data = intent.getData().toString();
			boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			
			if (action.equals(Intent.ACTION_PACKAGE_ADDED) && !replacing) {
				//add to db				
				Intent i = new Intent();
				i.setAction(ACTION_NEW_PACKAGES);
				Bundle b = new Bundle();
				b.putBooleanArray("items", new boolean[]{false, true});
				b.putInt("whatToDo", 2);
				b.putString("text", data.replaceFirst("package:", ""));
				i.putExtras(b);
				context.sendBroadcast(i);
			} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)  && !replacing) {
				//remove from db
				Intent i = new Intent();
				i.setAction(ACTION_NEW_PACKAGES);
				Bundle b = new Bundle();
				b.putInt("whatToDo", 1);
				
				b.putBooleanArray("items", new boolean[]{false, true});
				b.putString("text", data.replaceFirst("package:", ""));
				i.putExtras(b);
				context.sendBroadcast(i);
			} else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
				//Get new icon & app name
				Intent i = new Intent();
				i.setAction(ACTION_NEW_PACKAGES);
				Bundle b = new Bundle();
				b.putInt("whatToDo", 0);
				b.putBooleanArray("items", new boolean[]{false, true});
				b.putString("text", data.replaceFirst("package:", ""));
				i.putExtras(b);
				context.sendBroadcast(i);
			}
		}									
	}
}

