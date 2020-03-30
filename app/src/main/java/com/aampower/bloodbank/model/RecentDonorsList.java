package com.aampower.bloodbank.model;

import java.util.List;

public class RecentDonorsList {

    private boolean error;
    private List<RecentDonor> message;

    public RecentDonorsList(boolean error, List<RecentDonor> message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<RecentDonor> getMessage() {
        return message;
    }

}
