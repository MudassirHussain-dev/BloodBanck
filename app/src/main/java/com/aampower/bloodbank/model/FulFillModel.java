package com.aampower.bloodbank.model;

public class FulFillModel {

    private String id, user_id, name, city, phoneNumber, blood_group, profile_image, token, last_bleed;

    public FulFillModel(String id, String user_id, String name, String city, String phoneNumber, String blood_group, String profile_image, String token, String last_bleed) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.blood_group = blood_group;
        this.profile_image = profile_image;
        this.token = token;
        this.last_bleed = last_bleed;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getToken() {
        return token;
    }

    public String getLast_bleed() {
        return last_bleed;
    }
}
