package com.aampower.bloodbank.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.ui.fragments.SelectCityFragment;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBloodRequestActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etRequestType, etBloodGroup, etReason, etLoation, etMessage, etPhoneNumber, etCityy;
    TextView btnGetLocation;
    Button btnRequestFinish;
    Toolbar addToolBar;

    Activity context;

    String cityName, phoneNumber;

    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    LocationManager locationManager;
    String lat, lng;
    private static final int REQUEST_LOCATION = 111;
    int LOCATION_REQUEST_CODE = 10111;

    static final int TWO_MINUTES = 1000 * 60 * 2;

    private Location currentBestLocation = null;
    String latlng = "";

    String requestType = "", reason = "", bloodgroup = "", user_id = "";

    Alert dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = AddBloodRequestActivity.this;
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_add_blood_request);
        addToolBar = findViewById(R.id.addToolBar);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        etRequestType = findViewById(R.id.etRequestType);
        etBloodGroup = findViewById(R.id.etBloodGroup);
        etReason = findViewById(R.id.etReason);
        etLoation = findViewById(R.id.etLoation);
        etMessage = findViewById(R.id.etMessage);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnRequestFinish = findViewById(R.id.btnRequestFinish);
        etCityy = findViewById(R.id.etCityy);

        setSupportActionBar(addToolBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        phoneNumber = preferences.getString("phoneNumber", "");
        etPhoneNumber.setText(phoneNumber);
        user_id = preferences.getString("user_id", "");


        btnGetLocation.setOnClickListener(this);
        etRequestType.setOnClickListener(this);
        etBloodGroup.setOnClickListener(this);
        etReason.setOnClickListener(this);
        btnRequestFinish.setOnClickListener(this);
        etCityy.setOnClickListener(this);


    }

    private void choosingRequesstType() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Do you need whole blood or platelets?");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice);
        arrayAdapter.add("Whole Blood");
        arrayAdapter.add("White Blood");
        arrayAdapter.add("Platelets");
        arrayAdapter.add("AB Plasma");
        arrayAdapter.add("Double Red Cell");
        arrayAdapter.add("Cord Blood");
        arrayAdapter.add("I Don't Know");

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                requestType = strName;

                etRequestType.setText(requestType);
                etRequestType.setError(null);

            }
        });

        builderSingle.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builderSingle.show();

    }

    private void choosingBloodGroup() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Blood Group");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice);
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
                bloodgroup = strName;

                etBloodGroup.setError(null);

            }
        });
        builderSingle.show();

    }

    private void choosingBloodReason() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Do you need whole blood or platelets?");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice);
        arrayAdapter.add("Accident");
        arrayAdapter.add("Surgery");
        arrayAdapter.add("Pregnancy");
        arrayAdapter.add("Cancer");
        arrayAdapter.add("Transplant");
        arrayAdapter.add("Thalassemia");
        arrayAdapter.add("Low HB (Hemoglobin)");
        arrayAdapter.add("Other");


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                reason = strName;

                etReason.setText(reason);
                etReason.setError(null);

            }
        });

        builderSingle.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builderSingle.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_filter) {


        }


        return true;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnGetLocation) {

            // checking permissions
            if (!hasPermissions(this, PERMISSIONS)) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_REQUEST_CODE);

            } else {

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                assert locationManager != null;
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessage();
                } else {
//                    getLocation();

                    Location location = getLastBestLocation();

                    if (location != null) {

                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        latlng = lat + "," + lng;


                        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
                        StringBuilder builder = new StringBuilder();
                        try {
                            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
//                            int maxLines = address.get(0).getMaxAddressLineIndex();
//                            for (int i = 0; i < maxLines; i++) {
//                                String addressStr = address.get(0).getAddressLine(i);
//                                builder.append(addressStr);
//                                builder.append(" ");
//                            }

                            cityName = address.get(0).getAddressLine(0);
                            String stateName = address.get(0).getAddressLine(1);
                            String countryName = address.get(0).getAddressLine(2);

                            String fnialAddress = builder.toString(); //This is the complete address.

                            etLoation.setText(cityName);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Toast.makeText(context, "Location has been gathered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        } else if (id == R.id.etRequestType) {
            choosingRequesstType();
        } else if (id == R.id.etBloodGroup) {
            choosingBloodGroup();
        } else if (id == R.id.etReason) {
            choosingBloodReason();
        } else if (id == R.id.btnRequestFinish) {

            String hospitalName = etLoation.getText().toString().trim();


            if (!etPhoneNumber.getText().toString().trim().equals("")){
                phoneNumber = etPhoneNumber.getText().toString().trim();
            }
            String message = etMessage.getText().toString().trim();
            String city = etCityy.getText().toString().trim();


            if (requestType == null || requestType.isEmpty()) {
                etRequestType.setError("Required");
                return;
            }
            if (bloodgroup == null || bloodgroup.isEmpty()) {
                etBloodGroup.setError("Required");
                return;
            }
            if (reason == null || reason.isEmpty()) {
                etReason.setError("Required");
                return;
            }

            if (city.isEmpty()){
                etCityy.setError("Required");
                return;
            }

//            if (message.isEmpty()) {
//                etMessage.setError("Required");
//                return;
//            }


            addingBloodRequest(requestType, bloodgroup, reason, hospitalName, message, phoneNumber, city);

        }else if (id == R.id.etCityy){

            final SelectCityFragment dialogFragment = cityFragInstance(1);
            dialogFragment.setCityCallback(new SelectCityFragment.CityCallback() {
                @Override
                public void onCityActionClick(String cityName) {

                    etCityy.setText(cityName);
                    etCityy.setError(null);

                    hideKeyboard(context);

                    dialogFragment.dismiss();
                }
            });
            if (getFragmentManager() != null) {
                dialogFragment.show(getSupportFragmentManager(), "tag1");
            }

        }

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

    private void addingBloodRequest(String requestTypee, String bloodgroupp, String reasonn,
                                    String hospitalNamee, String messagee, String phoneNumberr, final String city) {

        dialog = new Alert(context);
        dialog.setMessage("Submitting...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .insertBloodRequest(user_id, requestTypee, bloodgroupp, reasonn, latlng, city, hospitalNamee, messagee, phoneNumberr);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();

                String s = null;

                try {

                    if (response.code() == Constants.USER_CREATED) {

                        s = response.body().string();

                        requestType = "";
                        bloodgroup = "";
                        reason = "";
                        etLoation.setText("");
                        latlng = "";
                        etMessage.setText("");
                        etPhoneNumber.setText("");
                        etCityy.setText("");
                        
                        etRequestType.setText("");
                        etBloodGroup.setText("");
                        etReason.setText("");
                        etLoation.setText("");

                        finish();

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

    private void buildAlertMessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1111);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private Location getLastBestLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_LOCATION);

        } else {

            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                return locationGPS;
            } else {
                return locationNet;
            }

        }

        return null;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.length > 0) {

                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        assert locationManager != null;
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            buildAlertMessage();
                        } else {

                            Location location = getLastBestLocation();

                            if (location != null) {

                                double lat = location.getLatitude();
                                double lng = location.getLongitude();

                                latlng = lat + "," + lng;

//                                etLoation.setText("Location has been gathered");

                                Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
                                StringBuilder builder = new StringBuilder();
                                try {
                                    List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
//                            int maxLines = address.get(0).getMaxAddressLineIndex();
//                            for (int i = 0; i < maxLines; i++) {
//                                String addressStr = address.get(0).getAddressLine(i);
//                                builder.append(addressStr);
//                                builder.append(" ");
//                            }

                                    cityName = address.get(0).getAddressLine(0);
                                    String stateName = address.get(0).getAddressLine(1);
                                    String countryName = address.get(0).getAddressLine(2);

                                    String fnialAddress = builder.toString(); //This is the complete address.

                                    etLoation.setText(cityName);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                }
            }

        }

    }
}
