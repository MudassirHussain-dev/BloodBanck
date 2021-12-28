package com.aampower.bloodbank.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

//    private static final String BASE_URL = "http://128.1.0.5/BloodBank/public/";

    private static final String BASE_URL = "https://bloodbank.com.pk/services/BloodBank/public/";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getInstance(){
        if (mInstance == null){
            mInstance = new RetrofitClient();
        }

        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }

}
