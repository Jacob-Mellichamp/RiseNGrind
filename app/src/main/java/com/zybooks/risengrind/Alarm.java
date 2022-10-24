package com.zybooks.risengrind;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;


/*
 * Meat of the Model
 * Alarm Class:
 *
 *   - Contains relevant information for an alarm instance. The Alarm is a parent class that can later
 *     be set to a specific type of alarm.
 *
 *   - it is NOT an abstract class, we do this because we would like to have initialized Alarms before
 *     setting them to their type.
 *
 *
 *   Attributes:
 *      - ID:int --> ID of the alarm (unique)
 *      - isEnabled:Boolean --> flag for if the alarm is currently enabled.
 *      - time:Calendar --> The actual time the alarm will go off
 *      - mlistener:AlarmListener
 *      - pendingIntent:PendingIntent --> The actual background executable that will later launch AlarmAlert
 *      - context:Context --> required for setting up calendar and pendingIntent
 */

public class Alarm {

    private int ID;
    private boolean isEnabled;
    private Calendar time;
    protected AlarmListener mlistener;


    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    Context context;


    //Constructor
    //@Param:
    //  - Con = Application Context
    //  - id  = ID to be assigned
    //  - t   = time to be assigned
    //  - status = status of alarm at initalization
    public Alarm (Context con, int id, Calendar t, Boolean status) {
        context = con;
        ID = id;
        time = t;
        isEnabled = status;
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        if(isEnabled){
            int requestCode = (int)time.getTimeInMillis()/1000;
            Intent intent = new Intent(context, AlarmReceiver.class);

            intent.putExtra("REQUEST_CODE", requestCode);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    0
            );

        }
    }


    public interface AlarmListener {
        void progressUpdate(int progress);
        void turnOff();
    }
    public void setListener(AlarmListener listener){ mlistener = listener; }


    /*
     * @Pre: None
     * @Post:
     *   - PendingIntent is launched to execute at the Alarm.t Time.
     *   - isEnabled set to true
     */
    public void enableAlarm() {
        isEnabled = true;
        setAlarm();
    }

    /*
     * @Pre: None
     * @Post:
     *   - PendingIntent is canceled if in memory
     *   - isEnabled set to false
     */
    public void disableAlarm() {
        isEnabled = false;
        AlarmDataBase.getInstance(context).writeChanges(context);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @SuppressLint("NewApi")
    /*
     * @Pre:
     *   - Alarm status was switched to enabled
     * @Post:
     *   - Pending Intent launched
     */
    private void setAlarm () {
        int requestCode = (int)time.getTimeInMillis()/1000;
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("AlarmID", ID);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        Calendar rightNow = Calendar.getInstance();

        if(time.before(rightNow)) {
            time.add(Calendar.DATE, 1);
        }

        pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                0
        );


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time.getTimeInMillis(),
                    pendingIntent
            );



            long difference = time.getTimeInMillis() - rightNow.getTimeInMillis();
            long hourDifference = (difference / (1000 * 60 * 60)) % 24;
            long minDifference = (difference / (1000 * 60)) % 60;


            String toastText  = "Alarm set for " + hourDifference +
                    " hours and " + minDifference + " minutes from now";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        } else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
        }
        AlarmDataBase.getInstance(context).writeChanges(context);
    }


    public void setTime(Calendar cal){
        time = cal;
    }

    // Class Getters
    public boolean getAlarmStatus() { return isEnabled; }
    public void setID (int id) { this.ID = id; }
    public int getID () { return ID; }
    public Calendar getTime() { return time; }


    @Override
    public String toString(){
        return "ID: \t" + ID + "\n" +
                "Status: \t" + isEnabled + "\n" +
                "Hour: \t" + time.get(Calendar.HOUR_OF_DAY) +
                "Minute: \t" + time.get(Calendar.MINUTE);
    }
}