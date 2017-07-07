package com.example.pedestrian.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pedestrian.R;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lokesh.gupta on 10/4/2016.
 */
public class DataFromIOTFragment extends Fragment {
     TextView tv_list_from_iot ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = null;
        try {

            view = inflater.inflate(R.layout.data_from_iot, container, false);

            tv_list_from_iot  = (TextView) view.findViewById(R.id.list_from_iot);

            Button refreshData = (Button) view.findViewById(R.id.refreshData);
            Button clearData = (Button) view.findViewById(R.id.clearData);

            StringBuffer stringBuffer = new StringBuffer();
            Set<String> set = Preferences.getStringSet("list");
            if (set != null) {
                List sortedList = new ArrayList(set);
                Collections.sort(sortedList);
                Iterator iterator = sortedList.iterator();
                // check values
                while (iterator.hasNext()) {
                    stringBuffer.append(iterator.next() + "\n\n");
                }
                tv_list_from_iot.setText(stringBuffer);
            }


            refreshData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        StringBuffer stringBuffer = new StringBuffer();

                        Set<String> set = Preferences.getStringSet("list");

                        if (set != null) {
                            List sortedList = new ArrayList(set);
                            Collections.sort(sortedList);

                            Iterator iterator = sortedList.iterator();

                            // check values
                            while (iterator.hasNext()) {
                                stringBuffer.append(iterator.next() + "\n\n");
                            }

                            tv_list_from_iot.setText(stringBuffer);
                        }

                    } catch (Exception e) {
                        GlobalUtils.writeLogFile("Exception in refresh data " + e.getMessage());
                    }
                }
            });

            clearData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ClearDataFragment clearDataFragment = new ClearDataFragment();
                    clearDataFragment.show(getFragmentManager(), "");

                }
            });

        } catch (Exception e) {
            GlobalUtils.writeLogFile("Exception in Data From IOT " + e.getMessage());
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class ClearDataFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Clear Data")
                    .setMessage("Are you sure to clear data?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Preferences.setStringArray("list", new LinkedHashSet<String>());
                            tv_list_from_iot.setText("");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();
        }
    }
}