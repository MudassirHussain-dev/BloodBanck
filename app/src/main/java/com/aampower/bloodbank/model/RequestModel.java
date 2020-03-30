package com.aampower.bloodbank.model;

public class RequestModel {


    private String id;
    private String user_id;
    private String blood_request_type;
    private String blood_group;
    private String reason;
    private String latlng;
    private String city;
    private String message;
    private String phone_number_sec_person;
    private String profile_image;
    private String name;
    private String address;
    private String token;
    private String status;
    private String donor_id;

    public RequestModel(String id, String user_id, String blood_request_type, String blood_group, String reason, String latlng, String city, String message, String phone_number_sec_person, String profile_image, String name, String address, String token, String status, String donor_id) {
        this.id = id;
        this.user_id = user_id;
        this.blood_request_type = blood_request_type;
        this.blood_group = blood_group;
        this.reason = reason;
        this.latlng = latlng;
        this.city = city;
        this.message = message;
        this.phone_number_sec_person = phone_number_sec_person;
        this.profile_image = profile_image;
        this.name = name;
        this.address = address;
        this.token = token;
        this.status = status;
        this.donor_id = donor_id;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getBlood_request_type() {
        return blood_request_type;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getReason() {
        return reason;
    }

    public String getLatlng() {
        return latlng;
    }

    public String getCity() {
        return city;
    }

    public String getMessage() {
        return message;
    }

    public String getPhone_number_sec_person() {
        return phone_number_sec_person;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return status;
    }

    public String getDonor_id() {
        return donor_id;
    }


    //    private String id;
//    private String user_id;
//    private String name;
//    private String blood_request_type;
//    private String blood_group;
//    private String reason;
//    private String latlng;
//    private String city;
//    private String message;
//    private String phone_number_sec_person;
//    private String profile_image;
//    private String address;
//    private String token;
//    private String status;
//    private String donor_id;
//
//
//    public RequestModel(String id, String user_id, String name, String blood_request_type, String blood_group, String reason, String latlng, String city, String message, String phone_number_sec_person, String profile_image, String address, String token, String status, String donor_id) {
//        this.id = id;
//        this.user_id = user_id;
//        this.name = name;
//        this.blood_request_type = blood_request_type;
//        this.blood_group = blood_group;
//        this.reason = reason;
//        this.latlng = latlng;
//        this.city = city;
//        this.message = message;
//        this.phone_number_sec_person = phone_number_sec_person;
//        this.profile_image = profile_image;
//        this.address = address;
//        this.token = token;
//        this.status = status;
//        this.donor_id = donor_id;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getUser_id() {
//        return user_id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getBlood_request_type() {
//        return blood_request_type;
//    }
//
//    public String getBlood_group() {
//        return blood_group;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//
//    public String getLatlng() {
//        return latlng;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public String getPhone_number_sec_person() {
//        return phone_number_sec_person;
//    }
//
//    public String getProfile_image() {
//        return profile_image;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public String getDonor_id() {
//        return donor_id;
//    }

}
