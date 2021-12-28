package com.aampower.bloodbank.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.ui.fragments.CityDialogFrag;
import com.aampower.bloodbank.ui.fragments.SelectCityFragment;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Api;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.aampower.bloodbank.utils.SimpleOTPGenerator;
import com.aampower.bloodbank.utils.SmsListener;
import com.chaos.view.PinView;
import com.facebook.share.Share;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView profile_image;
    TextView tvProfilePic;
    Button btnFinish, btnNext;
    LinearLayout infoLayout, passLayout;
    EditText etPass, etCPass;
    TextInputLayout tiPass, tiCPass;
    TextView tvForgotPass;

    String profile = "";

    EditText etCity, etName, etEmail, etBloodGroup, etDOB, etGender, etLastBleed;
    Toolbar personalVeriToolBar;

    String phoneNumber;
    int code;

    int TAKE_PHOTO_CODE = 12345;
    public static int count = 0;
    int LOCATION_REQUEST_CODE = 10111;
    String dir;
    Uri imageUri;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    Alert dialog;

    File thumbFile1;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = PersonalInfoActivity.this;

        //region

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

        setContentView(R.layout.activity_personal_info);

        // maping

        //region
        etCity = findViewById(R.id.etCity);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etBloodGroup = findViewById(R.id.etBloodGroup);
        etDOB = findViewById(R.id.etDOB);
        profile_image = findViewById(R.id.profile_image);
        tvProfilePic = findViewById(R.id.tvProfilePic);
        btnFinish = findViewById(R.id.btnFinish);
        etGender = findViewById(R.id.etGender);
        infoLayout = findViewById(R.id.infoLayout);
        passLayout = findViewById(R.id.passLayout);
        btnNext = findViewById(R.id.btnNext);
        etPass = findViewById(R.id.etPass);
        etCPass = findViewById(R.id.etCPass);
        tiPass = findViewById(R.id.tiPass);
        tiCPass = findViewById(R.id.tiCPass);
        etLastBleed = findViewById(R.id.etLastBleed);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        //endregion

        infoLayout.setVisibility(View.GONE);
        passLayout.setVisibility(View.VISIBLE);

        personalVeriToolBar = findViewById(R.id.personalVeriToolBar);
        setSupportActionBar(personalVeriToolBar);

        if (getSupportActionBar() != null) {

            setTitle("Personal Information");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
//            backArrow.setColorFilter(getResources().getColor(R.color.md_grey_900), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }


        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();

            code = bundle.getInt("code");

            if (code == Constants.STATUS_OK) {

                personalVeriToolBar.setTitle("Enter Password");

                infoLayout.setVisibility(View.GONE);
                passLayout.setVisibility(View.VISIBLE);

            } else if (code == 423) {

                personalVeriToolBar.setTitle("Enter Password");

                infoLayout.setVisibility(View.GONE);
                passLayout.setVisibility(View.VISIBLE);
                etCPass.setVisibility(View.GONE);
                tiCPass.setVisibility(View.GONE);

                tvForgotPass.setVisibility(View.VISIBLE);

            } else if (code == 422) {

                personalVeriToolBar.setTitle("Update Profile");

                infoLayout.setVisibility(View.VISIBLE);
                passLayout.setVisibility(View.GONE);

                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

                final String name = preferences.getString("name", "");
                String city = preferences.getString("city", "");
                String blood_group = preferences.getString("blood_group", "");
                String date_of_birth = preferences.getString("date_of_birth", "");
                String last_bleed = preferences.getString("last_bleed", "");
                String gender = preferences.getString("gender", "");
                String email = preferences.getString("email", "");


                if (name != null && !name.equals("null")) {
                    etName.setText(name);
                }
                if (city != null && !city.equals("nul                                                                                                                                                                                                                              l")) {
                    etCity.setText(city);
                }
                if (blood_group != null && !blood_group.equals("null")) {
                    etBloodGroup.setText(blood_group);
                }
                if (date_of_birth != null && !date_of_birth.equals("null")) {
                    etDOB.setText(date_of_birth);
                }
                if (last_bleed != null && !last_bleed.equals("null")) {
                    etLastBleed.setText(last_bleed);
                }

                if (gender != null && !gender.equals("null")) {
                    etGender.setText(gender);
                }
                if (email != null && !email.equals("null")) {
                    etEmail.setText(email);
                }


                profile = preferences.getString("profile_image", "");

                if (!profile.equals("null") && !profile.equals("")) {

                    Picasso.get().load(profile).resize(512, 512).centerCrop().into(profile_image);

                }

            }

        }

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString("phoneNumber", "");

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/bloodgroup/";
        File newdir = new File(dir);
        if (!newdir.exists()) {
            newdir.mkdir();
        }


        etCity.setOnClickListener(this);
        etBloodGroup.setOnClickListener(this);
        etDOB.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        tvProfilePic.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        etGender.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvForgotPass.setOnClickListener(this);
        etLastBleed.setOnClickListener(this);

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private static final int CAMERA_REQUEST = 1888; // field

    private void takePicture() { //you can call this every 5 seconds using a timer or whenever you want
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.etCity) {

            final SelectCityFragment dialogFragment = cityFragInstance(1);
            dialogFragment.setCityCallback(new SelectCityFragment.CityCallback() {
                @Override
                public void onCityActionClick(String cityName) {

                    etCity.setText(cityName);
                    etCity.setError(null);

                    hideKeyboard(context);

                    dialogFragment.dismiss();
                }
            });
            if (getFragmentManager() != null) {
                dialogFragment.show(getSupportFragmentManager(), "tag1");
            }

        } else if (id == R.id.etBloodGroup) {

            choosingBloodGroup();

        } else if (id == R.id.etDOB) {

            choosingDOB(etDOB);

        } else if (id == R.id.profile_image) {
            // checking permissions
            if (!hasPermissions(this, PERMISSIONS)) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_REQUEST_CODE);

            } else {

                takingPhotoAlert();

            }
        } else if (id == R.id.tvProfilePic) {
            profile_image.performClick();
        } else if (id == R.id.btnFinish) {

            final String name = etName.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            final String gender = etGender.getText().toString().trim();
            final String bloodGroup = etBloodGroup.getText().toString().trim();
            final String DOB = etDOB.getText().toString().trim();
            final String city = etCity.getText().toString().trim();
            final String last_bleed = etLastBleed.getText().toString().trim();

//            if (thumbFile1 == null) {
//                Toast.makeText(context, "Please take profile image", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (name.isEmpty()) {
                etName.setError("Required");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Required");
                return;
            }
            if (gender.isEmpty()) {
                etGender.setError("Required");
                return;
            }
            if (bloodGroup.isEmpty()) {
                etBloodGroup.setError("Required");
                return;
            }
            if (DOB.isEmpty()) {
                etDOB.setError("Required");
                return;
            }
            if (city.isEmpty()) {
                etCity.setError("Required");
                return;
            }

            dialog = new Alert(context);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(context, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();

                    uploadFile(name, email, gender, bloodGroup, DOB, city, last_bleed, profile, newToken);

                }

            }).addOnFailureListener(context, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(context, "Something goes wrong please again later", Toast.LENGTH_SHORT).show();

                }
            });


        } else if (id == R.id.etGender) {

            choosingGender();

        } else if (id == R.id.btnNext) {

            final String pass = etPass.getText().toString().trim();
            String cPass = etCPass.getText().toString().trim();

            if (code == 423) {

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(context, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();

                        loginTask(phoneNumber, pass, newToken);

                    }

                }).addOnFailureListener(context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, "Something goes wrong please again later", Toast.LENGTH_SHORT).show();

                    }
                });

            } else if (code == Constants.UPDATE_PASS) {

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(context, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();

                        updatingPassword(phoneNumber, pass, newToken);

                    }

                }).addOnFailureListener(context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, "Something goes wrong please again later", Toast.LENGTH_SHORT).show();

                    }
                });

            } else {

                if (pass.equals(cPass)) {

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(context, new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();

                            creatingAccount(phoneNumber, pass, newToken);

                        }

                    }).addOnFailureListener(context, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, "Something goes wrong please again later", Toast.LENGTH_SHORT).show();

                        }
                    });


                } else {
                    etCPass.setError("Password not match");
                }
            }

        } else if (id == R.id.tvForgotPass) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);

            builder.setTitle("Confirmation");
            builder.setMessage("We will send an OTP code to this number \n\n" + phoneNumber + "\n\nto verify your phone number.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendingOTPTask();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();

        } else if (id == R.id.etLastBleed) {

            choosingDOB(etLastBleed);

        }

    }

    private void sendingOTPTask() {

        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();


        String otpCode = SimpleOTPGenerator.random(4);

        SharedPreferences.Editor preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
        preferences.putString("otpCode", otpCode);
        preferences.apply();
        preferences.commit();


        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .sendingVerificationOTP(phoneNumber, otpCode);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();

                try {

                    String s = null;

                    if (response.code() == Constants.STATUS_OK) {

                        pinAlertView();

                    } else if (response.code() == 423) {

                        s = response.errorBody().string();

                    } else {
                        s = response.errorBody().string();
                    }

                    if (s != null) {
                        JSONObject jsonObject = new JSONObject(s);

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void pinAlertView() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.otp_alert_view, null);

        Button btnOK = view.findViewById(R.id.btnOK);
        final TextView btnResend = view.findViewById(R.id.btnResend);
        final PinView pinview = view.findViewById(R.id.pinview);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                btnResend.setText("00:" + String.valueOf(millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                btnResend.setText("Re-send");
                btnResend.setEnabled(true);
            }

        }.start();


        builder.setView(view);

        final Dialog alertDialog = builder.create();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                if (pinview.getText().toString().trim().length() > 0) {
                    String storedOTP = preferences.getString("otpCode", "");

                    if (pinview.getText().toString().trim().equals(storedOTP)) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("phoneNumber", phoneNumber);
                        editor.apply();

                        infoLayout.setVisibility(View.GONE);
                        passLayout.setVisibility(View.VISIBLE);

                        tiCPass.setVisibility(View.VISIBLE);
                        etCPass.setVisibility(View.VISIBLE);

                        tvForgotPass.setVisibility(View.GONE);

                        code = Constants.UPDATE_PASS;

                        alertDialog.dismiss();

                    } else {
                        Toast.makeText(context, "OTP not match", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

                if (phoneNumber.substring(0, 3).equals("+92")) {
                    phoneNumber = phoneNumber.replace("+92", "0");
                } else if (!String.valueOf(phoneNumber.charAt(0)).equals("0")) {
                    phoneNumber = "0" + phoneNumber;
                }

                sendingOTPTask();

            }
        });

        alertDialog.show();

    }

    private void updatingPassword(final String phoneNumber, final String pass, final String token) {

        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .updatingPassword(phoneNumber, pass);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();

                try {

                    String s = null;

                    if (response.code() == Constants.STATUS_OK) {

                        loginTask(phoneNumber, pass, token);

                    } else {
                        s = response.body().string();
                    }

                    if (s != null) {
                        JSONObject jsonObject = new JSONObject(s);

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void loginTask(String phoneNumber, final String pass, String token) {

        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .userLogin(phoneNumber, pass, token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();

                try {

                    String s = null;

                    if (response.code() == Constants.STATUS_OK) {
                        String serverResponse = response.body().string();

                        JSONObject jsonObject = new JSONObject(serverResponse);

                        int isCompleted = jsonObject.getInt("isCompleted");

                        if (isCompleted == 1) {

                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                            editor.putString("password", pass);
                            editor.putString("user_id", jsonObject.getString("user_id"));
                            editor.apply();

                            startActivity(new Intent(context, HomePageActivity.class));
                            finish();

                        } else if (isCompleted == 0) {

                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                            editor.putString("user_id", jsonObject.getString("user_id"));
                            editor.apply();

                            personalVeriToolBar.setTitle("Personal Information");

                            infoLayout.setVisibility(View.VISIBLE);
                            passLayout.setVisibility(View.GONE);
                        }


                    } else {
                        s = response.body().string();
                    }

                    if (s != null) {
                        JSONObject jsonObject = new JSONObject(s);

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void creatingAccount(String phoneNumber, final String pass, String token) {


        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .createUser(phoneNumber, pass, token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();

                try {

                    String s = null;

                    if (response.code() == Constants.USER_CREATED) {
//                        s = response.body().string();

                        personalVeriToolBar.setTitle("Personal Information");

                        infoLayout.setVisibility(View.VISIBLE);
                        passLayout.setVisibility(View.GONE);

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        SharedPreferences.Editor preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
//                        preferences.putString("password", pass);
                        preferences.putString("user_id", jsonObject.getString("user_id"));
                        preferences.apply();


//                        JSONObject parentObject = new JSONObject(s);
//
//                        boolean error = parentObject.getBoolean("error");
//
//                        if (!error) {
//
//                            if (!parentObject.getString("message").isEmpty()) {
//
//                                Toast.makeText(PhoneVeriActivity.this, parentObject.getString("message"), Toast.LENGTH_SHORT).show();
//
////                                String[] a = parentObject.getString("message").split(":");
////                                String status = a[0];
////                                String message = a[1];
////
////                                if (status.equals("OK ")) {
////
////                                    SmsReceiver.bindListener(new SmsListener() {
////                                        @Override
////                                        public void messageReceived(String messageText) {
////
////
////                                            SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
////
////                                            if (messageText.equals(preferences.getString("otpCode", ""))) {
////
////                                                Toast.makeText(PhoneVeriActivity.this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
////
////                                                dialog.dismiss();
////
////                                                startActivity(new Intent(context, PersonalInfoActivity.class));
////
////                                            }
////
////                                        }
////                                    });
////
////                                    pinAlertView();
////
////
////                                }
//                            }
//
//                        } else {
//
//
//                            Toast.makeText(PhoneVeriActivity.this, parentObject.getString("message"), Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        s = response.errorBody().string();
                    }

                    if (s != null) {
                        JSONObject jsonObject = new JSONObject(s);

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void choosingFromGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);

    }

    private void takingPhoto() {

        //region

        File filee = new File(dir);
        File childfile[] = filee.listFiles();

        count = childfile.length;
        count++;
        String file = dir + count + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();

        } catch (IOException e) {
//                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(context, "com.aampower.bloodbank.provider", newfile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);


        } else {
            imageUri = Uri.fromFile(newfile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

        }

        //endregion

    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static SelectCityFragment cityFragInstance(int id) {

        SelectCityFragment cityFragment = new SelectCityFragment().newInstance();

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        cityFragment.setArguments(bundle);

        return cityFragment;

    }

    private void choosingBloodGroup() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Blood Group");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("A+");
        arrayAdapter.add("A-");
        arrayAdapter.add("B+");
        arrayAdapter.add("B-");
        arrayAdapter.add("AB+");
        arrayAdapter.add("AB-");
        arrayAdapter.add("O+");
        arrayAdapter.add("O-");

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                etBloodGroup.setText(strName);

                etBloodGroup.setError(null);

            }
        });
        builderSingle.show();

    }

    private void choosingGender() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Blood Group");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Male");
        arrayAdapter.add("Female");

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                etGender.setText(strName);
                etGender.setError(null);

            }
        });
        builderSingle.show();

    }

    private void choosingDOB(final EditText editText) {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog StartTime = new DatePickerDialog(this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                editText.setError(null);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        StartTime.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int img_width = Integer.valueOf(getString(R.string.img_width));
        int img_height = Integer.valueOf(getString(R.string.img_height));
        int img_quality = Integer.valueOf(getString(R.string.img_quality));

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Bitmap imageBitmap = null;

                try {
                    if (result.getUri() != null) {

                        imageUri = result.getUri();

                        if (MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri) != null) {

                            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                            Uri uri = rotateImageIfRequired(context,
                                    imageBitmap,
                                    imageUri);

                            if (imageBitmap != null) {


//                            if (getFilePathFromURI(context, uri) != null){
//                                file1 = new File(getFilePathFromURI(context, uri));

                                thumbFile1 = new Compressor(context)
                                        .setMaxWidth(img_width)
                                        .setMaxHeight(img_height)
                                        .setQuality(img_quality)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .compressToFile(new File(getFilePathFromURI(context, uri)));


                                Picasso.get().load(uri).resize(512, 512).centerCrop().into(profile_image);


                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public Uri rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap b = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
        return getImageUri(context, b);
    }

    private Uri rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws
            IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return selectedImage;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {

            File rootDataDir = context.getFilesDir();
            File copyFile = new File(rootDataDir + File.separator + fileName + ".jpg");

            //File copyFile = new File(copyFile + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.length > 0) {

                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            takingPhotoAlert();

                        } else {

                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    } else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        } else {

                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }

                }
            }

        }

    }

    private void takingPhotoAlert() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Picture taking options:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Take photo");
