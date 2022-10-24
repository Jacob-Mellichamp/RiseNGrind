package com.zybooks.risengrind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;


/*
 * AddAlarmActivity will contain the UI elements and functionality necessary to add alarms
 *  to the application
 */
public class AddAlarmActivity extends AppCompatActivity {

    //Get elements
    Button addButton;
    Button cancelButton;
    CheckBox is_recurring;

    LinearLayout weekdays;
    LinearLayout weekends;
    TimePicker alarm_setter;


    Calendar alarm_time;


    //Time Change Listener
    //Contains functionality set alarm instance values
    TimePicker.OnTimeChangedListener time_listener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            alarm_time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            alarm_time.set(Calendar.MINUTE, minute);
            alarm_time.set(Calendar.SECOND, 0);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);


        //Set Buttons
        addButton = findViewById(R.id.btn_addAlarm_activity);
        cancelButton = findViewById(R.id.btn_cancel);
        is_recurring = findViewById(R.id.check_recurring);
        weekdays = findViewById(R.id.layout_weekdays);
        weekends = findViewById(R.id.layout_weekends);
        alarm_setter = findViewById(R.id.fragment_createalarm_timePicker);
        alarm_setter.setOnTimeChangedListener(time_listener);
        alarm_time = Calendar.getInstance();


    }

    //Cancel Button Event handler
    //@Pre: User Click
    //@post:
    //      - Redirect back to mainactivity
    public void btn_cancelOnClick(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        view.getContext().startActivity(intent);
        this.finish();
    }

    //Add Alarm Event Handler
    //@Pre: User Click
    //  - Time is selected with time <HH:MM:PM|AM>
    //@post:
    //  - add item to AlarmList
    public void btn_addAlarmOnClick(View view) {
        AlarmDataBase.getInstance(getApplicationContext()).addAlarm(new Alarm(view.getContext(),
                AlarmDataBase.getInstance(getApplicationContext()).LIST_SIZE,
                alarm_time,
                false));


        File file = new File(view.getContext().getFilesDir(), "alarms");
        if (!file.exists()) {
            file.mkdir();
        }

        String saveTime = alarm_time.get(Calendar.HOUR_OF_DAY) + ","
                + alarm_time.get(Calendar.MINUTE) + ","
                + "false";

        try {
            File alarmFile = new File(file, "alarms.txt");
            FileWriter writer = new FileWriter(alarmFile, true);
            writer.append(saveTime);
            writer.append("\n");
            writer.flush();
            writer.close();
            //Toast.makeText(view.getContext(), time, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        view.getContext().startActivity(intent);
        this.finish();
    }

}