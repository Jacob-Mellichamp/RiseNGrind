package com.zybooks.risengrind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * DeleteAlarmActivity: Controller Class
 *
 *   - Ties UI and Model together for deleting alarms
 */
public class DeleteAlarmActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_alarm);

        //Define UI Elements
        RecyclerView recyclerView = findViewById(R.id.delete_alarm_list);
        DeleteAlarmAdapter adapter = new DeleteAlarmAdapter(this,
                AlarmDataBase.getInstance(this).getAlarms());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void btn_returnOnClick(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        view.getContext().startActivity(intent);
        this.finish();
    }

}