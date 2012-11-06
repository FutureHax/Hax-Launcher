package com.t3hh4xx0r.haxlauncher.parse;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

public class ChatReceiver extends BroadcastReceiver {
	String ACTION_CHAT_UPDATE = "com.t3hh4xx0r.haxlauncher.ACTION_CHAT_SENT_UPDATE";

	@Override
	public void onReceive(Context c, Intent i) {	
		String message = null;
		String sender;
		String time;		 
		 try {
		      JSONObject json = new JSONObject(i.getExtras().getString("com.parse.Data"));
		      message = json.getString("message");
		      time = json.getString("time");
		      sender = json.getString("sender");
		      DBAdapter db = new DBAdapter(c);
			  db.open();
			  db.insertChatMessage(sender, message, time);
			  db.close();	
			  
			  Intent intent = new Intent();
			  intent.setAction(ACTION_CHAT_UPDATE);
			  Bundle b = new Bundle();
			  b.putString("message", message);
			  b.putString("sender", sender);
			  b.putString("time", time);
			  intent.putExtras(b);
			  c.sendOrderedBroadcast(intent, null);
			 
			  if (!sender.equals(PushLogin.PrivatePushPreferencesProvider.getClientId(c))) {
				  notifyUser(b, c);
			  }
		    } catch (JSONException e) {
		    	e.printStackTrace();
		    }
	}

	private void notifyUser(Bundle b, Context c) {
		String message = b.getString("message");
		String time = b.getString("time");
		String sender = b.getString("sender");
		
		int icon = R.drawable.ic_launcher_application;
		long when = System.currentTimeMillis();
		CharSequence contentTitle = "New message from "+ sender; 
		Intent notificationIntent = new Intent(c, ClientPushActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);
		Notification notification = new Notification(icon, "New VIP Message!", when);
 	    notification.defaults = Notification.DEFAULT_VIBRATE;
 	    notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(c, contentTitle, message, contentIntent);
		final int HELLO_ID = 1;
		NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(
	                Context.NOTIFICATION_SERVICE);	
		mNotificationManager.notify(HELLO_ID, notification);			
	}
}