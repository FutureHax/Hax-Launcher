package com.t3hh4xx0r.haxlauncher.parse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.StrictMode;
import android.util.Log;

class IOUtils {

  public static boolean getUrlResponse(String url) {
	  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	  StrictMode.setThreadPolicy(policy);
	  
	try {
		  URL myurl = new URL(url);
		  URLConnection connection = myurl.openConnection();
		  HttpURLConnection httpConnection = (HttpURLConnection) connection;
		  Log.d("OMG WTF", httpConnection.getResponseMessage() +"");
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return false;
  }

}
