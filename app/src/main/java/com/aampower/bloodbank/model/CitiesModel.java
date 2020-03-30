package com.aampower.bloodbank.model;

import java.util.ArrayList;
import java.util.List;

public class CitiesModel {

    private boolean error;
    private List<CityModel> message;

    public CitiesModel(boolean error, ArrayList<CityModel> message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<CityModel> getCityModels() {
        return message;
    }
}
