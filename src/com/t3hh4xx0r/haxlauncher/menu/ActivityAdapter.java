package com.t3hh4xx0r.haxlauncher.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;

public class ActivityAdapter extends BaseAdapter {
	 private LayoutInflater mInflater;
	 Context ctx;	 
	 Activity act;
	 ArrayList<String> names;
	 ArrayList<Bitmap> icons;
	 ArrayList<Boolean> selection;
	 boolean isMulti = false;

	 
	 public ActivityAdapter(Context context, ArrayList<String> namesIn, ArrayList<Bitmap> iconsIn, ArrayList<Boolean> selectionIn) {
		 icons = iconsIn;
		 names = namesIn;
		 selection = selectionIn;
		 mInflater = LayoutInflater.from(context);
		 ctx = context;
	 }

	public int getCount() {
		return names.size();
	 }

	 public Object getItem(int position) {
	  	return names.get(position);
	 }

	 public long getItemId(int position) {
		 return position;
	 }

	 public View getView(final int position, View convertView, ViewGroup parent) {
		 final ViewHolder holder;
		 convertView = mInflater.inflate(R.layout.activity_row, null);
		 holder = new ViewHolder();
		 holder.title = (StyledTextFoo) convertView.findViewById(R.id.title);
		 holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		 convertView.setTag(holder);   	   
	  
		 holder.title.setText(names.get(position));
		 holder.icon.setImageBitmap(icons.get(position));
		 if (isMulti) {
			 convertView.setBackgroundColor(selection.get(position) ? ctx.getResources().getColor(android.R.color.holo_blue_bright) : ctx.getResources().getColor(android.R.color.transparent));
		 } else {
			 convertView.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
		 }
		 return convertView;
	}	 

	class ViewHolder {
		StyledTextFoo title;
		ImageView icon;
	 }
}