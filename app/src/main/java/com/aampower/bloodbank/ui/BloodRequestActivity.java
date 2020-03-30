package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.adapter.RequestAdapter;
import com.aampower.bloodbank.model.BloodRequestsListModel;
import com.aampower.bloodbank.model.CitiesModel;
import com.aampower.bloodbank.model.RequestModel;
import com.aampower.bloodbank.model.RequestModel2;
import com.aampower.bloodbank.ui.fragments.SelectCityFragment;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.jaeger.library.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodRequestActivity extends AppCompatActivity {

    RecyclerView donorsRecView;
    Toolbar donorsToolBar;
    View headreView;
    SwipeRefreshLayout pullToRefresh;
    TextView btnNewRequests, acceptedRequests, myRequests, btnBecomeDonor;

    List<RequestModel> arrayList;
    List<RequestModel> dupList;

    int refresh = 0;
    Alert dialog;
    RequestAdapter adapter;

    Dialog alertDialog;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = BloodRequestActivity.this;
        super.onCreate(savedInstanceState);

        StatusBarUtil.setTransparent(context);

        //region

        //region

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                w.setNavigationBarColor(getResources().getColor(R.color.black));
//            }
//        }

        //endregion

//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
//
//            Window window = context.getWindow();
//
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            window.setStatusBarColor(ContextCompat.getColor(context, R.color.white));
//
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Window window = context.getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(ContextCompat.getColor(context, R.color.white));
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//
//        }

        //endregion

        setContentView(R.layout.activity_donors_list);
        donorsRecView = findViewById(R.id.donorsRecView);
        donorsToolBar = findViewById(R.id.donorsToolBar);
        btnNewRequests = findViewById(R.id.btnNewRequests);
        acceptedRequests = findViewById(R.id.acceptedRequests);
        btnBecomeDonor = findViewById(R.id.btnBecomeDonor);
        myRequests = findViewById(R.id.myRequests);
        headreView = findViewById(R.id.headreView);
//        pullToRefresh = findViewById(R.id.pullToRefresh);
        setSupportActionBar(donorsToolBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back_white);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        donorsRecView.setLayoutManager(layoutManager);

//        pullToRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
//
//        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                refresh = 0;
//
////                bloodRequestTask2();
//
//            }
//        });

//        bloodRequestTask2();


        btnNewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, NewRequestsActivity.class));

            }
        });

        acceptedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, MyResponsesActivity.class));

            }
        });

        myRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("id", 1);

                Intent intent = new Intent(context, MyResponsesActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);

            }
        });

        btnBecomeDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddBloodRequestActivity.class));
            }
        });


    }


    public void bloodRequestTask() {

        if (refresh == 0) {
            dialog = new Alert(context);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        Call<BloodRequestsListModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingBloodRequestsList();

        call.enqueue(new Callback<BloodRequestsListModel>() {
            @Override
            public void onResponse(Call<BloodRequestsListModel> call, Response<BloodRequestsListModel> response) {
                if (refresh == 0) {
                    dialog.dismiss();
                }

                pullToRefresh.setRefreshing(false);

                if (response.code() == Constants.STATUS_OK) {

                    arrayList = response.body().getMessage();

                    if (arrayList != null && arrayList.size() > 0) {

                        dupList = new ArrayList<>();

                        for (int i = 0; i < arrayList.size(); i++) {

                            if (i == 0) {
                                dupList.add(null);
                            }
                            dupList.add(arrayList.get(i));

                        }

                        adapter = new RequestAdapter(context, dupList, 100);
                        donorsRecView.setAdapter(adapter);

                    } else {

                        dupList = new ArrayList<>();
                        dupList.add(null);

                        adapter = new RequestAdapter(context, dupList, 100);
                        donorsRecView.setAdapter(adapter);

                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                    }

                }

                refresh = 1;

            }

            @Override
            public void onFailure(Call<BloodRequestsListModel> call, Throwable t) {
                if (refresh == 0) {
                    dialog.dismiss();
                }

                pullToRefresh.setRefreshing(false);

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
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            //refershingData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return true;

    }

    public void refershingData() {


        Call<BloodRequestsListModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingBloodRequestsList();

        call.enqueue(new Callback<BloodRequestsListModel>() {
            @Override
            public void onResponse(Call<BloodRequestsListModel> call, Response<BloodRequestsListModel> response) {

                if (response.code() == Constants.STATUS_OK) {

                    arrayList = response.body().getMessage();

                    if (arrayList != null && arrayList.size() > 0) {

                        dupList = new ArrayList<>();

                        for (int i = 0; i < arrayList.size(); i++) {

                            if (i == 0) {
                                dupList.add(null);
                            }
                            dupList.add(arrayList.get(i));

                        }

                        adapter.refreshData(dupList);

                    }

                }
            }

            @Override
            public void onFailure(Call<BloodRequestsListModel> call, Throwable t) {

            }
        });


    }

    public void bloodRequestTask2() {

        if (refresh == 0) {
            dialog = new Alert(context);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingBloodRequestsList2();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (refresh == 0) {
                    dialog.dismiss();
                }

                pullToRefresh.setRefreshing(false);

                if (response.code() == Constants.STATUS_OK) {

                    JSONObject parentObject = null;
                    try {

                        parentObject = new JSONObject(response.body().string());

                        boolean isError = parentObject.getBoolean("error");
                        JSONArray jsonArray = parentObject.getJSONArray("message");

                        List<RequestModel2> modelList = new ArrayList<>();

                        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                        String user_id = preferences.getString("user_id", "");

                        if (jsonArray != null && jsonArray.length() > 0) {

                            dupList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (i == 0) {
                                    dupList.add(null);
                                }

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String accepter_user_id = jsonObject.getString("user_id");

                                RequestModel model = new RequestModel(

                                        jsonObject.getString("req_id"),
                                        jsonObject.getString("unique_id"),
                                        jsonObject.getString("blood_request_type"),
                                        jsonObject.getString("blood_group"),
                                        jsonObject.getString("reason"),
                                        jsonObject.getString("latlng"),
                                        jsonObject.getString("city"),
                                        jsonObject.getString("message"),
                                        jsonObject.getString("phone_number_sec_person"),
                                        jsonObject.getString("profile_image"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("address"),
                                        jsonObject.getString("token"),
                                        jsonObject.getString("status"),
                                        jsonObject.getString("user_id")

                                );

                                dupList.add(model);

                            }

                            adapter = new RequestAdapter(context, dupList, 100);
                            donorsRecView.setAdapter(adapter);

                        } else {

                            dupList = new ArrayList<>();
                            dupList.add(null);

                            adapter = new RequestAdapter(context, dupList, 100);
                            donorsRecView.setAdapter(adapter);

                            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                refresh = 1;

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (refresh == 0) {
                    dialog.dismiss();
                }

                pullToRefresh.setRefreshing(false);

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
