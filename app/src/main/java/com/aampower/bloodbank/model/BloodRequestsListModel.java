package com.aampower.bloodbank.model;

import java.util.List;

public class BloodRequestsListModel {

    private boolean error;
    private List<RequestModel> message;

    public BloodRequestsListModel(boolean error, List<RequestModel> message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<RequestModel> getMessage() {
        return message;
    }
}
