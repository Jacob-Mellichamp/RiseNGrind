package com.zybooks.risengrind;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.Calendar;

public class LightAlarm extends Alarm implements SensorEventListener {

    public LightAlarm(Context con, int id, Calendar t, Boolean status) {
        super(con, id, t, status);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float lightThreshold = 75f;
        float lightLevel = sensorEvent.values[0];

        mlistener.progressUpdate((int)lightLevel);


        if (lightLevel > lightThreshold)
            mlistener.turnOff();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ignore
    }


    @Override
    public String toString() {
        return null;
    }
}
