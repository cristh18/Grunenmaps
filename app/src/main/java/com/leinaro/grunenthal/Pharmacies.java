package com.leinaro.grunenthal;

/**
 * Created by Adela on 7/02/2016.
 */
public class Pharmacies {

    private String name;
    private String address;
    private String city;
    private Double lat;
    private Double lon;
    private String idfranchise;
    private String franchise;
    private String color;

    public Pharmacies(String name, String address, String city, Double lat, Double lon, String idfranchise, String franchise, String color) {
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getIdfranchise() {
        return idfranchise;
    }

    public void setIdfranchise(String idfranchise) {
        this.idfranchise = idfranchise;
    }

    public String getFranchise() {
        return franchise;
    }

    public void setFranchise(String franchise) {
        this.franchise = franchise;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
