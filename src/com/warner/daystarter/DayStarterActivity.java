package com.warner.daystarter;

import com.crashlytics.android.Crashlytics;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/* 
 * To Do:
 *  	    Logo 
 * 			
 * 			
 */



public class DayStarterActivity extends Activity {
	
	public MediaPlayer mp;
	public ToggleButton waveButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (hasCrashlyticsApiKey(this)) 
        	Crashlytics.start(this);
        else
            Log.e("DayStarter", "No Crashlytics API key found. Visit http://crashlytics.com to set up an account.");
        
		setContentView(R.layout.main);
        
        //Create font for Clocktopia
        Typeface font = Typeface.createFromAsset(getAssets(), "Clockopia.ttf");
        
        //Check if this is the first run
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if(firstrun == true)
        {
        	/******DIALOG*****/
            //Set up dialog
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            
            //Set Up Title for introduction dialog
            TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
            title.setText("DayStarter");
            title.setTypeface(font);

            //Set Up Text inside dialog
            TextView text = (TextView) dialog.findViewById(R.id.TextView01);
            text.setText("Leave this application on overnight and use it for the drowsy morning.\n\n" +
            			 "Ocean Waves: Background noise for sleeping\n\n" +
            			 "Send: Default text message app access\n\n" +
            			 "If you do not have an application, it will bring you to the Android Market.");
                   
            dialog.show();
            /*******************/
            
            //Save the fact that after this, it won't be the first run with shared preference
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstrun", false).commit();
        	
        }
        
        
        /*****Set Up Clock ******/
        TextView clock = (TextView)findViewById(R.id.digitalClock);
        
        clock.setTypeface(font);
        
        clock.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		PackageManager packageManager = getBaseContext().getPackageManager();
                Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

