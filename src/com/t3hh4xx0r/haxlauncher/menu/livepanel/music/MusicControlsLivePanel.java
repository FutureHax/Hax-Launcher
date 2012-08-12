package com.t3hh4xx0r.haxlauncher.menu.livepanel.music;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;
import com.t3hh4xx0r.haxlauncher.menu.LauncherMenu.AnimateDockTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MusicControlsLivePanel extends RelativeLayout implements OnClickListener {

    ImageButton mPlayPauseButton;
    ImageButton mSkipButton;
    ImageButton mRewindButton;
    ImageButton mStopButton;
    ImageButton mAlbumArt;
	View root;
	StyledTextFoo mNowPlayingInfo;
	static Context ctx;
	boolean isPlaying = false;

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
            //((Activity) ctx).startService(new Intent(MusicService.ACTION_PLAY));
        } else if (v == mSkipButton) {
        } else if (v == mRewindButton) {
        } else if (v == mStopButton) {
        }
	}
	
    @Override
    protected void onAttachedToWindow() {

    	super.onAttachedToWindow();
    }
}