package com.aampower.bloodbank.utils;

import com.aampower.bloodbank.model.BloodRequestsListModel;
import com.aampower.bloodbank.model.CitiesModel;
import com.aampower.bloodbank.model.CityModel;
import com.aampower.bloodbank.model.DonorsListModel;
import com.aampower.bloodbank.model.FullFillListModel;
import com.aampower.bloodbank.model.RecentDonorsList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("sendingOTP")
    Call<ResponseBody> sendingOTP(

            @Field("phone") String phoneNumber,
            @Field("otpCode") String otpCode

    );

    @FormUrlEncoded
    @POST("sendingVerificationOTP")
    Call<ResponseBody> sendingVerificationOTP(

            @Field("phone") String phoneNumber,
            @Field("otpCode") String otpCode

    );

    @FormUrlEncoded
    @POST("createUser")
    Call<ResponseBody> createUser(

            @Field("phone_number") String phoneNumber,
            @Field("password") String password,
            @Field("token") String token

    );

    @FormUrlEncoded
    @POST("updatingPassword")
    Call<ResponseBody> updatingPassword(

            @Field("phone_number") String phoneNumber,
            @Field("password") String password

    );


    @Multipart
    @POST("insertingProfileData")
    Call<ResponseBody> uploadFile(

            @Part MultipartBody.Part file,
            @Part("file") RequestBody filename,
            @Part("name") String name,
            @Part("email") String email,
            @Part("gender") String gender,
            @Part("bloodGroup") String bloodGroup,
            @Part("DOB") String DOB,
            @Part("city") String city,
            @Part("user_id") String user_id,
            @Part("last_bleed") String last_bleed,
            @Part("token") String token

    );

    @FormUrlEncoded
    @POST("insertingProfileDataWithoutFile")
    Call<ResponseBody> insertingProfileDataWithoutFile(

            @Field("name") String name,
            @Field("email") String email,
            @Field("gender") String gender,
            @Field("bloodGroup") String bloodGroup,
            @Field("DOB") String DOB,
            @Field("city") String city,
            @Field("user_id") String user_id,
            @Field("last_bleed") String last_bleed,
            @Field("profile") String profile,
            @Field("token") String token

    );

    @GET("gettingCitiesList")
    Call<CitiesModel> gettingCitiesList();

    @GET("gettingBloodRequests")
    Call<BloodRequestsListModel> gettingBloodRequestsList();

    @GET("gettingBloodRequests2")
    Call<ResponseBody> gettingBloodRequestsList2();

    @GET("gettingDonorsList")
    Call<DonorsListModel> gettingDonorsList();

    @GET("gettingLastTenDonors")
    Call<RecentDonorsList> gettingLastTenDonors();

    @FormUrlEncoded
    @POST("gettingMyAcceptedRequests")
    Call<BloodRequestsListModel> gettingMyAcceptedRequests(

            @Field("user_id") String user_id

    );

    @FormUrlEncoded
    @POST("gettingMyAllRequests")
    Call<BloodRequestsListModel> gettingMyAllRequests(

            @Field("user_id") String user_id

    );

    @FormUrlEncoded
    @POST("sendingNotification")
    Call<ResponseBody> sendingGCM(

            @Field("token") String token,
            @Field("bloodGroup") String bloodGroup,
            @Field("city") String city,
            @Field("userName") String userName,
            @Field("profile") String profile,
            @Field("id") String id,
            @Field("userID") String userID,
            @Field("sender_userID") String sender_userID,
            @Field("phoneNumber") String phoneNumber

    );

    @FormUrlEncoded
    @POST("cancelingRequest")
    Call<ResponseBody> cancelingRequest(

            @Field("req_id") String req_id

    );

    @FormUrlEncoded
    @POST("gettingProfileData")
    Call<ResponseBody> gettingProfileData(

            @Field("phoneNumber") String phoneNumber

    );

    @FormUrlEncoded
    @POST("gettingTotalDonReq")
    Call<ResponseBody> gettingTotalDonReq(

            @Field("phoneNumber") String phoneNumber

    );

    @FormUrlEncoded
    @POST("userLogin")
    Call<ResponseBody> userLogin(

      @Field("phoneNumber") String phoneNumber,
      @Field("password") String password,
      @Field("token") String token

    );


    @FormUrlEncoded
    @POST("insertBloodRequest")
    Call<ResponseBody> insertBloodRequest(

      @Field("user_id") String user_id,
      @Field("blood_request_type") String blood_request_type,
      @Field("blood_group") String blood_group,
      @Field("reason") String reason,
      @Field("latlng") String latlng,
      @Field("city") String city,
      @Field("address") String address,
      @Field("message") String message,
      @Field("phone_number_sec_person") String phone_number_sec_person

    );

    @FormUrlEncoded
    @POST("gettingFulfillReqs")
    Call<FullFillListModel> gettingFulfillReqs(

            @Field("req_id") String req_id

    );


    @FormUrlEncoded
    @POST("settingFulfilledReq")
    Call<ResponseBody> settingFulfilledReq(

            @Field("req_id") String req_id,
            @Field("donor_id") String settingFulfilledReq

    );

}
