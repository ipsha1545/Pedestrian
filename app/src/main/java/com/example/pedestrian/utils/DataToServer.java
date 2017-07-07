
package com.example.pedestrian.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataToServer {

    @SerializedName("SubId")
    @Expose
    private String subId;
    @SerializedName("DeviceId")
    @Expose
    private String deviceId;
    @SerializedName("Longitude")
    @Expose
    private String longitude;
    @SerializedName("Latitude")
    @Expose
    private String latitude;
    @SerializedName("ConnectedCount")
    @Expose
    private Integer connectedCount;
    @SerializedName("TimeStamp")
    @Expose
    private String timeStamp;


    @SerializedName("userId")
    @Expose
    private int userId;

    /**
     *
     * @return
     *     The subId
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     *
     * @param timeStamp
     *     The SubId
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * 
     * @return
     *     The subId
     */
    public String getSubId() {
        return subId;
    }

    /**
     * 
     * @param subId
     *     The SubId
     */
    public void setSubId(String subId) {
        this.subId = subId;
    }

    /**
     * 
     * @return
     *     The deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 
     * @param deviceId
     *     The DeviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The Longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The Latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The connectedCount
     */
    public Integer getConnectedCount() {
        return connectedCount;
    }

    /**
     * 
     * @param connectedCount
     *     The ConnectedCount
     */
    public void setConnectedCount(Integer connectedCount) {
        this.connectedCount = connectedCount;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
