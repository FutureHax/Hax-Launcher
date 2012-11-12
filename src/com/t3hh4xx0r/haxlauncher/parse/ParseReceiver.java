package com.t3hh4xx0r.haxlauncher.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import com.t3hh4xx0r.haxlauncher.Launcher;
import com.t3hh4xx0r.haxlauncher.R;

public class ParseReceiver extends BroadcastReceiver {
	Context c;
	public static final String action_message = "com.t3hh4xx0r.haxlauncher.MESSAGE";
	public static final String action_update = "com.t3hh4xx0r.haxlauncher.UPDATE";  
	LinkedHashMap<String, String> datas;
	
	@TargetApi(16)
	@Override				
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		c = context;
		datas = getDatas(intent);
		      	  
		if (action.equals(action_update)) {
			String url = datas.get("url");	    
			DownloadTask t = new DownloadTask();
			t.execute(url);
		} else if (action.equals(action_message)){
			if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
				notifyJellyBean();
			} else {
				notifyPreJellyBean();
			}
			
		}
	
  }
  
  private void notifyPreJellyBean() {
		 int icon = R.drawable.ic_launcher_application;
		 CharSequence tickerText = datas.get("messageTitle");
		 long when = System.currentTimeMillis();
		 CharSequence contentTitle = datas.get("messageTitle"); 
		 CharSequence contentText = datas.get("messageShort");		 
		 Intent notificationIntent = new Intent(c, Launcher.class);
		 PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);
		 Notification notification = new Notification(icon, tickerText, when);
	     notification.defaults |= Notification.DEFAULT_VIBRATE | 
		    		Notification.DEFAULT_LIGHTS |
		    		Notification.DEFAULT_SOUND;
	     notification.flags |= Notification.FLAG_AUTO_CANCEL;
		 notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);
		 final int HELLO_ID = 1;
		 NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(
	                Context.NOTIFICATION_SERVICE);	
		 mNotificationManager.notify(HELLO_ID, notification);					
	}

  @TargetApi(16)
  private void notifyJellyBean() {
	    Intent notiIntent = new Intent(c, Launcher.class);
		Intent unSubIntent = new Intent(c, Launcher.class);

		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "r2doesinc@gmail.com" });
		sendIntent.setData(Uri.parse("r2doesinc@gmail.com"));
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "RE:"+datas.get("messageTitle"));
		sendIntent.setType("plain/text");
		
		PendingIntent pSendIntent = PendingIntent.getActivity(c, 0, sendIntent, 0);
		PendingIntent pIntent = PendingIntent.getActivity(c, 0, notiIntent, 0);
		PendingIntent pUnSubIntent = PendingIntent.getActivity(c, 0, unSubIntent, 0);

		Notification noti = new Notification.Builder(c)
		        .setContentTitle(datas.get("messageTitle"))
		        .setContentText(datas.get("messageShort"))
		        .setSmallIcon(R.drawable.ic_launcher_application)
		        .setContentIntent(pIntent)		        
		        .addAction(R.drawable.ic_launcher_application, "Unsubscribe", pUnSubIntent)
		        .addAction(R.drawable.ic_reply, "Reply", pSendIntent)
		        .setStyle(new Notification.BigTextStyle().bigText(datas.get("messageLong"))).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

	    noti.defaults |= Notification.DEFAULT_VIBRATE | 
	    		Notification.DEFAULT_LIGHTS |
	    		Notification.DEFAULT_SOUND;


		notificationManager.notify(0, noti); 		
	}

	private LinkedHashMap<String, String> getDatas(Intent intent) {
		  LinkedHashMap<String, String> d = new LinkedHashMap<String, String>();
		  try {
				JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
				
				Iterator itr = json.keys();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					d.put(key, json.getString(key));
			     }
			} catch (Exception e) {
				e.printStackTrace();
			}
		  return d;
		}
	
	private class DownloadTask extends AsyncTask<String, Void, File> {
	
	      @Override
	      protected File doInBackground(String... url) {
				File outputFile = null;
			try {
				URL apk = new URL(url[0]);
				HttpURLConnection c = (HttpURLConnection) apk.openConnection();
		          c.setRequestMethod("GET");
		          c.setDoOutput(true);
		          c.connect();
	
		          String PATH = Environment.getExternalStorageDirectory() + "/t3hh4xx0r/haxlauncher/";
		          File file = new File(PATH);
		          file.mkdirs();
		          outputFile = new File(file, url[0].split("hax_launcher/")[1]);
		          FileOutputStream fos = new FileOutputStream(outputFile);
		          InputStream is = c.getInputStream();
	
		          byte[] buffer = new byte[1024];
		          int len1 = 0;
		          while ((len1 = is.read(buffer)) != -1) {
		              fos.write(buffer, 0, len1);
		          }
		          fos.close();
		          is.close();
		    } catch (IOException e) {
				e.printStackTrace();
			}      		
	        return outputFile;
	      }      
	
	      @Override
	      protected void onPostExecute(File f) {   
	    	  Intent i = new Intent(Intent.ACTION_VIEW);
	    	  i.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
	    	  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	  c.startActivity(i);
	      }
	
	      @Override
	      protected void onPreExecute() {
	      }
	
	      @Override
	      protected void onProgressUpdate(Void... values) {
	      }
	  }	
}