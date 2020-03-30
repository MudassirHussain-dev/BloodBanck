package com.aampower.bloodbank.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.aampower.bloodbank.utils.SimpleOTPGenerator;
import com.chaos.view.PinView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class PhoneVeriActivity extends AppCompatActivity {

    EditText etPhoneNumber;
    Button btnOTP;

    Alert dialog;

    Toolbar phoneVeriToolBar;

    Activity context;

//    String[] PERMISSIONS = {
//            Manifest.permission.RECEIVE_SMS
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = PhoneVeriActivity.this;
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

        setContentView(R.layout.activity_phone_veri);
        phoneVeriToolBar = findViewById(R.id.phoneVeriToolBar);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnOTP = findViewById(R.id.btnOTP);

        setSupportActionBar(phoneVeriToolBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
//            backArrow.setColorFilter(getResources().getColor(R.color.md_grey_900), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }


        btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if (phoneNumber.isEmpty()) {
                    etPhoneNumber.setError("Phone number required");
                    etPhoneNumber.requestFocus();
                    return;
                }

//                if (!hasPermissions(context, PERMISSIONS)) {
//
//                    ActivityCompat.requestPermissions(context, PERMISSIONS, 1223);
//
//                } else

                if (phoneNumber.substring(0, 3).equals("+92")) {

                    phoneNumber = phoneNumber.replace("+92", "0");

                } else if (!String.valueOf(phoneNumber.charAt(0)).equals("0")) {
                    phoneNumber = "0" + phoneNumber;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("NUMBER CONFIRMATION");

                builder.setMessage("\n" + phoneNumber + "\n\n" + "Is your phone number above correct?");

                final String finalPhoneNumber = phoneNumber;
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendingOTPTask(finalPhoneNumber);
                    }
                });

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();

//                }


            }
        });

    }

    private void sendingOTPTask(final String phoneNumber) {

//        dialog = new Dialog(context);
//
//        dialog.setContentView(R.layout.loading_alert);
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//
//        dialog.show();

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
                .sendingOTP(phoneNumber, otpCode);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();


//                if (response.code() == Constants.USER_CREATED){
//                    DefaultResponse dr = response.body();
//                    Toast.makeText(PhoneVeriActivity.this, dr.getMessage(), Toast.LENGTH_SHORT).show();
//                }else if (response.code() == 422){
//                    DefaultResponse dr = response.body();
//                    Toast.makeText(PhoneVeriActivity.this, dr.getMessage(), Toast.LENGTH_SHORT).show();
//                }
                try {

                    String s = null;

                    if (response.code() == Constants.STATUS_OK) {
//                        s = response.body().string();

//                        SmsListener smsListener = new SmsListener() {
//                            @Override
//                            public void messageReceived(String messageText) {
//
//                                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
//
//                                String storedOTP = preferences.getString("otpCode", "");
//
//                                if (messageText.equals(storedOTP)) {
//
//                                    SharedPreferences.Editor editor = preferences.edit();
//                                    editor.putString("phoneNumber", phoneNumber);
//                                    editor.apply();
//
//                                    dialog.dismiss();
//
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt("code", Constants.STATUS_OK);
//
//                                    Intent intent = new Intent(context, PersonalInfoActivity.class);
//                                    intent.putExtras(bundle);
//
//                                    startActivity(intent);
//
//                                    finish();
//
//                                }
//                            }
//                        };
//
//                        SmsReceiver.bindListener(smsListener);

                        pinAlertView(phoneNumber);

                    } else if (response.code() == 423) {

                        SharedPreferences.Editor preferences1 = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                        preferences1.putString("phoneNumber", phoneNumber);
                        preferences1.apply();
                        preferences1.commit();

                        Bundle bundle = new Bundle();
                        bundle.putInt("code", 423);

                        Intent intent = new Intent(context, PersonalInfoActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);

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


                Toast.makeText(PhoneVeriActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pinAlertView(final String phoneNumber) {

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

                        dialog.dismiss();

                        Bundle bundle = new Bundle();
                        bundle.putInt("code", Constants.STATUS_OK);

                        Intent intent = new Intent(context, PersonalInfoActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);

                        finish();

                    }else {
                        Toast.makeText(context, "OTP not match", Toast.LENGTH_SHORT).show();
                    }


//                    if (pinview.getText().toString().trim().equals(preferences.getString("otpCode", ""))) {
//
//                        alertDialog.dismiss();
//
//                        startActivity(new Intent(context, PersonalInfoActivity.class));
//
//                        finish();
//
//                    }

                }

            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if (phoneNumber.substring(0, 3).equals("+92")) {

                    phoneNumber = phoneNumber.replace("+92", "0");

                } else if (!String.valueOf(phoneNumber.charAt(0)).equals("0")) {
                    phoneNumber = "0" + phoneNumber;
                }

                sendingOTPTask(phoneNumber);

            }
        });

        alertDialog.show();

//        LayoutInflater factory = LayoutInflater.from(context);
//        AlertDialog.Builder alert = new AlertDialog.Builder(context);
//
//        Dialog  dialog = new Dialog(context);
//
//        dialog.setContentView(R.layout.otp_alert_view);
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);


//        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 1223) {

            if (grantResults.length > 0) {

                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equals(Manifest.permission.RECEIVE_SMS)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            String phoneNumber = etPhoneNumber.getText().toString().trim();

                            if (phoneNumber.substring(0, 3).equals("+92")) {

                                phoneNumber = phoneNumber.replace("+92", "0");

                            } else if (!String.valueOf(phoneNumber.charAt(0)).equals("0")) {
                                phoneNumber = "0" + phoneNumber;
                            }

                            sendingOTPTask(phoneNumber);

                        } else {

                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
                        }
                    }

                }
            }

        }

    }
}
