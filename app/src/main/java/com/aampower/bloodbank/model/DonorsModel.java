package com.aampower.bloodbank.model;

public class DonorsModel {

    private String unique_id;
    private String name;
    private String city;
    private String phone_number;
    private String blood_group;
    private String date_of_birth;
    private String last_bleed;
    private String profile_image;
    private String email;

    public DonorsModel(String unique_id, String name, String city, String phone_number, String blood_group, String date_of_birth, String last_bleed, String profile_image, String email) {
        this.unique_id = unique_id;
        this.name = name;
        this.city = city;
        this.phone_number = phone_number;
        this.blood_group = blood_group;
        this.date_of_birth = date_of_birth;
        this.last_bleed = last_bleed;
        this.profile_image = profile_image;
        this.email = email;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getLast_bleed() {
        return last_bleed;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getEmail() {
        return email;
    }
}
