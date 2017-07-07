package com.example.pedestrian.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.MapsActivity;
import com.example.pedestrian.R;

/**
 * Created by lokesh.gupta on 10/10/2016.
 */
public class BaseFragment extends Fragment {


    public void showToast(Context context, String title, String message,int icon, int background) {

        Toast toast = new Toast(MapsActivity.mContext);
        LayoutInflater inflater = (LayoutInflater) MapsActivity.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);
        TextView titleText = (TextView) view.findViewById(R.id.topText);
        TextView messageText = (TextView) view.findViewById(R.id.bottomText);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        iconView.setImageResource(icon);
        view.setBackgroundResource(background);
        titleText.setText(title);
        messageText.setText(message);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM,0,200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
