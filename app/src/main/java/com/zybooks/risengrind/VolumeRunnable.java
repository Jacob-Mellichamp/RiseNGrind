package com.zybooks.risengrind;


import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

//Reference: https://stackoverflow.com/questions/17697747/android-alarm-volume-increase-gradually

/*
 * Contains the code to launch a Background thread (Runnable) that will incremently adjust
 * the volume of the applicaiton based on the sharedPreference.
 *
 *  ******** IMPORTANT: Utilizes a handler to launch the VolumeRunnable at given delays
 */
public class VolumeRunnable implements Runnable {
    private AudioManager mAudioManager;
    private Handler mHandlerThatWillIncreaseVolume;
    private int MAX_AUDIO;

    private float step_size;
    private int curr_step;
    private int NUM_STEPS;
    private int DELAY_UNTIL_NEXT_INCREASE;//5 seconds between each increment
    VolumeRunnable(AudioManager audioManager, Handler handler, int userDelay){
        this.mAudioManager = audioManager;

        MAX_AUDIO = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        NUM_STEPS = MAX_AUDIO;
        DELAY_UNTIL_NEXT_INCREASE = (int)(((60 * userDelay) / NUM_STEPS) * 1000) ;  //Working in milliseconds so multiply by 1000

        if (DELAY_UNTIL_NEXT_INCREASE == 0){
            DELAY_UNTIL_NEXT_INCREASE = 10;
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        this.mHandlerThatWillIncreaseVolume = handler;
    }
    @Override
    public void run() {
        int currentAlarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("ALARM VOLUME: ", "Current Volume: " + currentAlarmVolume);

        if(currentAlarmVolume != MAX_AUDIO){ //if we havent reached the max

            //here increase the volume of the alarm stream by adding currentAlarmVolume+someNewFactor
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
            mHandlerThatWillIncreaseVolume.postDelayed(this,DELAY_UNTIL_NEXT_INCREASE); //"recursively call this runnable again with some delay between each increment of the volume, untill the condition above is satisfied.
        }

    }
}
