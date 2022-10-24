package com.zybooks.risengrind;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * class that deals with calculating, updating, and saving the user statistics
 */
public class StatisticsCalculator {

    private Calendar earliestTime;
    private Calendar averageTime;
    private Context context;

    public StatisticsCalculator(Context context) {
        earliestTime = Calendar.getInstance();
        averageTime = Calendar.getInstance();
        this.context = context;
        setTimesFromFile();
    }

    /**
     * @post
     * set earliest and average times which are saved in a file if that file exists
     */
    public void setTimesFromFile() {

        File statisticsDir = new File(context.getFilesDir(), "statistics");

        if (!statisticsDir.exists()) {
            return;
        }

        String line;
        try {
            File statisticsFile = new File(statisticsDir, "statistics.txt");
            BufferedReader buffReader = new BufferedReader(new FileReader(statisticsFile));

            while((line = buffReader.readLine()) != null){
                //Parsing of line
                String[] stats = line.split(",");
                String timeType = stats[0];
                int line_hour = Integer.parseInt(stats[1]);
                int line_minute = Integer.parseInt(stats[2]);

                if (timeType.equals("earliest")) {
                    earliestTime.set(Calendar.HOUR_OF_DAY, line_hour);
                    earliestTime.set(Calendar.MINUTE, line_minute);
                }
                else {
                    averageTime.set(Calendar.HOUR_OF_DAY, line_hour);
                    averageTime.set(Calendar.MINUTE, line_minute);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @post
     * writes updated times to the file
     */
    private void writeChanges(){

        File statisticsDir = new File(context.getFilesDir(), "statistics");
        if (!statisticsDir.exists()) {
            statisticsDir.mkdir();
        }

        try {
            File statisticsFile = new File(statisticsDir, "statistics.txt");
            FileWriter writer = new FileWriter(statisticsFile, false);

            String text = "earliest," + earliestTime.get(Calendar.HOUR_OF_DAY) + "," +
                    earliestTime.get(Calendar.MINUTE);

            writer.append(text);
            writer.append("\n");

            text = "average," + averageTime.get(Calendar.HOUR_OF_DAY) + "," +
                    averageTime.get(Calendar.MINUTE);

            writer.append(text);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Calendar getEarliestTime() {
        return earliestTime;
    }

    public Calendar getAverageTime() {
        return averageTime;
    }

    /**
     *
     * @param time
     * @pre
     * time is valid calendar object
     * @post
     * updates earliest time if the input time is better than saved time
     */
    public void checkBestTime(Calendar time) {
        if (time.getTimeInMillis() <= earliestTime.getTimeInMillis())
            earliestTime = time;
            writeChanges();
    }

    /**
     * @post
     * updates average time
     */
    public void updateAverageTime() {
        File statisticsDir = new File(context.getFilesDir(), "statistics");

        ArrayList<Calendar> timeList = new ArrayList<>();

        if (!statisticsDir.exists()) {
            return;
        }

        String line;
        try {
            File wakeUpTimeFile = new File(statisticsDir, "wake_up_times.txt");
            BufferedReader buffReader = new BufferedReader(new FileReader(wakeUpTimeFile));

            while((line = buffReader.readLine()) != null){
                //Parsing of line
                String[] stats = line.split(",");
                int line_hour = Integer.parseInt(stats[0]);
                int line_minute = Integer.parseInt(stats[1]);

                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, line_hour);
                time.set(Calendar.MINUTE, line_minute);
                timeList.add(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Probably not perfect but it seems to work
        long seconds = 0;
        for(Calendar time : timeList) {
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int minute = time.get(Calendar.MINUTE);

            seconds += hour * 60 * 60;
            seconds += minute * 60;
        }

        long avg = seconds / timeList.size();



        long minute = TimeUnit.SECONDS.toMinutes(avg) % 60;
        long hour = TimeUnit.SECONDS.toHours(avg) % 24;

        Calendar avgTime = Calendar.getInstance();
        avgTime.set(Calendar.HOUR_OF_DAY, (int)hour);
        avgTime.set(Calendar.MINUTE, (int)minute);

        averageTime = avgTime;
        writeChanges();
    }

    /**
     * @post
     * returns true if file exists other returns false
     * @return
     * true || false
     */
    public boolean fileExists() {
        File statisticsDir = new File(context.getFilesDir(), "statistics");
        return statisticsDir.exists();
    }
}
