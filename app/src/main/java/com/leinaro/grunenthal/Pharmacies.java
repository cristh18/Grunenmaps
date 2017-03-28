package com.leinaro.grunenthal;

/**
 * Created by Adela on 7/02/2016.
 */
public class Pharmacies {

    private String name;
    private String address;
    private String city;
    private String lat;
    private String lon;
    private String idfranchise;
    private String franchise;
    private String color;

    public Pharmacies(String name, String address, String city, String lat, String lon, String idfranchise, String franchise, String color) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.idfranchise = idfranchise;
        this.franchise = franchise;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLat() {
        return Double.parseDouble(lat);
    }

    public Double getLon() {
        return Double.parseDouble(lon);
    }

    public String getFranchise() {
        return franchise;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
