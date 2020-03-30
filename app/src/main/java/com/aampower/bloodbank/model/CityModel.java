package com.aampower.bloodbank.model;

import com.google.gson.annotations.SerializedName;

public class CityModel {

    @SerializedName("id")
    private int id;

    private String cityID;

    @SerializedName("city")
    private String cityName;


    public CityModel() {
    }

    public CityModel(int id, String cityName) {
        this.id = id;
        this.cityName = cityName;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
