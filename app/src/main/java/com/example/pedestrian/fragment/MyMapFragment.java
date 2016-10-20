package com.example.pedestrian.fragment;



import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedestrian.Crosswalks;
import com.example.pedestrian.GMapV2Direction;
import com.example.pedestrian.MapsActivity;
import com.example.pedestrian.Myservice;
import com.example.pedestrian.R;
import com.example.pedestrian.curl.Curl;
import com.example.pedestrian.utils.GlobalUtils;
import com.example.pedestrian.utils.Preferences;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by lokesh.gupta on 10/4/2016.
 */
public class MyMapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private EditText autocompleteView, autocompleteView2;
    private Button find;
    String src2 = "", dest2 = "", src = "", dest = "";
    PendingIntent pi;
    ArrayList<LatLng> directionPoint = null;
    String[] srcdest;

    LatLng latLngSrc, latLngDest;
    ArrayList<Crosswalks> crosswalks;
    ArrayList<Crosswalks> crosswalksNew;
    ProgressDialog progressDialog;
    String ACTION_FILTER = "com.example.proximityalert";
    LocationManager lm;
    private GoogleApiClient client;

    private int Location_PERMISSION_CODE = 23;
    private int External_PERMISSION_CODE = 24;
    SupportMapFragment mapFragment;
    private BroadcastReceiver proximityReceiver;

    int radius = 1;
    double sourceLat, sourceLong, destinationLat, destinationLong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps,container, false);

        try {
            autocompleteView = (EditText) view.findViewById(R.id.autocomplete);
            autocompleteView2 = (EditText) view.findViewById(R.id.autocomplete2);
            find = (Button) view.findViewById(R.id.btn_signup);

            if (!Preferences.getSettingsParam("source").equals("")) {
                autocompleteView.setText(Preferences.getSettingsParam("source"));
            }

            if (!Preferences.getSettingsParam("destination").equals("")) {
                autocompleteView2.setText(Preferences.getSettingsParam("destination"));
            }

            crosswalks = new ArrayList<Crosswalks>();

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();

            proximityReceiver = new ProximityReciever();
            getActivity().registerReceiver(proximityReceiver, new IntentFilter(ACTION_FILTER));


            find.setOnClickListener(this);

            findRoute();
        } catch (Exception e){
            GlobalUtils.writeLogFile("Error in Map Fragment " + e.getMessage());
        }

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void findRoute() {

        try {
            GlobalUtils.writeLogFile("Find route Button clicked");
            if (autocompleteView.getText().toString().equals("")) {
                autocompleteView.setError("This cant be empty");
                return;
            } else {
                src2 = autocompleteView.getText().toString();
                Preferences.setSettingsParam("source", src2);
            }

            if (autocompleteView2.getText().toString().equals("")) {
                autocompleteView2.setError("This cant be empty");
                return;
            } else {
                dest2 = autocompleteView2.getText().toString();
                Preferences.setSettingsParam("destination", dest2);
            }

            if (GlobalUtils.isLocationAllowed() && GlobalUtils.isExternalStorageAllowed()) {

                src = src2;  // get data from intent
                dest = dest2;// get data from intent
                radius = 20;

            /*if (radius == 0) {
              //  Toast.makeText(getActivity(), "Radius cant be 0, minimum value is 1", Toast.LENGTH_SHORT).show();
                //finish();
            }*/

                srcdest = new String[2];
                srcdest[0] = src;
                srcdest[1] = dest;


                //Parse Crosswalk file and feed data.
                crosswalks = GlobalUtils.getCrossWalkPoints();

                // for proximity
                //getActivity().registerReceiver(proximityReceiver, new IntentFilter(ACTION_FILTER));
                lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

                try {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                } catch (Exception e) {
                    // finish();
                }

                //Setting up My Broadcast Intent
                Intent i = new Intent(ACTION_FILTER);
                pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(), -1, i, 0);

                //setting up proximituMethod
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
                mapFragment.getMapAsync(this);

                return;
            } else if (GlobalUtils.isLocationAllowed() == false && GlobalUtils.isExternalStorageAllowed() == true) {
                requestLocationPermission();
            } else if (GlobalUtils.isExternalStorageAllowed() == false) {
                requestStoragePermission();
            }
        }catch (Exception e){
            GlobalUtils.writeLogFile("Exception in find Route " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            if (progressDialog != null)
                progressDialog.dismiss();
        }catch (Exception e){
            GlobalUtils.writeLogFile("Exception in onPause " + e.getMessage());
        }

    }

    private void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_PERMISSION_CODE);
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, External_PERMISSION_CODE);
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();


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
        } catch (Exception e){
            GlobalUtils.writeLogFile("Error in onlocation change listener " + e.getMessage());
        }

        // Reroute
        /*LatLng point = new LatLng(latitude, longitude);
        if (directionPoint != null && GlobalUtils.isNetworkAvailable()) {

            if (directionPoint.size() > 0 && !PolyUtil.isLocationOnPath(point, directionPoint, true, 100)) {



                MapsTask mapsTask = new MapsTask();
                srcdest[0] = latitude + ":#:" + longitude;
                srcdest[1] = dest;

                if (GlobalUtils.isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "Rerouting Done - As you are not between source and destination", Toast.LENGTH_SHORT).show();
                    mapsTask.execute(srcdest);
                } else {
                    Toast.makeText(getActivity(), "Internet not available, Please connect", Toast.LENGTH_SHORT).show();
                   // finish();
                }
            } else{
                Toast.makeText(getActivity(), "you are on track", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getActivity(), "direct point null or network not available", Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            client.connect();
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
            AppIndex.AppIndexApi.start(client, viewAction);
        } catch (Exception e){
            GlobalUtils.writeLogFile("Exception in Proximity " + e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            // ATTENTION: This was auto-generated to implement the App Indexing API.
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
            client.disconnect();
        } catch (Exception e){
            GlobalUtils.writeLogFile("Exception in onStop " + e.getMessage());
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            MapsTask mapsTask = new MapsTask();

            if (GlobalUtils.isNetworkAvailable()) {
                mapsTask.execute(srcdest);
                // Add a marker in Sydney and move the camera
            } else {
                // Toast.makeText(getActivity(), "Internet not available, Please connect", Toast.LENGTH_SHORT).show();
                //finish();
            }
        } catch (Exception e){
            GlobalUtils.writeLogFile("Error onMaoReady " + e.getMessage());
        }
    }

    private GoogleMap mMap;

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btn_signup){
            findRoute();

            try {
                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
            }
        }
    }

    private class ProximityReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                // Key for determining whether user is leaving or entering
                String key = LocationManager.KEY_PROXIMITY_ENTERING;

                //Gives whether the user is entering or leaving in boolean form
                boolean state = intent.getBooleanExtra(key, false);

                if (state) {
                    showToast(MapsActivity.mContext, "Message !!", "You are near a Crosswalk", R.drawable.crosswalk, R.drawable.blue_alert);
                } else {
                    showToast(MapsActivity.mContext, "Thank you for visiting my Area", "Come back again !!", R.drawable.crosswalk, R.drawable.blue_alert);
                }
            } catch (Exception e){
                GlobalUtils.writeLogFile("Exception in Proximity " + e.getMessage());
            }
        }
    }

    private void addHeatMap() {

        try {
            List<LatLng> list = null;

            int i = 1;

            if(!Preferences.getSettingsParam("duration").equals("")){
                i = Integer.parseInt(Preferences.getSettingsParam("duration"));
            }

            String response = Curl.getInSeparateThread("http://rsin-nisbnvm.india.rsystems.com/DemoAPI/api/device" + "/" + i);

            GlobalUtils.writeLogFile("Response from server for heat map: " + response);

            list = GlobalUtils.readItems(response);
            Toast.makeText(getActivity(), "Total Count: " + list.size(), Toast.LENGTH_SHORT).show();

            // Create a heat map tile provider, passing it the latlngs of the police stations.
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(list).build();

            // Add a tile overlay to the map, using the heat map tile provider.
            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

        } catch (Exception e) {
            GlobalUtils.writeLogFile("Exception in addHeatMap " + e.getMessage());
        }
    }

    private class MapsTask extends AsyncTask<String[], String, ArrayList<LatLng>> {

        private String resp;

        @Override
        protected ArrayList<LatLng> doInBackground(String[]... params) {

            String[] srcdest = params[0];
            String source = srcdest[0];
            String destination = srcdest[1];

            try {
                latLngSrc = GlobalUtils.getLocationFromAddress(source.trim());
                if (latLngSrc != null) {
                    sourceLat = latLngSrc.latitude;
                    sourceLong = latLngSrc.longitude;
                }

                latLngDest = GlobalUtils.getLocationFromAddress(destination.trim());
                if(latLngDest!=null){
                    destinationLat = latLngDest.latitude;
                    destinationLong = latLngDest.longitude;
                }

                /*if (source.contains(":#:")) {

                    ArrayList<Double> array = new ArrayList<Double>(2);
                    StringTokenizer st = new StringTokenizer(source, ":#:");
                    while (st.hasMoreTokens()) {
                        array.add(Double.parseDouble(st.nextToken()));
                    }

                    sourceLat = array.get(0);
                    sourceLong = array.get(1);
                    latLngSrc = new LatLng(sourceLat, sourceLong);

                    //GlobalUtils.writeLogFile("Source latlong " + latLngSrc);

                    latLngDest = GlobalUtils.getLocationFromAddress(destination.trim());
                    destinationLat = latLngDest.latitude;
                    destinationLong = latLngDest.longitude;

                    //GlobalUtils.writeLogFile("Dest latlong " + latLngDest);

                } else {
                    latLngSrc = GlobalUtils.getLocationFromAddress(source.trim());
                    sourceLat = latLngSrc.latitude;
                    sourceLong = latLngSrc.longitude;

                    latLngDest = GlobalUtils.getLocationFromAddress(destination.trim());
                    destinationLat = latLngDest.latitude;
                    destinationLong = latLngDest.longitude;
                }*/
            } catch (Exception e) {
                e.printStackTrace();

                progressDialog.dismiss();


                final String error = e.getMessage();

                GlobalUtils.writeLogFile("Exception in AsyncTask " + e.getMessage());

                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();

                    }
                });*/
            }

            if (latLngSrc != null & latLngDest != null) {
                GMapV2Direction md = new GMapV2Direction();
                Document doc = md.getDocument(latLngSrc, latLngDest, GMapV2Direction.MODE_WALKING);
                directionPoint = md.getDirection(doc);
                return directionPoint;

            } else {
                //finish();
                progressDialog.dismiss();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "Please Wait", "Fetching Map !!!");
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> directionPoint) {

            try {
                if (directionPoint != null) {

                    GlobalUtils.writeLogFile("OnPostExecute start ");

                    mMap.clear();

                    PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }

                    Polyline polylin = mMap.addPolyline(rectLine);

                    String mapZoom = Preferences.getSettingsParam("mapzoom");
                    int mapZoomLevel = 15;

                    if (!mapZoom.equals("")) {
                        mapZoomLevel = Integer.parseInt(mapZoom);
                    }

                    CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLngSrc, mapZoomLevel);
                    mMap.moveCamera(center);
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    mMap.addMarker(new MarkerOptions().position(new LatLng(sourceLat, sourceLong)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(destinationLat, destinationLong)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.crosswalk);

                    crosswalksNew = new ArrayList<>();
                    crosswalksNew = GlobalUtils.filterCrossWalkPoints(crosswalks, directionPoint);

                    // add crosswalk images
                    for (int j = 0; j < crosswalksNew.size(); j++) {

                        mMap.addGroundOverlay(new GroundOverlayOptions().image(icon).positionFromBounds(GlobalUtils.toBounds(crosswalksNew.get(j).getLatlng(), 17)));

                        lm.addProximityAlert(crosswalksNew.get(j).getLat(), crosswalksNew.get(j).getLng(), radius, -1, pi);
                    }

                    //add heat maps
                    addHeatMap();

                    progressDialog.dismiss();

                    GlobalUtils.writeLogFile("OnPostExecute end");


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Cannot find address. Please re-enter the address", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e){
                Toast.makeText(getActivity(), "Number Format Exception", Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Toast.makeText(getActivity(), e.getMessage() , Toast.LENGTH_LONG).show();
                GlobalUtils.writeLogFile("Exception in PostExecute Method " + e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}
