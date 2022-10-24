package com.zybooks.risengrind;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


 /*
  *  MainActivity that is launched on startup.
  *  The MainActivity initializes our AlarmDatabase and Initial View.
  */
public class MainActivity extends AppCompatActivity {
    private ImageButton addAlarm;
    private AlarmDataBase alarmDB;
    public static final String ALERT_CHANNEL_ID = "ALERT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        alarmDB = new AlarmDataBase(getApplicationContext());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_home);
        addAlarm = findViewById(R.id.addAlarmButton);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_container, new Fragment_Alarm()).commit();

        getPermissions();
        createNotificationChannel(this);
    }



    /*
     * Event handler for navbar, inflate fragments
     *
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;

                switch(item.getItemId()){
                    case R.id.nav_setting:
                        selectedFragment = new Fragment_Settings();
                        break;
                    case R.id.nav_stats:
                        selectedFragment = new Fragment_Statistics();
                        break;
                    default:
                        selectedFragment = new Fragment_Alarm();
                        break;

                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragement_container, selectedFragment).commit();
                return true;
            };

    //Button Handler for adding
    //@Post: Launch AddAlarmActivity
    public void addAlarm_OnClick(View view) {
        Intent intent = new Intent(view.getContext(), AddAlarmActivity.class);
        view.getContext().startActivity(intent);
        this.finish();
    };

    //Button Handler for deleting
    //@Post: Launch DeleteAlarmActivity
    public void btn_editAlarmOnClick(View view){
        Intent intent = new Intent(view.getContext(), DeleteAlarmActivity.class);
        view.getContext().startActivity(intent);
        this.finish();
    }

    //Post: Get necessary hardware permissions
    private void getPermissions() {
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }
    }


    //Post: Creates notification channel for alarms
    private void createNotificationChannel(Context context) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel alert_channel =
                    new NotificationChannel(ALERT_CHANNEL_ID,
                            "Alarm NotificationManager",
                            NotificationManager.IMPORTANCE_HIGH);

            alert_channel.setVibrationPattern(new long[] {1000, 1000});

            alert_channel.setLightColor(Color.RED);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(alert_channel);
        }
    }
}