package com.warner.daystarter.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.warner.daystarter.R;

public class BackgroundNoiseService extends Service
{
	MediaPlayer player;

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
    public void onCreate() 
	{
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.waves);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
	
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) 
    {
        // TO DO
    }
    
    public IBinder onUnBind(Intent arg0) 
    {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() 
    {

    }
    
    public void onPause() 
    {

    }
    
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }

}
