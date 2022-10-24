package com.zybooks.risengrind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;


/*
 * DeleteAlarmAdapter
 *
 *   - The DeleteActivity contains a recyclerview, this class handles its binding, and event handlers.
 */
public class DeleteAlarmAdapter extends RecyclerView.Adapter<DeleteAlarmAdapter.AlarmHolder> {

    private final List<Alarm> alarmlist;
    private final Context context;
    private final LayoutInflater inflater;

    public DeleteAlarmAdapter(Context context, List<Alarm> list) {
        this.context = context;
        alarmlist = list;
        inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.alarm_delete_item, parent, false);
        AlarmHolder holder = new AlarmHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmHolder holder, int position) {

        Alarm alarm = alarmlist.get(position);
        holder.txt_deleteAlarm_display.setText(createAlarmText(alarm));

        holder.btn_del_individual_alarm.setOnClickListener(view -> removeAlarm(alarm));

    }

    public void removeAlarm(Alarm a) {
        int position = alarmlist.indexOf(a);

        AlarmDataBase.getInstance(context).getAlarm(a.getID()).disableAlarm();
        AlarmDataBase.getInstance(context).deleteAlarm(a.getID());
        AlarmDataBase.getInstance(context).writeChanges(context);

        notifyItemRemoved(position);
    }

    public String createAlarmText(Alarm a) {
        String alarmText = "";
        String timeDay = "AM";

        //get the hour
        if(a.getTime().get(Calendar.HOUR_OF_DAY) >= 12){
            timeDay = "PM";
        }

        if (a.getTime().get(Calendar.HOUR) == 0 ) {
            alarmText = "12:";
        } else {
            alarmText = String.format("%02d:", a.getTime().get(Calendar.HOUR));
        }

        alarmText += String.format("%02d", a.getTime().get(Calendar.MINUTE)) + " " + timeDay;

        return alarmText;
    }

    @Override
    public int getItemCount() {
        return alarmlist.size();
    }

    public class AlarmHolder extends RecyclerView.ViewHolder {

        private final TextView txt_deleteAlarm_display;
        private final ImageButton btn_del_individual_alarm;

        public AlarmHolder(@NonNull View itemView) {
            super(itemView);

            btn_del_individual_alarm = itemView.findViewById(R.id.btn_deleteAlarm);
            txt_deleteAlarm_display = itemView.findViewById(R.id.txt_deleteAlarm_name);
        }
    }
}
