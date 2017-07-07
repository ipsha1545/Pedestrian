package com.example.pedestrian;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.pedestrian.utils.AppConstants;
import com.example.pedestrian.utils.DataFromIOT;
import com.example.pedestrian.utils.DataToServer;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by nischal.bansal on 9/22/2016.
 */
public class Myservice extends Service {



    @Override
    public void onCreate() {
        super.onCreate();

        GlobalUtils.writeLogFile("Service started");

        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();

        /*AcceptThreadtest acceptThreadtest = new AcceptThreadtest();
        acceptThreadtest.start();*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}




class AcceptThreadtest extends Thread {

    public void run() {

        try{

            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

            //String fromIOT = "{'DeviceID': 'L100', 'Signatures': '', 'TimeStamp': '28-Sep-2016 08:38:33', 'DevicesCount': 2}";

            String fromIOT = "{'TimeStamp': '30-Sep-2016 04:45:01', 'DevicesCount': 2, 'Signatures': [], 'DeviceID': 1}";
            String fromIOT1 = "{'TimeStamp': '30-Sep-2016 04:45:01', 'DevicesCount': 3, 'Signatures': [], 'DeviceID': 1}";
            String fromIOT2 = "{'TimeStamp': '30-Sep-2016 04:45:01', 'DevicesCount': 4, 'Signatures': [], 'DeviceID': 1}";
            String fromIOT3 = "{'TimeStamp': '30-Sep-2016 04:45:01', 'DevicesCount': 5, 'Signatures': [], 'DeviceID': 1}";

            Gson gson1 = new Gson();
            DataFromIOT dataFromIOT = gson1.fromJson(fromIOT, DataFromIOT.class);

            Preferences.setSettingsParam("datafromiot", fromIOT);

            //LinkedHashSet<String> linkedHashSet = (LinkedHashSet<String>) Preferences.getStringSet("list");
            Set<String> linkedHashSet =  Preferences.getStringSet("list");

            if(linkedHashSet == null){
                linkedHashSet = new LinkedHashSet<String>();
            }

            linkedHashSet.add(fromIOT);
            linkedHashSet.add(fromIOT1);
            linkedHashSet.add(fromIOT2);
            linkedHashSet.add(fromIOT3);

            Preferences.setStringArray("list", linkedHashSet);

            if (ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            LocationManager lm = (LocationManager) MapsActivity.mContext.getSystemService(MapsActivity.mContext.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            GlobalUtils.writeLogFile("data from iot " + fromIOT);

            GlobalUtils.writeLogFile("current lat: " + latitude);
            GlobalUtils.writeLogFile("current long: " + longitude);


            DataToServer dataToServer = new DataToServer();

            dataToServer.setSubId(GlobalUtils.getDeviceMac());
            dataToServer.setDeviceId(dataFromIOT.getDeviceID());
            dataToServer.setLongitude(longitude+"");
            dataToServer.setLatitude(latitude+"");
            dataToServer.setConnectedCount(dataFromIOT.getDevicesCount());
            dataToServer.setTimeStamp(dataFromIOT.getTimeStamp());
            dataToServer.setUserId(Preferences.getUserId());

            Gson gson = new Gson();

            String mdata = gson.toJson(dataToServer);

            GlobalUtils.writeLogFile("data to server " + mdata);

            URL url = null;

            try {
                url = new URL(AppConstants.BaseURL + "/api/device");
              /*  if (Preferences.getSettingsParam("Radio").equalsIgnoreCase("All")){

                }else if (Preferences.getSettingsParam("Radio").equalsIgnoreCase("Individual")){
                    url = new URL(AppConstants.BaseURL + "/api/device" + "/" + Preferences.getUserId());
                }*/
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            byte[] outputInBytes = mdata.getBytes("UTF-8");
            dStream.write(outputInBytes);
            dStream.flush();
            dStream.close();
            int responseCode = connection.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();

            String res = responseOutput.toString();
            GlobalUtils.writeLogFile("Response of Post Request from server " + res);

            connection.disconnect();

            System.out.println("AcceptThreadtest.run");

        } catch(Exception e)    {
            e.printStackTrace();
            GlobalUtils.writeLogFile("exception " + e.getMessage());

        }
    }
}



