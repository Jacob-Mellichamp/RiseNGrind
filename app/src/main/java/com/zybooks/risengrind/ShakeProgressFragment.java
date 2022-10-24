package com.zybooks.risengrind;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

// Fragment to deal with displaying and updating progress bar for shake alarm
public class ShakeProgressFragment extends Fragment {

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shake_progress,
                container, false);

        progressBar = view.findViewById(R.id.shakeProgressBar);

        return view;
    }

    // Post: updates progress bar on view
    public void updateProgressBar(Alarm a) {
        a.setListener(new Alarm.AlarmListener() {
            @Override
            public void progressUpdate(int progress) {
                progressBar.setProgress(progress, true);
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