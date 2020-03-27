package com.example.onmyway.UserInfo;

public class UserLocation {

    private GeoPoint geoPoint;
    private User user;

    public UserLocation() {
        //pour fireBase

    }

    public UserLocation(GeoPoint geoPoint, User user) {
        this.geoPoint = geoPoint;
        this.user = user;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