                // Verify clock implementation
                String clockImpls[][] = {
                        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
                        {"Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
                        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
                        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"}
                };

                boolean foundClockImpl = false;

                for(int i=0; i<clockImpls.length; i++) 
                {
                    String vendor = clockImpls[i][0];
                    String packageName = clockImpls[i][1];
                    String className = clockImpls[i][2];
                    try 
                    {
                        ComponentName cn = new ComponentName(packageName, className);
                        ActivityInfo aInfo = packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
                        alarmClockIntent.setComponent(cn);
                        debug("Found " + vendor + " --> " + packageName + "/" + className);
                        foundClockImpl = true;
                    }
                    catch (NameNotFoundException e) 
                    {
                        debug(vendor + " does not exists");
                    }
                }

                if (foundClockImpl) 
                {
                    PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, alarmClockIntent, 0);
                    if(waveButton.isSelected() == true)
                    {
                    	waveButton.setSelected(false);
                    	waveButton.setChecked(false);
                    	stopMusic();
                    }
                    startActivity(alarmClockIntent);

                }
                else
                {
                	Toast.makeText(DayStarterActivity.this, "Alarm clock intent not found", Toast.LENGTH_SHORT).show();
                }
        		
        		
        	}
        });
        
        /*********************/
        
        
        //Check if the following applications exist
        checkTwitter();
        checkFacebook();
        
        
        /*****Set up Wave Sound Machine Button***/
        waveButton = (ToggleButton) findViewById(R.id.waveButton);
        
        
        waveButton.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		
        		//Perform actions on clicks
        		        		        		
        		if(waveButton.isChecked())
        		{
        			waveButton.setSelected(true);
        			startMusic();
        		}
        		else
        		{
        			debug("I'm in the else");
        			waveButton.setSelected(false);
        			stopMusic();
        		}
        	}
        });
        /***********************************/
        

        
    }
    
    
    public void debug(String customOutput) 
    {
    	//Used for debugging purposes
		Log.d("DayStarter", customOutput);
		
	}

	@Override
    public void onStop()
    {
    	super.onStop();
    	if(waveButton.isSelected() == true)
    	{
    		stopMusic();
    		waveButton.setSelected(false);
    	}
    }
    
    public void startMusic()
    {
    	
    	mp = MediaPlayer.create(this, R.raw.waves);
        mp.setLooping(true);
        /**Auto-generated**/
		try 
    	{
			mp.prepareAsync();
		} 
    	catch (IllegalStateException e) 
    	{
			e.printStackTrace();
		}
		
		
    	mp.start();
    	
    }
    
    public void stopMusic()
    {
    	if(mp != null)
    	{
    		if(mp.isPlaying())
    		{
    			mp.stop();
    	    	mp.setLooping(false);
    		}

        	mp.release();
    		mp = null;
    	}
    	
    }
    
    public void launchGmail(View v)
    {
    	
    	//com.google.android.gm/.ConversationListActivityGmail
    	
    	try{
            Intent intent = new Intent("android.intent.category.LAUNCHER");
            intent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail");
            startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
            	debug("Gmail not found, launching market");
                Intent market = new Intent(null, Uri.parse("market://details?id=com.google.android.gm"));
                startActivity(market);
            }
    	
    	
    }
    
    public void launchFacebook(View v)
    {
        try{
        Intent intent = new Intent("android.intent.category.LAUNCHER");
        intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
        startActivity(intent);
        }
        catch(ActivityNotFoundException e)
        {
        	debug("Facebook not found, launching market");
            Intent market = new Intent(null, Uri.parse("market://details?id=com.facebook.katana"));
            startActivity(market);
        }
        
        
    }
    
    public void launchTwitter(View v)
    {
        try{
        Intent intent = new Intent("android.intent.category.LAUNCHER");
        intent.setClassName("com.twitter.android", "com.twitter.android.StartActivity");
        startActivity(intent);
        }
        catch(ActivityNotFoundException e)
        {
        	debug("Twitter not found, launching market");
            Intent market = new Intent(null, Uri.parse("market://details?id=com.twitter.android"));
            startActivity(market);
        }
        
        
        
    }
    
    public void checkGmail()
    {
    	//com.google.android.gm/.ConversationListActivityGmail
    	
    	try{
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.gm", 0 );
        }
        catch( PackageManager.NameNotFoundException e )
        {
            ImageView gmail = (ImageView)findViewById(R.id.gmailImage);
            gmail.setImageResource(R.drawable.gmaildisabled);       
        }
    	
    	
    }
    
    public void checkTwitter()
    {
                
        try{
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.twitter.android", 0 );
        }
        catch( PackageManager.NameNotFoundException e )
        {
            ImageView twitter = (ImageView)findViewById(R.id.twitterImage);
            twitter.setImageResource(R.drawable.twitterdisabled);       
        }
        
    
    }
    
    public void checkFacebook()
    {
        
        try{
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.facebook.katana", 0 );
        }
        catch( PackageManager.NameNotFoundException e )
        {
            ImageView facebook = (ImageView)findViewById(R.id.facebookImage);
            facebook.setImageResource(R.drawable.facebookdisabled);
        }   
            
    }
    
    public void sendMessage(View v)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(intent);
        
    }
    

    
    /**
    
     * @return true if the Crashlytics API key is declared in AndroidManifest.xml metadata, otherwise return false.
  
     */
     static boolean hasCrashlyticsApiKey(Context context) 
     {
    	 boolean hasValidKey = false;
  
         try 
         {
  
             Context appContext = context.getApplicationContext();
  
             ApplicationInfo ai = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
  
             Bundle bundle = ai.metaData;
  
             if (bundle != null) 
             {
  
                 String apiKey = bundle.getString("com.crashlytics.ApiKey");
  
                 hasValidKey = apiKey != null && !apiKey.equals("0000000000000000000000000000000000000000");
  
             }
  
         } 
         catch (NameNotFoundException e) 
         {
  
             // Should not happen since the name was determined dynamically from the app context.
             Log.e("DayStarter", "Unexpected NameNotFound.", e);
  
         }
  
         return hasValidKey;
         
     }
    
}