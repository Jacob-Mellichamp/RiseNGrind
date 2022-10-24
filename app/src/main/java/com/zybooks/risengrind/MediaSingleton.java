package com.zybooks.risengrind;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;


/*
 * Purpose of MediaSingleton:
 *   - The way our alarm application works is a AlarmReceiver Class Launches at the time of an alarm going off.
 *
 *   - For android API 10 and up, intents can't be launched automatically from the receiver class, and therefore a notification
 *     will instead be built to notify the user that an alarm went off.
 *
 *   - Our App will play sound when the notification pops up, but needs to also get passed to the
 *     AlertAlarmActivity so that when the user completes the task at hand,  the sound and vibration will stop.
 *
 */
public class MediaSingleton {
    private static MediaSingleton media;
    private MediaPlayer sound;
    private Context context;

    private Vibrator vibrator;


    //Constructor
    public MediaSingleton(Context context) {
        this.context = context;
        this.sound = MediaPlayer.create(context, R.raw.alarm_wakeup);
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public static MediaSingleton getInstance(Context context) {
        if (media == null) {
            media = new MediaSingleton(context);
        }

        return media;
    }


    /*
     * @Pre: Running Android API 10 +
     * @Post: starts infinite loop of audio
     *      - Starts a VolumeRunnable that will auto increment the volume of the alarm.
     */
    public void  setAudio(){
        sound.setLooping(true);
        sound.start();

        //Handle Audio
        Handler audioHandler = new Handler();
        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("myprefs",
                Context.MODE_PRIVATE
        );
        audioHandler.post(new VolumeRunnable(mAudioManager,
                audioHandler,
                sharedPreferences.getInt("wake_time_delay", 0))
        );
    }

    /*
     * @Pre: Running Android API 10 +
     *     - this.sound != Null
     * @Post:
     *     - stops music
     *     - releases music
     */
    public void disableAudio(){
        sound.stop();
        sound.release();

        sound = null;
    }


    /*
     * @Pre: Running Android API 10 +
     * @Post: Creates a vibrator class that vibrates for a second, pauses for a second
     */
    public void setupVibration(){
        long[] pattern = {1000, 1000};

        vibrator.vibrate(VibrationEffect.createWaveform(pattern,0),
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        );
    }

    /*
     * @Pre: Running Android API 10 +
     *     - this.vibrator != Null
     * @Post:
     *     - stops vibration
     */
    public void disableVibration(){
        vibrator.cancel();
    }



}
