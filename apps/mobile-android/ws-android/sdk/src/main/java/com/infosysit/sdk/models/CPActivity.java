package com.infosysit.sdk.models;

public class CPActivity {
    private String eid;
    private String ver;
    private String uid;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public long getEts() {
        return ets;
    }

    public void setEts(long ets) {
        this.ets = ets;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getResid() {
        return resid;
    }

    public void setResid(String resid) {
        this.resid = resid;
    }





    private String sid;
    private long ets;
    private String mode;
    private String resid;

    public BodhiUser getBodhiuser() {
        return bodhiuser;
    }

    public void setBodhiuser(BodhiUser bodhiuser) {
        this.bodhiuser = bodhiuser;
    }

    public DeviceData getDevicedata() {
        return devicedata;
    }

    public void setDevicedata(DeviceData devicedata) {
        this.devicedata = devicedata;
    }

    private BodhiUser bodhiuser;
    private DeviceData devicedata;

}

class BodhiUser{


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
}
class DeviceData{
    private String device;
    private int osVersion;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(int osVersion) {
        this.osVersion = osVersion;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private String screenResolution;
    private String deviceName;

    public String getUA() {
        return UA;
    }

    public void setUA(String UA) {
        this.UA = UA;
    }

    private String UA;
}