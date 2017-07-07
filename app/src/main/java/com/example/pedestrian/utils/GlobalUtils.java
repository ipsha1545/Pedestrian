package com.example.pedestrian.utils;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.pedestrian.Crosswalks;
import com.example.pedestrian.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by lokesh.gupta on 9/23/2016.
 */
public class GlobalUtils {


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MapsActivity.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static ArrayList<Crosswalks> filterCrossWalkPoints(ArrayList<Crosswalks> allCrossWalkPoints, ArrayList<LatLng> directionPoint){
        ArrayList<Crosswalks> crossWalksOnPath = new ArrayList<Crosswalks>();

        double tolerance = 10; // meters

        for (int i =0; i < allCrossWalkPoints.size(); i++){

            LatLng point = new LatLng(allCrossWalkPoints.get(i).getLat(), allCrossWalkPoints.get(i).getLng());

            if(PolyUtil.isLocationOnPath(point, directionPoint, true, tolerance)){
                Crosswalks crosswalks = new Crosswalks(point.latitude, point.longitude, new LatLng(point.latitude, point.longitude));
                crossWalksOnPath.add(crosswalks);
            }
        }
        return crossWalksOnPath;
    }

    public static boolean isExternalStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    public static boolean isLocationAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(MapsActivity.mContext, Manifest.permission.ACCESS_FINE_LOCATION);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    public static LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(MapsActivity.mContext);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            GlobalUtils.writeLogFile("Get coordinates for: " +  address + " ,lat = "+ location.getLatitude() + " & lng = " + location.getLongitude());

            p1 = new LatLng((double) (location.getLatitude()), (double) (location.getLongitude()));

            return p1;
        } catch (Exception e)
        {
            System.out.println("MainActivity.getLocationFromAddress");
        }
        return null;
    }


    /**
     * Read the data (locations of police stations) from raw resources.
     */
    public static ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = MapsActivity.mContext.getResources().openRawResource(resource);
        @SuppressWarnings("resource")
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

    public static ArrayList<LatLng> readItems(String json) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        /*InputStream inputStream = getResources().openRawResource(resource);
        @SuppressWarnings("resource")
        String json = new Scanner(inputStream).useDelimiter("\\A").next();*/
        JSONArray array = new JSONArray(json);
        GlobalUtils.writeLogFile("LatLong after parsing - to be added in list for heat map");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if(object.getString("Latitude")!="" && object.getString("Longitude") !=""){
                double lat = Double.parseDouble(object.getString("Latitude"));
                double lng = Double.parseDouble(object.getString("Longitude"));

                GlobalUtils.writeLogFile("Lat: "+lat+ ",long: "+lng);
                list.add(new LatLng(lat, lng));
            }

            /*double lat = object.getDouble("Latitude");
            double lng = object.getDouble("Longitude");
            list.add(new LatLng(lat, lng));*/
        }
        return list;
    }


    public static LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    public static String getDeviceMac(){
        WifiManager wifiManager = (WifiManager) MapsActivity.mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    public static ArrayList<Crosswalks> getCrossWalkPoints(){

        ArrayList<Crosswalks> crosswalkses = new ArrayList<Crosswalks>();

        try {
            String path = Environment.getExternalStorageDirectory() + "/crosswalks.txt";
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            @SuppressWarnings("resource")
            String json = new Scanner(fileInputStream).useDelimiter("\\A").next();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                double lat = object.getDouble("lat");
                double lng = object.getDouble("lng");
                LatLng latlng = new LatLng(lat, lng);
                crosswalkses.add(new Crosswalks(lat, lng, latlng));
            }

        }catch (Exception e){
            //Deepak code
            //Toast.makeText(MapsActivity.mContext, "File not found", Toast.LENGTH_SHORT).show();
        }

        return crosswalkses;
    }




    public static void writeLogFile(String content) {
        try {

            if(!Constants.enabledebugFile){
                return;
            }

            File file = new File(Constants.debugFilePath + "pedestrian_log.txt");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("\n");
            bw.append("\n");
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLogFile() {
        try {

            if(!Constants.enabledebugFile){
                return;
            }

            File file = new File(Constants.debugFilePath + "pedestrian_log.txt");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                return;
            }

            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Location getLocation() {

        LocationManager locationManager;
        boolean isGPSEnabled = true;
        boolean isNetworkEnabled = true;
        boolean canGetLocation = true;
        Location location = null; // location
        double latitude; // latitude
        double longitude; // longitude

        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
        long MIN_TIME_BW_UPDATES = 1; // 1 minute


        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
        try {
            locationManager = (LocationManager) MapsActivity.mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            //Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
            } else {
                //MapsActivity.mContext.canGetLocation = true;
                if (isNetworkEnabled) {
                    //location=null;
                    if (ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) MapsActivity.mContext);
                    // Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (isGPSEnabled) {
                    //location=null;
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) MapsActivity.mContext);
                        //Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


}
