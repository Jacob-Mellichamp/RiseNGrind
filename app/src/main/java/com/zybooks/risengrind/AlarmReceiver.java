package com.zybooks.risengrind;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

//When pendingIntentFinishes, this class will handle it's code execution
public class AlarmReceiver extends BroadcastReceiver {
    private static final int ANDROID10 =  29;


    @Override
    public void onReceive(Context context, Intent intent) {

        // Turning off Alarm
        int alarmID = intent.getIntExtra("AlarmID", -1);
        Alarm a = AlarmDataBase.getInstance(context).getAlarm(alarmID);
        a.disableAlarm();
        AlarmDataBase.getInstance(context).writeChanges(context);

        // Setting stats
        StatisticsCalculator stats = new StatisticsCalculator(context);
        stats.checkBestTime(a.getTime());
        writeWakeupTime(context, a.getTime());
        stats.updateAverageTime();

        Intent launch_intent = new Intent(context, AlarmAlertActivity.class);

        // Handling alarm based off android version
        if(Build.VERSION.SDK_INT >= ANDROID10){
            MediaSingleton.getInstance(context).setAudio();
            MediaSingleton.getInstance(context).setupVibration();

            launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                    launch_intent, PendingIntent.FLAG_UPDATE_CURRENT);


            Notification.Builder incomingNotification = new Notification.Builder(context,
                    MainActivity.ALERT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setOngoing(true)
                    .setContentTitle("Alarm")
                    .setContentText("Your previously set alarm has gone off! " +
                            "Click here to terminate it")
                    .setChannelId(MainActivity.ALERT_CHANNEL_ID)
                    .setFullScreenIntent(fullScreenPendingIntent, true);

            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(9999, incomingNotification.build());

        } else {
            Log.d("RECEIVE:", "Old Android Phone");
            launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launch_intent);
        }

    }

    /**
     *
     * @Post
     * write wake-up time to text file
     */
    private void writeWakeupTime(Context context, Calendar cal) {
        File statisticsDir = new File(context.getFilesDir(), "statistics");

        try {
            File wakeUpTimeFile = new File(statisticsDir, "wake_up_times.txt");
            FileWriter writer = new FileWriter(wakeUpTimeFile, true);

            String text = cal.get(Calendar.HOUR_OF_DAY) + "," + cal.get(Calendar.MINUTE);

            writer.append(text);
            writer.append("\n");

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
