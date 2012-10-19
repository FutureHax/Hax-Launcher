package com.t3hh4xx0r.haxlauncher.menu.livepanel;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

import dalvik.system.PathClassLoader;

public class PanelDetails extends Activity {	
	byte[] screenCapRaw;
	TextView title;
	TextView desc;
	ImageView screenCap;
	
	String pName;
	String title_v;
	String desc_v;
	Drawable screenCap_v;
	boolean curStatus;
	ToggleButton statusToggle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel_deets);
		title = (TextView) findViewById(R.id.title);
		desc = (TextView) findViewById(R.id.desc);
		screenCap = (ImageView) findViewById(R.id.screencap);
		statusToggle = (ToggleButton) findViewById(R.id.status_toggle);
		statusToggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBAdapter db = new DBAdapter(v.getContext());
				db.open();			
				Cursor c = db.getAllPanels();
				if (c.getCount() != 0) {
					while (c.moveToNext()) {						
						if (title_v.equals(c.getString(c.getColumnIndex("name")) + " By " + c.getString(c.getColumnIndex("author")))) {
							db.updateStatus(Boolean.toString(isChecked()), c.getInt(c.getColumnIndex("_id")));
							break;
						}
					}
				}
				db.close();				
			}			
		});
		getValues();
	}
	
	public void getValues() {
		DBAdapter db = new DBAdapter(this);
	    db.open();
	    Cursor c = db.getAllPanels();
	    c.moveToPosition(getIntent().getExtras().getInt("pos"));
	    screenCapRaw = c.getBlob(c.getColumnIndex("screencap"));
	    desc_v = c.getString(c.getColumnIndex("desc"));
	    curStatus = Boolean.parseBoolean(c.getString(c.getColumnIndex("status")));
	    pName = c.getString(c.getColumnIndex("package"));
	    title_v = c.getString(c.getColumnIndex("name")) + " By " + c.getString(c.getColumnIndex("author"));
	    c.close();
	    db.close();
	    if (screenCapRaw != null) {
			   ByteArrayInputStream is = new ByteArrayInputStream(screenCapRaw);
			   screenCap_v = Drawable.createFromStream(is, "myBalls");			   
	    }	   
	    try {
			getExternalConfig();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	    setValues();
	}

	private void getExternalConfig() throws NameNotFoundException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String className = pName+".Configuration";
	    Context forgeinContext = createPackageContext(pName, Context.CONTEXT_IGNORE_SECURITY);  
        
        String apkName = getPackageManager().getApplicationInfo(
        		pName, 0).sourceDir;
        PathClassLoader myClassLoader = new dalvik.system.PathClassLoader(
                apkName, ClassLoader.getSystemClassLoader());
        Class<?> handler = Class.forName(className, true, myClassLoader);
        Constructor[] m = handler.getConstructors();
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().contains("Configuration")) {
            	LinearLayout ll = (LinearLayout) findViewById(R.id.root);
            	ll.addView((View)m[i].newInstance(new Object[] {forgeinContext}));
            }
        }		
	}

	private void setValues() {
		title.setText(title_v);
		desc.setText(desc_v);
		screenCap.setImageDrawable(screenCap_v);
		statusToggle.setChecked(curStatus);
	}
	
	public boolean isChecked() {
		curStatus = statusToggle.getText().equals("Enabled");
		return statusToggle.getText().equals("Enabled");
	}
	
}
