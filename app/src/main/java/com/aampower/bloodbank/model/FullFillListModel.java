package com.aampower.bloodbank.model;

import java.util.ArrayList;
import java.util.List;

public class FullFillListModel {

    private boolean error;
    private List<FulFillModel> message;

    public FullFillListModel(boolean error, ArrayList<FulFillModel> message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<FulFillModel> getFulFillModels() {
        return message;
    }

}
