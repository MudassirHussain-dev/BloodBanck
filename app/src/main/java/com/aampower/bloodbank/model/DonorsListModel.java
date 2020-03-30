package com.aampower.bloodbank.model;

import java.util.List;

public class DonorsListModel {

    private boolean error;
    private List<DonorsModel> message;

    public DonorsListModel(boolean error, List<DonorsModel> message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<DonorsModel> getMessage() {
        return message;
    }
}
