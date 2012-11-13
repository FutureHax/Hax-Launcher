package com.t3hh4xx0r.haxlauncher.menu;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

public class AddFragment extends ListFragment {
	ListView v;
	ActivityAdapter a;
	Context ctx;
	int pos;
	static ArrayList<String> names = new ArrayList<String>();
	static ArrayList<String> packages = new ArrayList<String>();
	static ArrayList<Bitmap> icons = new ArrayList<Bitmap>();
	static ArrayList<Boolean> selection = new ArrayList<Boolean>();
	 
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    ctx = container.getContext();
		    if (v != null) {
		        v.invalidate();
		    }
		    v = (ListView) inflater.inflate(R.layout.list, container, false);
		   
		    setupAdapterTask t = new setupAdapterTask();		    
		    t.execute();
		    return v;
	}

	private Bitmap getIconForPackage(String s) {
		PackageManager pm = ctx.getPackageManager();
		try {
			return drawableToBitmap(pm.getApplicationIcon(s));
		} catch (NameNotFoundException e) {
			return drawableToBitmap(ctx.getResources().getDrawable(R.drawable.ic_launcher_application));			
		}
	}
	
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
	
	public class setupAdapterTask extends AsyncTask<Object, View, Void> {
	    ProgressDialog progressDialog;
	    int progress; 
	    int total;
	    
	    @Override
	    protected void onPreExecute() {
	        progressDialog = ProgressDialog.show(ctx, 
	        		"Loading...","Getting all installed apps. ", true);
	    };      
	    @Override
		protected Void doInBackground(Object... params) {
	    	packages.clear();
	    	names.clear();
	    	icons.clear();
	    	selection.clear();

	    	DBAdapter db = new DBAdapter(ctx);
			db.open();
			Cursor c = db.getAllSearchablePackages();
			total = c.getCount();
			while (c.moveToNext()) {
				progress++;
				packages.add(c.getString(c.getColumnIndex("package")));
				names.add(c.getString(c.getColumnIndex("text")));
				icons.add(getIconForPackage(c.getString(c.getColumnIndex("package"))));
				selection.add(false);
				publishProgress();
			}
			c.close();
			return null;
			
		};  
		
	    @Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	        progressDialog.dismiss();
		    a = new ActivityAdapter(ctx, names, icons, selection);
		    v.setAdapter(a);		    
		    v.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); 
		    v.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
		        @Override
		        public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean b) {
		            Log.i("debug", "item " + position + " changed state to " +b);
		            selection.set(position, b);
		            DBAdapter db = new DBAdapter(ctx);
		        	db.open();
		            if (b) {
			        	db.insertHotseat(packages.get(position), bitmapToByte(icons.get(position)), names.get(position));			
			        	Log.d("AT LEAST ONE IS ADDED", "YA!");
		            } else {
		            	db.removeHotseat(packages.get(position));
		            }
		            a.notifyDataSetChanged();
		        }

		        @Override
		        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		            //TODO: real menu 
		            MenuInflater inflater = actionMode.getMenuInflater();
		            inflater.inflate(R.menu.panel_menu, menu);
		            a.isMulti = true;
		            a.notifyDataSetChanged();
		            return true;  
		        }

		        @Override
		        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		            return false;
		        }

		        @Override
		        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
					return false;
		        }

		        @Override
		        public void onDestroyActionMode(ActionMode actionMode) {
		        	a.isMulti = false;
		        	a.notifyDataSetChanged();
		        	AddActivity.pager.setCurrentItem(1, true);
		        }
		    });
	    }
	    
	    @Override
	    protected void onProgressUpdate(View... view) {
	    	super.onProgressUpdate(view);
	    	progressDialog.setMessage("Getting "+progress+" of "+total+" installed apps. ");
	    }
		
	 }
	
	byte[] bitmapToByte(Bitmap in) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		in.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
}

