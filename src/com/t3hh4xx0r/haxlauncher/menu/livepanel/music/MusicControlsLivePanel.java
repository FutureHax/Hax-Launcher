package com.t3hh4xx0r.haxlauncher.menu.livepanel.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;
import com.t3hh4xx0r.haxlauncher.menu.LivePanel;


public class MusicControlsLivePanel extends LivePanel implements OnClickListener {

	public static ImageButton mPlayPauseButton;
	public static ImageButton mSkipButton;
	public static ImageButton mRewindButton;
    public static ImageButton mStopButton;
    public static ImageButton mAlbumArt;
    public static final String PLAY = "play";
    public static final String PAUSE = "pause";
    public static final String TOGGLEPAUSE = "togglepause";
    public static final String STOP = "stop";
    public static final String PREVIOUS = "previous";
    public static final String NEXT = "next";
    private static String mArtist;
    private static String mTrack;
    private static boolean mPlaying;
    private static long mAlbumId = 420;
    private static String mAlbum;
    
	View root;
	public static StyledTextFoo mNowPlayingInfo;
	static Context ctx;
	static boolean isPlaying = false;

	public MusicControlsLivePanel(Context context) {
		super(context);
		ctx = context;
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = layoutInflater.inflate(R.layout.music_controls_lp, this);
        mPlayPauseButton = (ImageButton) findViewById(R.id.musicControlPlayPause);
        mPlayPauseButton.setOnClickListener(this);
        mRewindButton = (ImageButton) findViewById(R.id.musicControlPrevious);
        mRewindButton.setOnClickListener(this);
        mSkipButton = (ImageButton) findViewById(R.id.musicControlNext);
        mSkipButton.setOnClickListener(this);
        mNowPlayingInfo = (StyledTextFoo) findViewById(R.id.musicNowPlayingInfo);
        mNowPlayingInfo.setOnClickListener(this);
        mAlbumArt = (ImageButton) findViewById(R.id.albumArt);
        mAlbumArt.setOnClickListener(this);
        mNowPlayingInfo.setSelected(true); // set focus to TextView to allow scrolling
        mNowPlayingInfo.setTextColor(0xffffffff);
	}

	@Override
	public void onClick(View v) {
        if (v == mPlayPauseButton) {
        	sendMediaButtonEvent(TOGGLEPAUSE);
        	updateControls(420, null, null, null);
        } else if (v == mSkipButton) {
            sendMediaButtonEvent(NEXT);
        	updateControls(R.drawable.media_pause, null, null, null);
        } else if (v == mRewindButton) {
        	updateControls(R.drawable.media_pause, null, null, null);
            sendMediaButtonEvent(PREVIOUS);
        } else if (v == mStopButton) {
        	updateControls(420, null, null, null);
            sendMediaButtonEvent(STOP);
        }
	}	
	
    private void updateControls(int image, String artist, String album, String track) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    	AudioManager aM = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		if (image == 420) {
			image = (aM.isMusicActive() || mPlaying) ? R.drawable.media_pause : R.drawable.media_play; 
		}
		if (artist != null && track != null && album != null) {
			mNowPlayingInfo.setText(track);
		} else if (mArtist != null && mTrack != null && mAlbum != null) {
			mNowPlayingInfo.setText(mTrack);
		} else {            
			mNowPlayingInfo.setText(prefs.getString("_lastTrack", "Unknown"));
		}
		mNowPlayingInfo.setSelected(true);
		
		File art = null;
		if (mAlbumId != 420) {
			art = new File(Environment.getExternalStorageDirectory()+"/Android/data/com.google.android.music/cache/artwork/"+mAlbumId+".jpg");
		} else {
			art = new File(Environment.getExternalStorageDirectory()+"/Android/data/com.google.android.music/cache/artwork/"+prefs.getLong("_lastAlbumId", 420)+".jpg");
		}
		
		if (art.exists()) {
              InputStream i = null;
			try {
				i = new FileInputStream(art);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			  Bitmap bitmap = BitmapFactory.decodeStream(i);
			  mAlbumArt.setImageBitmap(bitmap);
		} else {
			//TODO check online?
			mAlbumArt.setImageResource(R.drawable.default_artwork);
		}
		mPlayPauseButton.setImageResource(image);    
    }
        
	private void sendMediaButtonEvent(String command) {
        Intent localIntent1 = new Intent();
        localIntent1.setAction("com.android.music.musicservicecommand");
        localIntent1.putExtra("command", command);
        localIntent1.putExtra("device", "local");
        getContext().sendBroadcast(localIntent1);
    }
    
    @Override
    protected void onAttachedToWindow() {  
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        ctx.registerReceiver(mMusicReceiver, iF);    	
    	updateControls(420, null, null, null);    	
    	super.onAttachedToWindow();
    }
    
    private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {        	
            mArtist = intent.getStringExtra("artist");
            mTrack = intent.getStringExtra("track");
            mPlaying = intent.getBooleanExtra("playing", false);
            mAlbumId = intent.getLongExtra("albumId", 0);
            mAlbum = intent.getStringExtra("album");
            updateControls(420, mArtist, mAlbum, mTrack);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Editor e = prefs.edit();
            e.putString("_lastArtist", mArtist);
            e.putString("_lastTrack", mTrack);
            e.putString("_lastAlbum", mAlbum);
            e.putLong("_lastAlbumId", mAlbumId);
            e.commit();
            Set<String> set = intent.getExtras().keySet();
            Iterator<String> i = set.iterator();
            while (i.hasNext()) {
            	Log.d(intent.getAction(), i.next());
            }
        }
    };
}