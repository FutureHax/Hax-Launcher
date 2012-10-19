package com.t3hh4xx0r.haxlauncher.menu.livepanel;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
			try{
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
	      	PanelReceiver.requestPanels(PanelMenuActivity.this);

	    };      
	    @Override
		protected Void doInBackground(Object... params) {
	    	try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	return null;
		};      
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
		String version;
		Drawable screenCap;
		Drawable icon;
		int id;
	}
}
