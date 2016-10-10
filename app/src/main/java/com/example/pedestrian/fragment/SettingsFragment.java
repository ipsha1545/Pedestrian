package com.example.pedestrian.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.R;
import com.example.pedestrian.utils.Preferences;

/**
 * Created by lokesh.gupta on 10/4/2016.
 */
public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.iot_data, container, false);

        final EditText tv_duration = (EditText) view.findViewById(R.id.tv);
        final EditText tv_map_zoom = (EditText) view.findViewById(R.id.map_zoom);


        // Show Android App version
        TextView version = (TextView) view.findViewById(R.id.version);
        try{
            version.setText("Android Version: " + getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        } catch (Exception e){

        }

        // Show last saved values (if any)
        if(!Preferences.getSettingsParam("duration").equals("")){
            tv_duration.setText(Preferences.getSettingsParam("duration"));
        }

        if(!Preferences.getSettingsParam("mapzoom").equals("")){
            tv_map_zoom.setText(Preferences.getSettingsParam("mapzoom"));
        }




        Button button = (Button) view.findViewById(R.id.submitSettings);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!tv_duration.getText().toString().equals("") && !tv_map_zoom.getText().toString().equals("") ){

                    Preferences.setSettingsParam("duration", tv_duration.getText().toString());
                    Preferences.setSettingsParam("mapzoom",  tv_map_zoom.getText().toString());

                    Toast.makeText(getActivity(), "Setting Saved, Go to MapView", Toast.LENGTH_SHORT).show();

                    try {
                        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                    }
                } else{
                    Toast.makeText(getActivity(), "No field can be left empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
