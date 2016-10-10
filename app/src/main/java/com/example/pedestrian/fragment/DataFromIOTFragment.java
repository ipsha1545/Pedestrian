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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.data_from_iot, container, false);

        final TextView tv_list_from_iot = (TextView) view.findViewById(R.id.list_from_iot);

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

                } catch (Exception e){

                }

        }
        });


        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Preferences.setStringArray("list", new LinkedHashSet<String>());

                tv_list_from_iot.setText("");

            }
        });



        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
