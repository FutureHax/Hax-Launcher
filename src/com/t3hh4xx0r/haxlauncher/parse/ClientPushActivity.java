package com.t3hh4xx0r.haxlauncher.parse;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParsePush;
import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

public class ClientPushActivity extends Activity {
	ListView lv1;
	Button send;
	EditText input;
	
	String ACTION_CHAT = "com.t3hh4xx0r.haxlauncher.ACTION_CHAT_SENT";
	String ACTION_CHAT_UPDATE = "com.t3hh4xx0r.haxlauncher.ACTION_CHAT_SENT_UPDATE";
	int chatCount = 0;

	ArrayAdapter<String> a;
	ArrayList<String> chatList;
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.client_push);
	    lv1 = (ListView) findViewById(R.id.display_list);  
	    lv1.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    lv1.setStackFromBottom(true);
	    chatList = getChatList(this);
	    a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatList);
	    lv1.setAdapter(a);
	    
	    input = (EditText) findViewById(R.id.input);
	    send = (Button) findViewById(R.id.send_button);
	    send.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try {
			    	DateFormat f = DateFormat.getDateTimeInstance();
			    	String time = f.format(new Date());
					sendMessage(input.getText().toString(), PushLogin.PrivatePushPreferencesProvider.getClientId(v.getContext()), time);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				input.setText("");
			}	    	
	    });
	    
	    IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHAT_UPDATE);
        LocalChatReceiver uR = new LocalChatReceiver();
        registerReceiver(uR, filter);
        
	}
	
	protected void sendMessage(String message, String id, String time) throws JSONException {
		JSONObject data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
				"\"sender\": \""+id+"\"," +
				"\"time\": \""+time+"\"," +
				"\"message\": \""+message+"\"" +
				"}");

        ParsePush push = new ParsePush();
        push.setChannel("chat");
        push.setData(data);	
        push.sendInBackground();
	}

	private ArrayList<String> getChatList(Context c) {
		 ArrayList<String> res = new ArrayList<String>();
		 DBAdapter db = new DBAdapter(c);
		 db.open();
		 Cursor cur = db.getChats();
		 chatCount = cur.getCount();
		 while (cur.moveToNext()) {
			StringBuilder sB = new StringBuilder();
			sB.append(cur.getString(cur.getColumnIndex("sent_time")));
			sB.append(":");
			sB.append(cur.getString(cur.getColumnIndex("sender")));
			sB.append(" - ");
			sB.append(cur.getString(cur.getColumnIndex("message")));
			res.add(sB.toString());
		 }
		 return res;
	}
	
	public class LocalChatReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context c, Intent i) {		
			Bundle b = i.getExtras();
			String message = b.getString("message");
			String time = b.getString("time");
			String sender = b.getString("sender");
			
			StringBuilder sB = new StringBuilder();
			sB.append(time);
			sB.append(":");
			sB.append(sender);
			sB.append(" - ");
			sB.append(message);
			chatList.add(sB.toString());
			
			a.notifyDataSetChanged();
		}
	}

}
