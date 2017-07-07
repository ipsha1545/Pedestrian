package com.example.pedestrian;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import com.example.pedestrian.curl.Curl;
import com.example.pedestrian.utils.AppConstants;
import com.example.pedestrian.utils.DataFromIOT;
import com.example.pedestrian.utils.DataToServer;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;
import com.google.gson.Gson;

/**
 * Created by nischal.bansal on 9/16/2016.
 */
public class AcceptThread extends Thread {
    private BluetoothServerSocket mmServerSocket = null;
    private String NAME = "Rsystems";
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final

        try {
            BluetoothServerSocket tmp = null;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                GlobalUtils.writeLogFile("Error in BLE Listening " + e.getMessage());
            }
            mmServerSocket = tmp;
        } catch (Exception e){
            GlobalUtils.writeLogFile("Exception in Accept Thread " + e.getMessage());
        }

    }

    public void run() {
        BluetoothSocket socket = null;

        GlobalUtils.writeLogFile("Thread started");

        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
                System.out.println("AcceptThread.run");
            } catch (IOException e) {
                GlobalUtils.writeLogFile("Error in accept thread socket "+ e.getMessage());
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                InputStream mmInStream = null;
                OutputStream mmOutStream = null;
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()

                try {
                    mmInStream = socket.getInputStream();
                    //mmOutStream = socket.getOutputStream();

                    bytes = mmInStream.read(buffer);

                    String data = new String(buffer);

                    Log.d("Padestrian", data);

                    //data = data.substring(0, data.indexOf('}') + 1);
                    data = data.trim();
                    GlobalUtils.writeLogFile("Data from IOT device: "+data);

                    String res = sendData(data);
                    GlobalUtils.writeLogFile("Response of Post Request from server " + res);

                } catch (IOException e) {
                    GlobalUtils.writeLogFile("Error in Post Thread: " + e.getMessage());
                    Toast.makeText(MapsActivity.mContext, "Exception while sending data" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                }

                //not closing the socket to keep it always listening mode
                /*try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;*/

            }
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
        }
    }

    private String sendData(String data) {
        String res = "";
        try {
            //String fromIOT = "{'DeviceID': 'L100', 'Signatures': '', 'TimeStamp': '28-Sep-2016 08:38:33', 'DevicesCount': 2}";
            Gson gson1 = new Gson();
            DataFromIOT dataFromIOT = gson1.fromJson(data, DataFromIOT.class);

            Preferences.setSettingsParam("datafromiot", data);

            Set<String> linkedHashSet =  Preferences.getStringSet("list");

            if(linkedHashSet == null){
                linkedHashSet = new LinkedHashSet<String>();
            }

            linkedHashSet.add(data);

            Preferences.setStringArray("list", linkedHashSet);


           // Toast.makeText(MapsActivity.mContext, "das", Toast.LENGTH_SHORT).show();

            LocationManager lm = (LocationManager) MapsActivity.mContext.getSystemService(MapsActivity.mContext.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            /*Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();*/

            Location location1 = GlobalUtils.getLocation();
            double longitude1 = location1.getLongitude();
            double latitude1 = location1.getLatitude();


            DataToServer dataToServer = new DataToServer();
            dataToServer.setSubId(GlobalUtils.getDeviceMac());
            dataToServer.setDeviceId(dataFromIOT.getDeviceID());
            dataToServer.setLongitude(longitude1+"");
            dataToServer.setLatitude(latitude1+"");
            dataToServer.setConnectedCount(dataFromIOT.getDevicesCount());
            dataToServer.setTimeStamp(dataFromIOT.getTimeStamp());
            dataToServer.setUserId(Preferences.getUserId());

            Gson gson = new Gson();
            String mdata = gson.toJson(dataToServer);
            Log.d("IOT data to server", mdata);
            GlobalUtils.writeLogFile("IOT data to be sent to the server "+ mdata);

            URL url = null;

            try {
                url = new URL(AppConstants.BaseURL + "/api/device");
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


            GlobalUtils.writeLogFile("responseCode of Post request "+ responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();


            res = responseOutput.toString();
            Log.d("response from server", res);

            connection.disconnect();

            System.out.println("AcceptThreadtest.run");

        } catch (Exception e) {
            e.printStackTrace();
            GlobalUtils.writeLogFile("Error in send method "+ e.getMessage());
        }

        return res;
    }
}