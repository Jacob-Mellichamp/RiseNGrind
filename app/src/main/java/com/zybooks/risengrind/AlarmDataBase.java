package com.zybooks.risengrind;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * AlarmDatabase Class:
 *
 *   - class is SINGLETON, only one AlarmDataBase used throughout the application
 *
 *   - contains code for managing List of Alarms
 */
public class AlarmDataBase {

    private static AlarmDataBase alarmDB;
    private List<Alarm> alarms;
    public int LIST_SIZE;


    //Constructor
    public AlarmDataBase(Context context) {
        LIST_SIZE = 0;
        alarms = createDBfromFile(context);

    }

    public static AlarmDataBase getInstance(Context context) {
        if (alarmDB == null) {
            alarmDB = new AlarmDataBase(context);
        }
        return alarmDB;
    }



    //Pre:
    //Post: Read the contents of "alarms.txt"
    //      - Create alarms based on the contents
    //      - return List of Alarms
    public List<Alarm> createDBfromFile(Context context){

        alarms = new ArrayList<>();

        File file = new File(context.getFilesDir(), "alarms");

        if (!file.exists()) {
            return alarms;
        }

        String line;
        try {

            File alarmFile = new File(file, "alarms.txt");
            BufferedReader buffReader = new BufferedReader(new FileReader(alarmFile));

            while((line = buffReader.readLine()) != null){
                //Parsing of line
                String[] alarm_stats = line.split(",");
                int line_hour = Integer.parseInt(alarm_stats[0]);
                int line_minute = Integer.parseInt(alarm_stats[1]);

                Boolean isEnabled = false;
                if (alarm_stats[2].equals("true")) {
                    isEnabled = true;
                }
                //Creating of alarm
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, line_hour);
                calendar.set(Calendar.MINUTE, line_minute);
                calendar.set(Calendar.SECOND, 0);
                Alarm currAlarm = new Alarm(context, LIST_SIZE, calendar, isEnabled);
                addAlarm(currAlarm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alarms;
    }


    //Post: Return list of alarms
    public List<Alarm> getAlarms() {
        return alarms;
    }

    //Post: Return Single alarm based on alarmID
    public Alarm getAlarm(int alarmID) {
        for (Alarm alarm : alarms) {
            if (alarm.getID() == alarmID) {
                return alarm;
            }
        }
        return null;
    }

    //Param: a:Alarm --> alarm to be added to list
    //Post: add alarm to list
    public void addAlarm(Alarm a){
        LIST_SIZE += 1;
        a.setID(LIST_SIZE);
        alarms.add(a);
    }

    //Pre:
    //  - alarms list contains alarm with Parameter ID
    //post:
    //  - Return index of the alarm instance with the parameter ID
    public int get_alarm_index(int ID){
        return alarms.indexOf(getAlarm(ID));
    }


    //Post: Remove alarm with ID == id
    public void deleteAlarm(int id){
        alarms.remove(getAlarm(id));
    }




    /*
     * @Context: There is a file "alarms/alarms.txt"
     *
     * @Pre
     *   - On each item added or deleted from the AlarmDataBase.alarms attribute, launch writeChanges()
     *
     * @Post
     *   - Overwrite the "alarms/alarms.txt" file with the current contents of the AlarmDataBase.alarms
     *     values
     */
    public void writeChanges(Context context){

        File file = new File(context.getFilesDir(), "alarms");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            File alarmFile = new File(file, "alarms.txt");
            FileWriter writer = new FileWriter(alarmFile, false);

            for(int i = 0; i <alarms.size(); i++){
                String saveTime = alarms.get(i).getTime().get(Calendar.HOUR_OF_DAY) + ","
                        + alarms.get(i).getTime().get(Calendar.MINUTE) + ","
                        + alarms.get(i).getAlarmStatus();
                writer.append(saveTime);
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
