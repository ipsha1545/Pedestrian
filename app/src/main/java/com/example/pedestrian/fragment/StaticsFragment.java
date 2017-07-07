package com.example.pedestrian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.R;
import com.example.pedestrian.curl.Curl;
import com.example.pedestrian.utils.AppConstants;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by deepak.sharma on 4/27/2017.
 */

public class StaticsFragment extends Fragment {

    FragmentManager fragmentManager;
    private TextView tv_total_count, tv_due_amount;
    private EditText et_minutes,tv_amount ;
    private Button btn_submit ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statics, container, false);
        tv_total_count = (TextView) view.findViewById(R.id.tv_total_count);
        tv_amount = (EditText) view.findViewById(R.id.tv_amount) ;
        tv_due_amount = (TextView) view.findViewById(R.id.tv_due_amount);
        et_minutes = (EditText) view.findViewById(R.id.et_minutes);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);

        if (et_minutes.getText().toString().length() == 0){
            tv_amount.setEnabled(false);
        }
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_minutes.getText().toString().length() == 0){
                    Toast.makeText(getActivity(), "Please enter minutes", Toast.LENGTH_SHORT).show();
                }else {
                    tv_total_count.setText("Total Scan Count : " + getTotalCount(et_minutes.getText().toString()));
                    tv_amount.setEnabled(true);
                }

            }
        });
        tv_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equalsIgnoreCase("m")){
                    tv_amount.setEnabled(false);
                    tv_amount.setVisibility(View.GONE);
                    tv_due_amount.setVisibility(View.VISIBLE);
                    tv_due_amount.setText("Amount due - $" + Integer.parseInt(tv_total_count.getText().toString().replaceAll("[^0-9]", ""))/100.0f);

                }else {
                    fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                    fragmentTransaction2.replace(R.id.container, new MyMapFragment()).commit();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private int getTotalCount(String minutes) {
        List<LatLng> list = null;
        String response = null;
        try {

            if (Preferences.getSettingsParam("Radio").equalsIgnoreCase("All")){
                response  = Curl.getInSeparateThread(AppConstants.BaseURL + "/api/device" + "/" + minutes);
            }else if (Preferences.getSettingsParam("Radio").equalsIgnoreCase("Individual")){
                response = Curl.getInSeparateThread(AppConstants.BaseURL + "/api/device" + "/" + minutes + "/" + Preferences.getUserId());
            }

            list = GlobalUtils.readItems(response);
        } catch (Exception e) {
            GlobalUtils.writeLogFile("Exception in addHeatMap " + e.getMessage());
        }
        return list.size();
    }

}
