package com.example.pedestrian;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.curl.Curl;
import com.example.pedestrian.curl.EndPoints;
import com.example.pedestrian.fragment.DataFromIOTFragment;
import com.example.pedestrian.fragment.MyMapFragment;
import com.example.pedestrian.fragment.SettingsFragment;
import com.example.pedestrian.utils.DataFromIOT;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    LatLng latLngSrc, latLngDest;
    ArrayList<Crosswalks> crosswalks;
    ArrayList<Crosswalks> crosswalksNew;
    ProgressDialog progressDialog;

    public static Context mContext;



    private Button find;
    String src2 = "", dest2 = "", src = "", dest = "";

    EditText autocompleteView, autocompleteView2;

    private int Location_PERMISSION_CODE = 23;
    private int External_PERMISSION_CODE = 24;


    LocationManager lm;
    FragmentManager fragmentManager;

    String[] srcdest;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int DISCOVERABLE_REQUEST_CODE = 2;

    PendingIntent pi;
    ArrayList<LatLng> directionPoint = null;

    int radius = 1;
    double sourceLat, sourceLong, destinationLat, destinationLong;

    String ACTION_FILTER = "com.example.proximityalert";

    //private GoogleApiClient client;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
   // SupportMapFragment mapFragment;
    private FrameLayout container;

    private BroadcastReceiver proximityReceiver;// = new ProximityReciever();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        container = (FrameLayout) findViewById(R.id.container);
        //GlobalUtils.clearLogFile();

        fragmentManager=getSupportFragmentManager();
        FragmentTransaction  fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,new MyMapFragment()).commit();

        GlobalUtils.writeLogFile("*********Application  started*****************");
       // proximityReceiver = new ProximityReciever();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();


        statusCheck();

        mContext = this;

        /*find = (Button) findViewById(R.id.btn_signup);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findRoute();

                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                }
            }
        });*/

       // autocompleteView = (EditText) findViewById(R.id.autocomplete);
      //  autocompleteView2 = (EditText) findViewById(R.id.autocomplete2);

       // crosswalks = new ArrayList<Crosswalks>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /// mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

       // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
       // StrictMode.setThreadPolicy(policy);

       // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //check for bluetooth support
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            GlobalUtils.writeLogFile("BLE does not support");
            Toast.makeText(this, "BLE does not support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //enabling bluetooth
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }

        //enabling discoverability
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST_CODE);
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*mMap = googleMap;
        MapsTask mapsTask = new MapsTask();

        if (GlobalUtils.isNetworkAvailable()) {
            mapsTask.execute(srcdest);
            // Add a marker in Sydney and move the camera
        } else {
            Toast.makeText(MapsActivity.this, "Internet not available, Please connect", Toast.LENGTH_SHORT).show();
            finish();
        }*/
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_CANCELED) {

                    GlobalUtils.writeLogFile("BLE is not enabled on your device");
                    System.out.println("BLE is not enabled on your device");
                    finish();
                    System.exit(0);
                }

            case DISCOVERABLE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    GlobalUtils.writeLogFile("Your device is not discoverable to others");
                    System.out.println("Your device is not discoverable to others");
                    finish();
                    System.exit(0);
                }
        }

        Intent intent = new Intent(this, Myservice.class);
        startService(intent);
    }


    @Override
    public void onLocationChanged(Location newLocation) {

        /*double latitude = newLocation.getLatitude();
        double longitude = newLocation.getLongitude();

        GlobalUtils.writeLogFile("OnLocationChange listener called ");
        GlobalUtils.writeLogFile("Current latitude " + latitude);
        GlobalUtils.writeLogFile("Current longitude " + longitude);

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        Preferences.setSettingsParam("latitude", latitude + "");
        Preferences.setSettingsParam("longitude", longitude + "");

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Reroute
        LatLng point = new LatLng(latitude, longitude);
        if (directionPoint != null && GlobalUtils.isNetworkAvailable()) {
            if (directionPoint.size() > 0 && !PolyUtil.isLocationOnPath(point, directionPoint, true, 100)) {

                MapsTask mapsTask = new MapsTask();
                srcdest[0] = latitude + ":#:" + longitude;
                srcdest[1] = dest;

                if (GlobalUtils.isNetworkAvailable()) {
                    Toast.makeText(MapsActivity.this, "Rerouting Done - As you are not between source and destination", Toast.LENGTH_SHORT).show();
                    mapsTask.execute(srcdest);
                } else {
                    Toast.makeText(MapsActivity.this, "Internet not available, Please connect", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }*/
    }

    @Override
    public void onProviderDisabled(String arg0) {
    }

    @Override
    public void onProviderEnabled(String arg0) {
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    @Override
    public void onStart() {
        super.onStart();

       /* *//**//*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.pedestrian/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        /*// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.pedestrian/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        // for proximity
        try {
            registerReceiver(proximityReceiver, new IntentFilter(ACTION_FILTER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


   /* private class ProximityReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Key for determining whether user is leaving or entering
            String key = LocationManager.KEY_PROXIMITY_ENTERING;

            //Gives whether the user is entering or leaving in boolean form
            boolean state = intent.getBooleanExtra(key, false);

            if (state) {
                Toast toast = Toast.makeText(context, "You are near a cross walk", Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(25);
                toastTV.setBackgroundColor(Color.BLUE);
                toastTV.setTextColor(Color.WHITE);
                toast.show();
            } else {
                //Other custom Notification
                // Log.i("MyTag", "Thank you for visiting my Area,come back again !!");
                Toast toast = Toast.makeText(context, "Thank you for visiting my Area,come back again !!", Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(25);
                toastTV.setTextColor(Color.BLUE);
                toast.show();
            }
        }
    }*/




    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
       // unregisterReceiver(proximityReceiver);
    }

    public void initNavigationDrawer() {



        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {

                    case R.id.mapview:
                        // Toast.makeText(getApplicationContext(), "Register me", Toast.LENGTH_SHORT).show();

                        FragmentTransaction  fragmentTransaction2=fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.container,new MyMapFragment()).commit();

                        drawerLayout.closeDrawers();
                        break;


                    case R.id.datafromiot:

                        /*Gson gson1 = new Gson();
                        DataFromIOT dataFromIOT = gson1.fromJson(Preferences.getSettingsParam("datafromiot"), DataFromIOT.class);

                        if (dataFromIOT != null && dataFromIOT instanceof DataFromIOT)
                            Toast.makeText(getApplicationContext(), dataFromIOT.getTimeStamp() + " /DeviceID: " + dataFromIOT.getDeviceID() + " / CNT: " + dataFromIOT.getDevicesCount(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "No data received from IOT yet", Toast.LENGTH_LONG).show();*/


                        FragmentTransaction  fragmentTransaction3=fragmentManager.beginTransaction();
                        fragmentTransaction3.replace(R.id.container,new DataFromIOTFragment()).commit();
                        drawerLayout.closeDrawers();
                        break;



                    case R.id.upgrade:
                        Toast.makeText(getApplicationContext(), "Upgrade to Subscriber", Toast.LENGTH_SHORT).show();

                        /*FragmentTransaction  fragmentTransaction=fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container,new MyMapFragment()).commit();*/

                        drawerLayout.closeDrawers();
                        break;

                    case R.id.settings:
                        //Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();

                        FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                        fragmentTransaction1.replace(R.id.container,new SettingsFragment()).commit();

                        drawerLayout.closeDrawers();
                        break;

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        tv_email.setText("xyz user");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == Location_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can access the location", Toast.LENGTH_LONG).show();


                src = src2;  // get data from intent
                dest = dest2;// get data from intent
                radius = 20;

                if (radius == 0) {
                    Toast.makeText(MapsActivity.this, "Radius cant be 0, minimum value is 1", Toast.LENGTH_SHORT).show();
                    finish();
                }

                srcdest = new String[2];
                srcdest[0] = src;
                srcdest[1] = dest;


                //Parse Crosswalk file and feed data.
                crosswalks = GlobalUtils.getCrossWalkPoints();

                // for proximity
                registerReceiver(proximityReceiver, new IntentFilter(ACTION_FILTER));
                lm = (LocationManager) getSystemService(LOCATION_SERVICE);

                try {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                } catch (Exception e) {
                    finish();
                }

                //Setting up My Broadcast Intent
                Intent i = new Intent(ACTION_FILTER);
                pi = PendingIntent.getBroadcast(getApplicationContext(), -1, i, 0);

                //setting up proximituMethod
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
                //mapFragment.getMapAsync(this);
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == External_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (GlobalUtils.isLocationAllowed() == true) {

                    //Displaying a toast
                    Toast.makeText(this, "Permission granted now you can access the location", Toast.LENGTH_LONG).show();

                    src = src2;  // get data from intent
                    dest = dest2;// get data from intent
                    radius = 20;

                    if (radius == 0) {
                        Toast.makeText(MapsActivity.this, "Radius cant be 0, minimum value is 1", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    srcdest = new String[2];
                    srcdest[0] = src;
                    srcdest[1] = dest;


                    //Parse Crosswalk file and feed data.
                    crosswalks = GlobalUtils.getCrossWalkPoints();

                    // for proximity
                    registerReceiver(proximityReceiver, new IntentFilter(ACTION_FILTER));
                    lm = (LocationManager) getSystemService(LOCATION_SERVICE);

                    try {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                    } catch (Exception e) {
                        finish();
                    }

                    //Setting up My Broadcast Intent
                    Intent i = new Intent(ACTION_FILTER);
                    pi = PendingIntent.getBroadcast(getApplicationContext(), -1, i, 0);

                    //setting up proximituMethod
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
                    //mapFragment.getMapAsync(this);
                } else {
                    //requestLocationPermission();
                }
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}






