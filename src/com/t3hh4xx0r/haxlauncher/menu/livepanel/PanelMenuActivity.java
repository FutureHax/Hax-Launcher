package com.t3hh4xx0r.haxlauncher.menu.livepanel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

public class PanelMenuActivity extends Activity {
	ListView lv;
	static ArrayAdapter<String> a;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel_menu);
		
		lv = (ListView) findViewById(R.id.panel_list);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos,
					long id) {
				Intent i = new Intent(v.getContext(), PanelDetails.class);
				Bundle b = new Bundle();
				b.putInt("pos", ((PanelDetailHolder)a.getItemAtPosition(pos)).id);
				i.putExtras(b);
				startActivity(i);
			}			
		});
		GetPanelsTask t = new GetPanelsTask();
		t.execute();
	}

	private ListAdapter buildAdapter(Context c) {
		return new PanelAdapter(this, getPanels(c));
	}

	public ArrayList<PanelDetailHolder> getPanels(Context ctx) {
		ArrayList<PanelDetailHolder> panelArray = new ArrayList<PanelDetailHolder>();		
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllPanels();
		while (c.moveToNext()) {
			try {
			     getPackageManager().getApplicationInfo(c.getString(c.getColumnIndex("package")), 0 );
			} catch (NameNotFoundException e){
				db.removePanel(c.getString(c.getColumnIndex("package")));
				continue;			
			}
			PanelDetailHolder p = new PanelDetailHolder();
			p.author = c.getString(c.getColumnIndex("author"));
			p.title = c.getString(c.getColumnIndex("name"));
			p.desc = c.getString(c.getColumnIndex("desc"));
			p.version = c.getString(c.getColumnIndex("version"));
			p.id = c.getPosition();
			panelArray.add(p);
		}
		c.close();
		db.close();
		return panelArray;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.refresh:
				GetPanelsTask t = new GetPanelsTask();
				t.execute();	
		}
		return false;
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.panel_menu, menu);
		return true;
	}
	
	
	public class GetPanelsTask extends AsyncTask<Object, View, Void> {
	    ProgressDialog progressDialog;
	    @Override
	    protected void onPreExecute() {
	        progressDialog= ProgressDialog.show(PanelMenuActivity.this, 
	        		"Loading...","Checking for live panels...", true);

	    };      
	    @Override
		protected Void doInBackground(Object... params) {
	    	DBAdapter db = new DBAdapter(PanelMenuActivity.this);
	    	db.open();
	    	ArrayList<String> tmpArray = new ArrayList<String>();
 	    	Cursor c = db.getAllPanels();
	    	PackageManager pm = getPackageManager();
	    	Intent i = new Intent("android.intent.action.MAIN");
	    	i.addCategory("com.t3hh4xx0r.haxlauncher.PANEL");
	    	List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
	    	if (lst != null && lst.size() != c.getCount()) {
	    		while (c.moveToNext()) {
		    		tmpArray.add(c.getString(c.getColumnIndex("package")));
		    	}
	    	   for (ResolveInfo resolveInfo : lst) {
	    	        if (!tmpArray.contains(resolveInfo.activityInfo.packageName)) {
	    	        	try {
							addPanel(resolveInfo, PanelMenuActivity.this);
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
	    	        }
	    	   }
	    	}
	    	return null;
		}; 
		
	    private void addPanel(ResolveInfo info, Context c) throws NameNotFoundException {
	    	PanelDetailHolder p = new PanelDetailHolder();
	    	PackageInfo pInfo = getPackageManager().getPackageInfo(info.activityInfo.packageName, 0);
			Context fC = c.getApplicationContext().createPackageContext(info.activityInfo.packageName, Context.CONTEXT_IGNORE_SECURITY);
			Resources res = fC.getResources();			

	    	p.pName = info.activityInfo.packageName;
			p.title = info.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
			p.version = pInfo.versionName;

			p.id = 0;
			p.author = res.getString(res.getIdentifier("author", "string", info.activityInfo.packageName));
			p.desc = res.getString(res.getIdentifier("description", "string", info.activityInfo.packageName));
			
	    	DBAdapter db = new DBAdapter(PanelMenuActivity.this);
	    	db.open();
 		    Log.d("ADDING THIS ITEM", p.version+":"+p.author+":"+p.title+":"+p.pName+":"+p.desc+(db.insertPanel(p.version, p.author, p.title, p.pName, p.desc, image("ic_launcher", PanelMenuActivity.this, p.pName), image("screencap", PanelMenuActivity.this, p.pName)) != -1));
 		    db.close();
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
		
		public Bitmap drawableToBitmap (Drawable drawable) {
		    if (drawable instanceof BitmapDrawable) {
		        return ((BitmapDrawable)drawable).getBitmap();
		    }

		    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		    Canvas canvas = new Canvas(bitmap); 
		    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		    drawable.draw(canvas);
		    return bitmap;
		}
		
		@Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	        progressDialog.dismiss();
			lv.setAdapter(buildAdapter(PanelMenuActivity.this));		
	    }
		
	 }
	
	
	class PanelDetailHolder {
		String author;
		String title;
		String desc;
		String pName;
		String version;
		Drawable screenCap;
		Drawable icon;
		int id;
	}
}