//        arrayAdapter.add("Choose from gallery");
        arrayAdapter.add("Remove profile image");

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
//                    takingPhoto();

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(context);

                } else if (which == 1) {
//                    choosingFromGallery();

                    imageUri = null;
                    profile_image.setImageResource(R.drawable.placeholder);

                }

            }
        });
        builderSingle.show();

    }

    private void uploadFile(String name, String email, String gender, String bloodGroup, String DOB, String city,
                            String last_bleed, String profile, String token) {

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        String user_id = preferences.getString("user_id", "");


        Call<ResponseBody> call;

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = thumbFile1;


        if (thumbFile1 != null) {

            // Parsing any Media type file
            final RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .uploadFile(fileToUpload, filename, name, email, gender, bloodGroup, DOB, city, user_id, last_bleed, token);

        } else {


            String[] aa = profile.split("/");
            profile = aa[aa.length - 1];


            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .insertingProfileDataWithoutFile(name, email, gender, bloodGroup, DOB, city, user_id, last_bleed, profile, token);


        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();

                String s = null;

                try {
                    if (response.code() == Constants.STATUS_OK) {

                        String serverResponse = response.body().string();

                        JSONObject jsonObject = new JSONObject(serverResponse);

                        SharedPreferences.Editor preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                        preferences.putString("password", etPass.getText().toString().trim());

                        preferences.apply();

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(context, HomePageActivity.class));

                        finish();

                    } else {
                        s = response.errorBody().string();
                    }

                    if (s != null) {

                        JSONObject jsonObject = new JSONObject(s);

                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
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

}
