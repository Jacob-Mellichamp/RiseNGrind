package com.zybooks.risengrind;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

//Controller for displaying statistics information to the user.
public class Fragment_Statistics extends Fragment {

    private TextView earliestWakeup;
    private TextView averageWakeup;
    private StatisticsCalculator stats;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        earliestWakeup = view.findViewById(R.id.earlyTimeValue);
        averageWakeup = view.findViewById(R.id.averageTimeValue);

        stats = new StatisticsCalculator(getContext());

        setEarliestWakeupText();
        setAverageWakeupTime();

        return view;
    }

    //@Pre: None
    //@Post: Set and display the earliest wake up time from the "statistics.txt"
    private void setEarliestWakeupText() {
        String text = "";
        String timeDay = "AM";
        Calendar earliestTime = stats.getEarliestTime();

        if (!stats.fileExists()) {
            earliestWakeup.setText(R.string.None);
            return;
        }


        //get the hour
        if(earliestTime.get(Calendar.HOUR_OF_DAY) >= 12){
            timeDay = "PM";
        }


        if (earliestTime.get(Calendar.HOUR) == 0 ) {
            text = "12:";
        } else {
            text = String.format("%02d:",earliestTime.get(Calendar.HOUR));
        }

        text += String.format("%02d", earliestTime.get(Calendar.MINUTE)) + " " + timeDay;

        earliestWakeup.setText(text);
    }

    //@Pre: None
    //@Post: Set and Display average wakeup time from the "statistics.txt" file
    private void setAverageWakeupTime() {
        String text = "";
        String timeDay = "AM";
        Calendar averageTime = stats.getAverageTime();

        if (!stats.fileExists()) {
            averageWakeup.setText(R.string.None);
            return;
        }


        //get the hour
        if(averageTime.get(Calendar.HOUR_OF_DAY) >= 12){
            timeDay = "PM";
        }


        if (averageTime.get(Calendar.HOUR) == 0 ) {
            text = "12:";
        } else {
            text = String.format("%02d:",averageTime.get(Calendar.HOUR));
        }

        text += String.format("%02d", averageTime.get(Calendar.MINUTE)) + " " + timeDay;

        averageWakeup.setText(text);
    }
}
