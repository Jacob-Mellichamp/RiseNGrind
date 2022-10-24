package com.zybooks.risengrind;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;


/*
 * AlertActivity:
 *   - Alarm screen that is launched when alarm goes off.
 *   - Launched by AlarmReceiver
 */
public class AlarmAlertActivity extends AppCompatActivity {

    private final String shakeAlarm = "Phone-Shake";
    private final String walkAlarm = "Walk";
    private final String lightAlarm = "Light";
    private final String buttonAlarm = "Button";

    private String wakeType;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private Alarm alarm;
    private Vibrator vibrator;
    private MediaPlayer sound;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(Build.VERSION.SDK_INT >= 29)) {
            setupAudio();
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            setupVibration();
        }
        setUpWindowOptions();
        setContentView(R.layout.activity_alarm_alert);



        wakeType = getSharedPreferences("myprefs", Context.MODE_PRIVATE)
                .getString("wake_type", null);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        activity = this;
        setSensorType();
    }

    private void setUpWindowOptions() {
        Window win = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
        else {
            win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            );


        }

        win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    //Post: Vibrate Phone
    private void setupVibration() {
        long[] timing = {1000, 1000};
        int[] amplitude = {255, 255};

        vibrator.vibrate(VibrationEffect.createWaveform(timing, amplitude, 0));
    }

    //Pre: Activity has been launched by AlarmReceiver
    //Post:
    //      Sound Media Begins to Play
    //      Sound Media Begins to Loop
    //      Runnable "VolumeRunnable" is launched by Handler
    private void setupAudio(){
        sound = MediaPlayer.create(getApplicationContext(), R.raw.alarm_wakeup);
        sound.setLooping(true);
        sound.start();

        //Handle Audio
        Handler audioHandler = new Handler();
        AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        SharedPreferences sharedPreferences = this.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        audioHandler.post(new VolumeRunnable(mAudioManager, audioHandler, sharedPreferences.getInt("wake_time_delay", 0)));
    }

    //Post: Initialize Alarm Type, and Fragment View to use
    private void setSensorType() {
        switch (wakeType) {
            case shakeAlarm:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                alarm = new ShakeAlarm(this, -1, null, false);

                ShakeProgressFragment shakeFragment = new ShakeProgressFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.progressFragmentContainer,
                        shakeFragment).commit();
                shakeFragment.updateProgressBar(alarm);

                break;
            case walkAlarm:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                alarm = new WalkAlarm(this, -1, null, false);

                WalkProgressFragment walkFragment = new WalkProgressFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.progressFragmentContainer,
                        walkFragment).commit();
                walkFragment.updateWalkProgress(alarm);

                break;
            case lightAlarm:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                alarm = new LightAlarm(this, -1, null, false);

                LightProgressFragment lightFragment = new LightProgressFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.progressFragmentContainer,
                        lightFragment).commit();
                lightFragment.updateWalkProgress(alarm);
                break;
            case buttonAlarm:
                alarm = new ButtonAlarm(this, -1, null, false);
                TextView progress = findViewById(R.id.currProgress);
                progress.setVisibility(View.GONE);

                Button turnOff = findViewById(R.id.btn_turnOff);
                turnOff.setVisibility(View.VISIBLE);
                turnOff.setOnClickListener(listener);
                break;
        }

    }

    /**
     * @post
     * registers needed listener depending on wake up type selected
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mSensor != null) {
            switch (wakeType) {
                case shakeAlarm:
                    mSensorManager.registerListener((ShakeAlarm) alarm, mSensor,
                            SensorManager.SENSOR_DELAY_UI);
                    break;
                case walkAlarm:
                    mSensorManager.registerListener((WalkAlarm) alarm, mSensor,
                            SensorManager.SENSOR_DELAY_UI);
                    break;
                case lightAlarm:
                    mSensorManager.registerListener((LightAlarm) alarm, mSensor,
                            SensorManager.SENSOR_DELAY_UI);
                    break;
            }
        }
    }
    /**
     * @post
     * unregisters needed listener depending on wake up type selected
     */
    @Override
    public void onPause() {
        super.onPause();

        if (mSensor != null) {
            switch (wakeType) {
                case shakeAlarm:
                    mSensorManager.unregisterListener((ShakeAlarm) alarm);
                    break;
                case walkAlarm:
                    mSensorManager.unregisterListener((WalkAlarm) alarm);
                    break;
                case lightAlarm:
                    mSensorManager.unregisterListener((LightAlarm) alarm);
                    break;
            }
        }
    }

    //Post: cancels sound, vibration, and notification and destroys activity
    @Override
    public void onDestroy() {
        super.onDestroy();

        if(Build.VERSION.SDK_INT >= 29){
            MediaSingleton.getInstance(getApplicationContext()).disableAudio();
            MediaSingleton.getInstance(getApplicationContext()).disableVibration();
        } else {
            sound.stop();
            sound.release();
            vibrator.cancel();
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancelAll();

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (activity != null) {
                activity.finish();
            }
        }
    };
}