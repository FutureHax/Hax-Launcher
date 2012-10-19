package com.t3hh4xx0r.haxlauncher.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.t3hh4xx0r.haxlauncher.Hotseat;

public class UpdateReceiver extends BroadcastReceiver {
  Context c;
  	
  @Override				
  public void onReceive(Context context, Intent intent) {
	  String action = intent.getAction();
	  c = context;
      LinkedHashMap<String, String> datas = new LinkedHashMap<String, String>();
	  try {
	      JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
	 
	      Iterator itr = json.keys();
	      while (itr.hasNext()) {
	        String key = (String) itr.next();
	        datas.put(key, json.getString(key));
	      }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	      	  
	  String url = datas.get("url");	      
	  DownloadTask t = new DownloadTask();
	  t.execute(url);	
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