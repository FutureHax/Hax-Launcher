package com.t3hh4xx0r.haxlauncher.menu;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.t3hh4xx0r.haxlauncher.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;

public class SearchAdapter extends BaseAdapter {
	 ArrayList<String> selectionList;
	 final PackageManager pm;
	 private LayoutInflater mInflater;
	 Context ctx;	 
	 String title;
	 ImageView icon;
	 Object tmpIcon;
	 Activity activity;
	 
	 public SearchAdapter(Context context, ArrayList<String> list, Activity act) {
	  pm = context.getPackageManager();
	  selectionList = list;
	  mInflater = LayoutInflater.from(context);
	  ctx = context;	  
	  activity = act;
	 }

	public int getCount() {
	  return selectionList.size();
	 }

	 public Object getItem(int position) {
	  return selectionList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(final int position, View convertView, ViewGroup parent) {
	  final ViewHolder holder;
	  if (convertView == null) {
		  convertView = mInflater.inflate(R.layout.search_row, null);
		  holder = new ViewHolder();
		  holder.title = (StyledTextFoo) convertView.findViewById(R.id.title);
		  convertView.setTag(holder);   	   	  
	  } else {
		  holder = (ViewHolder) convertView.getTag();
	  }
  
	  holder.title.setText(selectionList.get(position));
//	  if (selectionList.size() == 1) {
//		  setIcon(holder.icon, selectionList.get(position));
//	  }
	  return convertView;
	 }

	 private void setIcon(final ImageView holder, final String name ) {
		Thread thread = new Thread() {
	    @Override
	    public void run() {
			 List<ApplicationInfo> applications = pm.getInstalledApplications(0);
			 for (int n=0; n < applications.size(); n++) {
				if (applications.get(n).loadLabel(pm).equals(name)) {
					tmpIcon = ((Drawable)applications.get(n).loadIcon(pm));
				}
		     }		
			 if (tmpIcon == null) {
				Cursor c = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
				while (c.moveToNext()) {
					if (name.equals((c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) + " - " + 
							c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))))) {
						Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong((c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID)))));
					    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
					    tmpIcon = BitmapFactory.decodeStream(input);
					}
				}
				c.close();
				tmpIcon = null;
			 }
			 activity.runOnUiThread(new Runnable() {
	               @Override
	               public void run() {
	            	   if (tmpIcon instanceof Bitmap) {
	       					holder.setImageBitmap((Bitmap) tmpIcon);
	            	   } else if (tmpIcon instanceof Drawable){
	            		   holder.setImageDrawable((Drawable) tmpIcon);
	            	   } else if (tmpIcon == null) {
	            		   Log.d("Fuck Your Life","SHITCOCK");
	            	   }
	               }
	         });		    			    		    	
	    }
	};
	thread.start();		
	}	 

	static class ViewHolder {
	  StyledTextFoo title;
	  ImageView icon;
	 }
	}