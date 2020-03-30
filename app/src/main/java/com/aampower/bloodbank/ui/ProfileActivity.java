package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView image_profile;
    TextView tvProfileName, tvCity, tvPMobile, tvPEmail, tvPGender, tvPBloodGroup, tvPDOB, tvPLastBleed;
    Toolbar profileToolBar;
    Activity context;

    String phonNumber = "";
    private Alert dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = ProfileActivity.this;
        super.onCreate(savedInstanceState);

        //region
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = context.getWindow();
//
//            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
//        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {

            Window window = context.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(context, R.color.white));


        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = context.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        //endregion

        setContentView(R.layout.activity_profile);
        image_profile = findViewById(R.id.image_profile);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvCity = findViewById(R.id.tvCity);
        tvPEmail = findViewById(R.id.tvPEmail);
        tvPMobile = findViewById(R.id.tvPMobile);
        tvPGender = findViewById(R.id.tvPGender);
        tvPBloodGroup = findViewById(R.id.tvPBloodGroup);
        tvPDOB = findViewById(R.id.tvPDOB);
        tvPLastBleed = findViewById(R.id.tvPLastBleed);
        profileToolBar = findViewById(R.id.profileToolBar);
        setSupportActionBar(profileToolBar);
        setTitle("Profile");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }

        //region

        String name = "", city = "", blood_group = "", date_of_birth = "", last_bleed = "", profile_image = "", email = "", gender = "";

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        phonNumber = preferences.getString("phoneNumber", "");

        name = preferences.getString("name", "---");
        city = preferences.getString("city", "---");
        blood_group = preferences.getString("blood_group", "---");
        date_of_birth = preferences.getString("date_of_birth", "---");
        last_bleed = preferences.getString("last_bleed", "---");
        profile_image = preferences.getString("profile_image", "---");
        gender = preferences.getString("gender", "---");
        email = preferences.getString("email", "---");

        if (name != null && !name.equals("null")) {
            tvProfileName.setText(name);
        }else {
            tvProfileName.setText("---");
        }
        if (city != null && !city.equals("null")) {
            tvCity.setText(city);
        }else {
            tvCity.setText("---");
        }
        if (blood_group != null && !blood_group.equals("null")) {
            tvPBloodGroup.setText(blood_group);
        }else {
            tvPBloodGroup.setText("---");
        }
        if (date_of_birth != null && !date_of_birth.equals("null")){
            tvPDOB.setText(date_of_birth);
        }else {
            tvPDOB.setText("---");
        }
        if (last_bleed != null && !last_bleed.equals("null")){
            tvPLastBleed.setText(last_bleed);
        }else {
            tvPLastBleed.setText("---");
        }
        if (profile_image != null && !profile_image.equals("null") && !profile_image.equals("")){
            Picasso.get().load(profile_image).resize(512, 512).centerCrop().into(image_profile);
        }
        if (gender != null && !gender.equals("null")){
            tvPGender.setText(gender);
        }else {
            tvPGender.setText("---");
        }
        if (email != null && !email.equals("null")){
            tvPEmail.setText(email);
        }else {
            tvPEmail.setText("---");
        }

        tvPMobile.setText(phonNumber);

        //endregion


        profileTask();


    }

    private void profileTask(){

        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingProfileData(phonNumber);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();


                if (response.code() == Constants.STATUS_OK){


                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        JSONArray jsonArray = jsonObject.getJSONArray("message");

                        final String name = jsonArray.getJSONObject(0).getString("name");
                        String city = jsonArray.getJSONObject(0).getString("city");
                        String blood_group = jsonArray.getJSONObject(0).getString("blood_group");
                        String date_of_birth = jsonArray.getJSONObject(0).getString("date_of_birth");
                        String last_bleed = jsonArray.getJSONObject(0).getString("last_bleed");
                        final String profile_image = jsonArray.getJSONObject(0).getString("profile_image");
                        String gender = jsonArray.getJSONObject(0).getString("gender");
                        String email = jsonArray.getJSONObject(0).getString("email");


                        SharedPreferences.Editor preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                        preferences.putString("name", name);
                        preferences.putString("city", city);
                        preferences.putString("blood_group", blood_group);
                        preferences.putString("date_of_birth", date_of_birth);
                        preferences.putString("last_bleed", last_bleed);
                        preferences.putString("profile_image", profile_image);
                        preferences.putString("gender", gender);
                        preferences.putString("email", email);

                        preferences.apply();

                        if (name != null && !name.equals("null")) {
                            tvProfileName.setText(name);
                        }else {
                            tvProfileName.setText("---");
                        }
                        if (city != null && !city.equals("null")) {
                            tvCity.setText(city);
                        }else {
                            tvCity.setText("---");
                        }
                        if (blood_group != null && !blood_group.equals("null")) {
                            tvPBloodGroup.setText(blood_group);
                        }else {
                            tvPBloodGroup.setText("---");
                        }
                        if (date_of_birth != null && !date_of_birth.equals("null")){
                            tvPDOB.setText(date_of_birth);
                        }else {
                            tvPDOB.setText("---");
                        }
                        if (last_bleed != null && !last_bleed.equals("null")){
                            tvPLastBleed.setText(last_bleed);
                        }else {
                            tvPLastBleed.setText("---");
                        }
                        if (profile_image != null && !profile_image.equals("null")){
                            Picasso.get().load(profile_image).resize(512, 512).centerCrop().into(image_profile);
                        }
                        if (gender != null && !gender.equals("null")){
                            tvPGender.setText(gender);
                        }else {
                            tvPGender.setText("---");
                        }
                        if (email != null && !email.equals("null")){
                            tvPEmail.setText(email);
                        }else {
                            tvPEmail.setText("---");
                        }

                        tvPMobile.setText(phonNumber);

                        image_profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Bundle bundle = new Bundle();
                                bundle.putString("imgURL", profile_image);
                                bundle.putString("accName", name);

                                Intent intent = new Intent(context, FullImageActivity.class);

                                intent.putExtras(bundle);

                                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                        .makeSceneTransitionAnimation(context, image_profile,
                                                ViewCompat.getTransitionName(image_profile));

                                context.startActivity(intent, optionsCompat.toBundle());

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
                dialog.dismiss();


                String message = "Something goes wrong...Please try again later.";
                if (t.getCause() instanceof NetworkErrorException) {
                    message = "Cannot connect to Network...Please check your connection!";
                } else if (t.getCause() instanceof SocketTimeoutException) {
                    message = "Connection TimeOut! Please check your internet connection.";
                } else if (t.getCause() instanceof ConnectException) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (t.getCause() instanceof TimeoutException) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }


                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_out_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.action_logout){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    startActivity(new Intent(context, MainActivity.class));
                    finish();

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

        }else if (item.getItemId() == R.id.action_edit){

            Bundle bundle = new Bundle();
            bundle.putInt("code", 422);

            Intent intent = new Intent(context, PersonalInfoActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);

        }


        return true;
    }

}
