package com.coolapps;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCOptions;

/**
 * Created by Vanko7 on 13/04/2016.
 */
public class VLCSurfaceView extends SurfaceView implements LibVLC.HardwareAccelerationError, IVLCVout.Callback
{
    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;

    private static LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;

    public LibVLC GetLibVLC()
    {
        return mLibVLC;
    }

    public MediaPlayer GetMediaPlayer()
    {
        return mMediaPlayer;
    }

    public VLCSurfaceView(Context context) {
        super(context);
        Intialize();
    }

    public VLCSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Intialize();
    }

    public VLCSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Intialize();
    }

    public static void InitVLC(){
        ArrayList<String> options = new ArrayList<String>();
        //options.add("--subsdec-encoding <encoding>");
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity

        //mLibVLC = new LibVLC(VLCOptions.getLibOptions(this.getContext(), true, "UTF-8", true, "", true));
        mLibVLC = new LibVLC(options);
    }

    private void Intialize()
    {
        mLibVLC.setOnHardwareAccelerationError(this);

        mMediaPlayer = new MediaPlayer(mLibVLC);

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        //vlcVout.addCallback(this);
        vlcVout.setVideoView(this);
        vlcVout.attachViews();

/*
        int flags = VLCOptions.MEDIA_VIDEO;
        org.videolan.libvlc.Media media = new org.videolan.libvlc.Media(mLibVLC, Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        VLCOptions.setMediaOptions(media, this.getContext(), flags);
        mMediaPlayer.setMedia(media);
        mMediaPlayer.play();
*/
    }

    /*************
     * Events
     *************/

    @Override
    public void eventHardwareAccelerationError()
    {
         Toast.makeText(this.getContext(), "Hardware acceleration error occured", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        Log.i("onNewLayout", "w: " + width + " h: " + height + " vw: " + visibleWidth + " vh: " + visibleHeight);
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

    /*************
     * Surface
     *************/
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        // get screen size
        Activity activity = (Activity)this.getContext();
        int w = activity.getWindow().getDecorView().getWidth();
        int h = activity.getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size

        this.getHolder().setFixedSize(mVideoWidth, mVideoHeight);

        ViewGroup rootView = (ViewGroup)this.getParent();
        if(rootView.getClass() == FrameLayout.class)
        {
            Log.i("setSize", "FrameLayout " + w + " : " + h);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)this.getLayoutParams();
            lp.width = w;
            lp.height = h;
            this.setLayoutParams(lp);
        }
        else
        if(rootView.getClass() == GridLayout.class)
        {
            Log.i("setSize", "GridLayout " + w + " : " + h);
            GridLayout.LayoutParams lp = (GridLayout.LayoutParams)this.getLayoutParams();
            lp.width = w;
            lp.height = h;
            this.setLayoutParams(lp);
        }
        else
        if(rootView.getClass() == LinearLayout.class)
        {
            Log.i("setSize", "LinearLayout " + w + " : " + h);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)this.getLayoutParams();
            lp.width = w;
            lp.height = h;
            this.setLayoutParams(lp);
        }
        else
        if(rootView.getClass() == RelativeLayout.class)
        {
            Log.i("setSize", "RelativeLayout " + w + " : " + h);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)this.getLayoutParams();
            lp.width = w;
            lp.height = h;
            this.setLayoutParams(lp);
        }
        else
        if(rootView.getClass().toString().equals("org.nativescript.widgets.GridLayout"))
        {
            Log.i("setSize", "org.nativescript.widgets.GridLayout " + w + " : " + h);
            GridLayout.LayoutParams lp = (GridLayout.LayoutParams)this.getLayoutParams();
            lp.width = w;
            lp.height = h;
            this.setLayoutParams(lp);
        }

       /* Log.i("setSize", "org.nativescript.widgets.GridLayout1 " + w + " : " + h);
        org.nativescript.widgets.CommonLayoutParams lp = (org.nativescript.widgets.CommonLayoutParam)this.getLayoutParams();
        lp.width = w;
        lp.height = h;
        this.setLayoutParams(lp);
        */

        this.invalidate();
        Log.i("setSize", rootView.getClass().toString());
    }
}
