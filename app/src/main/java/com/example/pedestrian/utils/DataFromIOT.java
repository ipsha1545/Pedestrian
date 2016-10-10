
package com.example.pedestrian.utils;

public class DataFromIOT {

    private String DeviceID;
    private String[] Signatures;
    private String TimeStamp;
    private Integer DevicesCount;

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDevice_ID(String deviceID) {
        DeviceID = deviceID;
    }

    public String[] getSignatures() {
        return Signatures;
    }

    public void setSignatures(String[] signatures) {
        Signatures = signatures;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String time_Stamp) {
        TimeStamp = time_Stamp;
    }

    public Integer getDevicesCount() {
        return DevicesCount;
    }

    public void setDevicesCount(Integer devices_Count) {
        DevicesCount = devices_Count;
    }


}
