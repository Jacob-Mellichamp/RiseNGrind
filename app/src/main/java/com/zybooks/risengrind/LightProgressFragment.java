package com.zybooks.risengrind;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class LightProgressFragment extends Fragment {

    TextView lightProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_progress,
                container, false);

        lightProgress = view.findViewById(R.id.lightProgressTextView);

        return view;
    }

    public void updateWalkProgress(Alarm a) {
        a.setListener(new Alarm.AlarmListener() {
            @Override
            public void progressUpdate(int progress) {
                String baseText = getResources().getString(R.string.current_light_value);
                lightProgress.setText(baseText);
                lightProgress.append(" " + progress);
            }

            @Override
            public void turnOff() {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });
    }
}