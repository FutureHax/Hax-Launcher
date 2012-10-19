package com.t3hh4xx0r.haxlauncher.menu.livepanel;

import java.io.ByteArrayOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.t3hh4xx0r.haxlauncher.DBAdapter;

public class PanelReceiver extends BroadcastReceiver {
	private static final String PANEL_REQUEST = "com.t3hh4xx0r.haxlauncher.PANEL_REQUEST";
	private static final String PANEL_REGISTER = "com.t3hh4xx0r.haxlauncher.PANEL_REGISTER";
	boolean shouldAdd = true;
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		String author = b.getString("author_name");
		String plugin = b.getString("plugin_name");
		String packageN = b.getString("package_name");
		String desc = b.getString("description");
		String version = b.getString("version");
		    			
		DBAdapter db = new DBAdapter(context);
		db.open();		
		Cursor c = db.getAllPanels();
		if (c.getCount() != 0) {
			while (c.moveToNext()) {
				if (c.getString(c.getColumnIndex("author")).equals(author) &&
						c.getString(c.getColumnIndex("package")).equals(packageN)){
					shouldAdd = false;
					if (!c.getString(c.getColumnIndex("version")).equals(version)) {
						db.removePanel(packageN);
						shouldAdd = true;
					}				
				}
			}
		}
		if (shouldAdd) {			
			db.insertPanel(version, author, plugin, packageN, desc, image("ic_launcher", context, packageN), image("screencap", context, packageN));
		}
		db.close();		
	}
	
	public static void requestPanels(Context context) {
		Intent i = new Intent();
		i.setAction(PANEL_REQUEST);
		context.sendBroadcast(i); 
	}
	
	public byte[] image(String imageName, Context c, String packageN) {
        try {
			Context fC = c.getApplicationContext().createPackageContext(packageN, Context.CONTEXT_IGNORE_SECURITY);
			Resources res = fC.getResources();			
			Bitmap photo = drawableToBitmap(res.getDrawable(res.getIdentifier(imageName, "drawable", packageN)));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
	    	return bos.toByteArray();
		} catch (NameNotFoundException e) {
			return null;	
		}  
	};
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);
	    return bitmap;
	}
}