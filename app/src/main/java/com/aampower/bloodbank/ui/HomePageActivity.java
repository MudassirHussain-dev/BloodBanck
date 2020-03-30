package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.adapter.RecentDonorsAdapter;
import com.aampower.bloodbank.adapter.RequestAdapter;
import com.aampower.bloodbank.model.RecentDonor;
import com.aampower.bloodbank.model.RecentDonorsList;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDonorsCount, tvRequestsCount;
    Button btnFindDonors, btnSeeRequests;
    CircleImageView imgProfile;
    RecyclerView recView;
    ImageView btnNote;

    List<RecentDonor> donorsList;
    List<RecentDonor> dupList;

    RecentDonorsAdapter adapter;

    Toolbar homeToolBar;

    Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = HomePageActivity.this;
        super.onCreate(savedInstanceState);

        StatusBarUtil.setTransparent(context);

        //region
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                w.setNavigationBarColor(getResources().getColor(R.color.black));
//            }
//        }

        //endregion

        setContentView(R.layout.activity_home_page);
        btnFindDonors = findViewById(R.id.btnFindDonors);
        btnSeeRequests = findViewById(R.id.btnSeeRequests);
        tvDonorsCount = findViewById(R.id.tvDonorsCount);
        tvRequestsCount = findViewById(R.id.tvRequestsCount);
        imgProfile = findViewById(R.id.imgProfile);
        recView = findViewById(R.id.recView);
        btnNote = findViewById(R.id.btnNote);

        homeToolBar = findViewById(R.id.homeToolBar);
        setSupportActionBar(homeToolBar);


        recView.setLayoutManager(new LinearLayoutManager(this));


        btnFindDonors.setOnClickListener(this);
        btnSeeRequests.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnNote.setOnClickListener(this);


        gettingLastTenDonors();


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnFindDonors){

            startActivity(new Intent(context, DonorsListActivity.class));

        }else if (id == R.id.btnSeeRequests){

            startActivity(new Intent(context, BloodRequestActivity.class));

        }else if (id == R.id.imgProfile){

            startActivity(new Intent(context, ProfileActivity.class));

        }else if (id == R.id.btnNote){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Note");
            builder.setMessage("- We are not selling or purchasing any type of blood.\n" +
                    "\n" +
                    "- We do not store any blood.\n" +
                    "\n" +
                    "- We just create a medium between patient and blood donor to communicate with each other for blood donation.\n" +
                    "\n" +
                    "- Patient must perform all tests before taking any blood by himself.");

            builder.create().show();

        }

    }

    @Override
    protected void onResume() {

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

        Picasso.get()
                .load(preferences.getString("user_profile", Constants.default_profile))
                .resize(100, 100).centerCrop().into(imgProfile, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                imgProfile.setImageResource(R.drawable.placeholder);
            }
        });

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingTotalDonReq(preferences.getString("phoneNumber", ""));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == Constants.STATUS_OK){

                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        JSONArray jsonArray = jsonObject.getJSONArray("message");

                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        tvDonorsCount.setText(jsonObject1.getString("donor_count"));
                        tvRequestsCount.setText(jsonObject1.getString("request_count"));

                        SharedPreferences.Editor preferences1 = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                        preferences1.putString("user_profile", jsonObject1.getString("user_profile"));
                        preferences1.apply();

                        Picasso.get()
                                .load(jsonObject1.getString("user_profile"))
                                .centerCrop()
                                .resize(100, 100)
                                .into(imgProfile, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        imgProfile.setImageResource(R.drawable.placeholder);
                                    }
                                });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        gettingLastTenDonors();

        super.onResume();
    }


    private void gettingLastTenDonors(){

        Call<RecentDonorsList> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingLastTenDonors();

        call.enqueue(new Callback<RecentDonorsList>() {
            @Override
            public void onResponse(Call<RecentDonorsList> call, Response<RecentDonorsList> response) {

                if (response.code() == Constants.STATUS_OK){

                    donorsList = response.body().getMessage();

                    if (donorsList != null && donorsList.size() > 0) {

                        adapter = new RecentDonorsAdapter(context, donorsList);
                        recView.setAdapter(adapter);

                    }

                }

            }

            @Override
            public void onFailure(Call<RecentDonorsList> call, Throwable t) {

            }
        });

    }


}
