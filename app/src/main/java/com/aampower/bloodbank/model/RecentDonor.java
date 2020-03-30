package com.aampower.bloodbank.model;

public class RecentDonor {

    private String unique_id, name, blood_group, profile_image;

    public RecentDonor(String unique_id, String name, String blood_group, String profile_image) {
        this.unique_id = unique_id;
        this.name = name;
        this.blood_group = blood_group;
        this.profile_image = profile_image;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public String getName() {
        return name;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getProfile_image() {
        return profile_image;
    }
}
