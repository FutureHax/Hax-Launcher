package com.t3hh4xx0r.haxlauncher.menu.livepanel;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

public class PanelAdapter extends BaseAdapter {
	 ArrayList<PanelMenuActivity.PanelDetailHolder> selectionList;
	
	 private LayoutInflater mInflater;
	 Context ctx;	 
	 String title;
	 String desc;
	 byte[] iconRaw;
	 
	 public PanelAdapter(Context context, ArrayList<PanelMenuActivity.PanelDetailHolder> list) {
		 selectionList = list;
		 mInflater = LayoutInflater.from(context);
		 ctx = context;	
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
			  convertView = mInflater.inflate(R.layout.panel_item, null);
			  holder = new ViewHolder();
			  holder.title = (StyledTextFoo) convertView.findViewById(R.id.title);
			  holder.desc = (StyledTextFoo) convertView.findViewById(R.id.desc);
			  holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			  convertView.setTag(holder);   	   	  
		  } else {
			  holder = (ViewHolder) convertView.getTag();
		  }
		  title = selectionList.get(position).title+" by "+selectionList.get(position).author;
		  desc = selectionList.get(position).desc;
		  DBAdapter db = new DBAdapter(ctx);
	      db.open();
	      Cursor c = db.getAllPanels();
	      if (c.getCount() != 0) {
	    	  try {
	    		  while (c.moveToNext()) {
	    			  if (c.getString(c.getColumnIndex("name")).equals(selectionList.get(position).title)
	    					  && c.getString(c.getColumnIndex("author")).equals(selectionList.get(position).author)) {	 
	    				  iconRaw = c.getBlob(c.getColumnIndex("icon"));
	    				  break;
	    			  }
	    		  }
	    	  } catch (Exception e) {
	    		  e.printStackTrace();
	    	  }
	       }
	       c.close();
	       db.close();
	      
		   holder.title.setText(title);
		   holder.desc.setText(desc);
		   if (iconRaw != null) {
			   ByteArrayInputStream is = new ByteArrayInputStream(iconRaw);
			   Drawable d = Drawable.createFromStream(is, "myBalls");
			   holder.icon.setImageDrawable(d);
		   }
		   return convertView;
	 }

	 static class ViewHolder {
		  StyledTextFoo title;
		  StyledTextFoo desc;
		  ImageView icon;
	 }
}