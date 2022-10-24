package com.zybooks.risengrind;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;

// Model class to deal with detecting shakes and updating progress
public class ShakeAlarm extends Alarm implements SensorEventListener {

    private int totalForce = 0;

    public ShakeAlarm(Context con, int id, Calendar t, Boolean status) {
        super(con, id, t, status);
    }

    /* Post: detects shake and updates view
    *  - Will cancel activity if progress threshold is reached */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // Get accelerometer values
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        // Detect shake
        float SHAKE_THRESHOLD = 1.5f;
        if (gForce > SHAKE_THRESHOLD) {
            totalForce += 1;
            mlistener.progressUpdate(totalForce);
        }

        if (totalForce >= 500)
            mlistener.turnOff();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ignore
    }
}

