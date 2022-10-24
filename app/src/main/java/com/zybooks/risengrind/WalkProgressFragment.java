package com.zybooks.risengrind;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
// Controller fragment that deals with displaying and updating walk progress on view
public class WalkProgressFragment extends Fragment {

    TextView walkProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk_progress,
                container, false);

        walkProgress = view.findViewById(R.id.walkProgressTextView);
        walkProgress.append(" " + 0);

        return view;
    }

    //Post: updates number of steps walked on view
    public void updateWalkProgress(Alarm a) {
        a.setListener(new Alarm.AlarmListener() {
              @Override
              public void progressUpdate(int progress) {
                  String baseText = getResources().getString(R.string.steps_walked);
                  walkProgress.setText(baseText);
                  walkProgress.append(" " + progress);
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