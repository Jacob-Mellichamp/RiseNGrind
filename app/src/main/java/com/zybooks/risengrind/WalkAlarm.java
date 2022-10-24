package com.zybooks.risengrind;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.Calendar;
// Model class to deal with the walk alarm sensor implementation
public class WalkAlarm extends Alarm implements SensorEventListener {

    private int steps = 0;
    private final int SET_STEPS = 50;

    public WalkAlarm(Context con, int id, Calendar t, Boolean status) {
        super(con, id, t, status);
    }

    //Post: increments number of steps taken
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.values[0] == 1)
            steps++;

        mlistener.progressUpdate(steps);

        if (steps >= SET_STEPS)
            mlistener.turnOff();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ignore
    }
}
