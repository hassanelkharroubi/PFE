package com.example.onmyway.UserInfo;

import java.util.ArrayList;

public class ListGeoPoints {
    private long time;
    private ArrayList<GeoPoint> geoPointArrayList;

    public ListGeoPoints(long time, ArrayList<GeoPoint> geoPointArrayList) {
        this.time = time;
        this.geoPointArrayList = geoPointArrayList;
    }
    public ListGeoPoints()
    {

    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<GeoPoint> getGeoPointArrayList() {
        return geoPointArrayList;
    }

    public void setGeoPointArrayList(ArrayList<GeoPoint> geoPointArrayList) {
        this.geoPointArrayList = geoPointArrayList;
    }
}
