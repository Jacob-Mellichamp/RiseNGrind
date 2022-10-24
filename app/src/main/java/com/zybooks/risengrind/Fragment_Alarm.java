package com.zybooks.risengrind;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

//Controller for displaying Alarms on the "Alarm" page.
public class Fragment_Alarm extends Fragment {


    private Switch individual_alarm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);




        RecyclerView recyclerView = view.findViewById(R.id.alarm_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        AlarmAdapter adapter = new AlarmAdapter(AlarmDataBase.getInstance(getContext()).getAlarms());
        recyclerView.setAdapter(adapter);

        return view;
    }


    // Referenced from Zybooks Chapter 6
    private class AlarmHolder extends RecyclerView.ViewHolder {

        private Alarm alarm;



        private AlarmHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.alarm_item, parent, false));
            individual_alarm = itemView.findViewById(R.id.switch_individual_alarm);
        }



        public void bind(Alarm a) {
            alarm = a;

            String alarmText = "";
            String timeDay = "AM";
            //get the hour
            if(alarm.getTime().get(Calendar.HOUR_OF_DAY) >= 12){
                timeDay = "PM";
            }


            if (alarm.getTime().get(Calendar.HOUR) == 0 ) {
                alarmText = "12:";
            } else {
                alarmText = String.format("%02d:",alarm.getTime().get(Calendar.HOUR));
            }

            alarmText += String.format("%02d", alarm.getTime().get(Calendar.MINUTE)) + " " + timeDay;

            individual_alarm.setText(alarmText);


            if(alarm.getAlarmStatus()){
                individual_alarm.setChecked(true);
            }



            individual_alarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(!isChecked){
                    AlarmDataBase.getInstance(getContext()).getAlarm(alarm.getID()).disableAlarm();

                } else{
                    AlarmDataBase.getInstance(getContext()).getAlarm(alarm.getID()).enableAlarm();
                }
            });


        }

    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private List<Alarm> alarmlist;

        public AlarmAdapter(List<Alarm> alarms) { alarmlist = alarms; }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new AlarmHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            Alarm alarm = alarmlist.get(position);
            holder.bind(alarm);
        }

        @Override
        public int getItemCount() { return alarmlist.size(); }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }
}