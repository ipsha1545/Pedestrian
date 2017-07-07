package com.example.pedestrian.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.R;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lokesh.gupta on 10/4/2016.
 */
public class SettingsFragment extends Fragment {

    FragmentManager fragmentManager;
    Calendar myCalendar, myCalendar1;
    DatePickerDialog.OnDateSetListener date , date1;
    private EditText et_from_date, et_To_date ;
    EditText tv_duration;
    View view ;
    RadioGroup radioUser;
    RadioButton radioButton ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_from_date.setText(sdf.format(myCalendar.getTime()));
        tv_duration.setText(null);
        tv_duration.setEnabled(false);
    }

    private void updateLabel1() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_To_date.setText(sdf.format(myCalendar.getTime()));
        tv_duration.setText(null);
        tv_duration.setEnabled(false);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.iot_data, container, false);

        tv_duration  = (EditText) view.findViewById(R.id.tv);
        final EditText tv_map_zoom = (EditText) view.findViewById(R.id.map_zoom);
        radioUser = (RadioGroup) view.findViewById(R.id.radioUser);
        radioUser.check(R.id.radio_all);

        tv_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tv_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_from_date.setText(null);
                et_To_date.setText(null);

                et_from_date.setEnabled(false);
                et_To_date.setEnabled(false);
            }
        });

        et_from_date = (EditText) view.findViewById(R.id.et_from_date);
        et_To_date = (EditText) view.findViewById(R.id.et_To_date);

        et_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_To_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getActivity(), date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        myCalendar1 = Calendar.getInstance();

        date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel1();
            }

        };

        // Show Android App version
        TextView version = (TextView) view.findViewById(R.id.version);
        try {
            version.setText("Android Version: " + getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);


            // Show last saved values (if any)
            if (!Preferences.getSettingsParam("duration").equals("")) {
                tv_duration.setText(Preferences.getSettingsParam("duration"));
            }

            if (!Preferences.getSettingsParam("mapzoom").equals("")) {
                tv_map_zoom.setText(Preferences.getSettingsParam("mapzoom"));
            }

            if (!Preferences.getSettingsParam("FromDate").equalsIgnoreCase("")){

                et_from_date.setText(Preferences.getSettingsParam("FromDate"));
            }


            if (!Preferences.getSettingsParam("ToDate").equalsIgnoreCase("")){

                et_To_date.setText(Preferences.getSettingsParam("ToDate"));
            }



            Button button = (Button) view.findViewById(R.id.submitSettings);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {

                    int selectedId = radioUser.getCheckedRadioButtonId();

                    radioButton = (RadioButton) view.findViewById(selectedId);


                    //if (!tv_duration.getText().toString().equals("") && !tv_map_zoom.getText().toString().equals("") ) {

                        Preferences.setSettingsParam("duration", tv_duration.getText().toString());
                        Preferences.setSettingsParam("mapzoom", tv_map_zoom.getText().toString());
                        Preferences.setSettingsParam("FromDate", et_from_date.getText().toString());
                        Preferences.setSettingsParam("ToDate", et_To_date.getText().toString());

                        Preferences.setSettingsParam("Radio", radioButton.getText().toString());

                        Toast.makeText(getActivity(), "Setting Saved, Go to map view", Toast.LENGTH_SHORT).show();
                        // Deepak code
                        /*fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.container, new MyMapFragment()).commit();*/

                        try {
                            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        } catch (Exception e) {
                        }
                    //} /*else {
                       // Toast.makeText(getActivity(), "No field can be left empty", Toast.LENGTH_SHORT).show();
                    //}*/
                }
            });
        } catch (Exception e){
            GlobalUtils.writeLogFile("Exception in Settings Fragment " + e.getMessage());
        }

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
