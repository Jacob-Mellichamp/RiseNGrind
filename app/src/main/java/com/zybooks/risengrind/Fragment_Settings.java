package com.zybooks.risengrind;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;




public class Fragment_Settings extends Fragment {

    SharedPreferences sharedPref;
    Spinner wake_selector;
    SeekBar seekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        sharedPref = getActivity().getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        init_spinner(view);
        init_seekBar(view);

        return view;
    }

    //Post: Initialize Spinner that controls apps "Awake Type" Preference
    private void init_spinner(View view){
        //initialize spinner
        wake_selector = view.findViewById(R.id.wake_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.wake_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wake_selector.setAdapter(adapter);
        wake_selector.setOnItemSelectedListener(wake_listener);

        //set default selection
        String default_value = sharedPref.getString("wake_type", "Button");
        int spinner_position = adapter.getPosition(default_value);
        wake_selector.setSelection(spinner_position);
    }

    //POST: initialize Seekbar --> set the progress to what it has been before
    private void init_seekBar(View view){

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        //set default selection
        seekBar.setProgress(sharedPref.getInt("wake_time_delay", 3));
    }


    //Selector EventHandler
    //@Post: save selected value in sharedPreferences
    private AdapterView.OnItemSelectedListener wake_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = (String)parent.getItemAtPosition(position);
            SharedPreferences.Editor edt = sharedPref.edit();
            edt.putString("wake_type", item);
            edt.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //Event handler for Seekbar
    //@POST save state in sharedPreference file when changing values
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            SharedPreferences.Editor edt = sharedPref.edit();
            edt.putInt("wake_time_delay", progress);
            edt.apply();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // ignore
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // ignore
        }
    };
}
