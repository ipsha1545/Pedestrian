package com.example.pedestrian;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lokesh.gupta on 9/14/2016.
 */
public class Crosswalks {

    public  double lat;
    public  double lng;
    public  LatLng latlng;

    public Crosswalks(double lat, double lng, LatLng latlng){
        this.lat = lat;
        this.lng = lng;
        this.latlng = latlng;
    }

    public  double getLat() {
        return lat;
    }

    public  void setLat(double lat) {
        this.lat = lat;
    }

    public  double getLng() {
        return lng;
    }

    public  void setLng(double lng) {
        this.lng = lng;
    }

    public  LatLng getLatlng() {
        return latlng;
    }

    public  void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

}
