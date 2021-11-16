package com.appsxone.citisecurity.models;

public class OfflineLocationModel {
    public int id;
    public String userId;
    public String udid;
    public String lat_lng_date_time;

    public OfflineLocationModel(int id, String userId, String udid, String lat_lng_date_time) {
        this.id = id;
        this.userId = userId;
        this.udid = udid;
        this.lat_lng_date_time = lat_lng_date_time;
    }
}