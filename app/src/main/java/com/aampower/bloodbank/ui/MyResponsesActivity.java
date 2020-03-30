package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.adapter.RequestAdapter;
import com.aampower.bloodbank.model.BloodRequestsListModel;
import com.aampower.bloodbank.model.RequestModel;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.jaeger.library.StatusBarUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyResponsesActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView newReqRecView;
    Toolbar reqToolBar;
    SwipeRefreshLayout pullToRefresh;

    List<RequestModel> arrayList;
    List<RequestModel> dupList;

    Alert dialog;
    RequestAdapter adapter;
    int refresh = 0;

    int identity = 0;

    String user_id;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = MyResponsesActivity.this;
        super.onCreate(savedInstanceState);

        StatusBarUtil.setTransparent(context);

        setContentView(R.layout.activity_new_requests);

        newReqRecView = findViewById(R.id.newReqRecView);
        reqToolBar = findViewById(R.id.reqToolBar);
        pullToRefresh = findViewById(R.id.pullToRefresh);

        setSupportActionBar(reqToolBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back_white);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }




        if (getIntent().getExtras() != null){

            Bundle bundle = getIntent().getExtras();
            identity = bundle.getInt("id");

        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        user_id = preferences.getString("user_id", "");


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        newReqRecView.setLayoutManager(layoutManager);

        pullToRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refresh = 0;

                acceptedBloodRequestTask();

            }
        });

        acceptedBloodRequestTask();

    }


    public void acceptedBloodRequestTask() {

        if (refresh == 0) {
            dialog = new Alert(context);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        Call<BloodRequestsListModel> call = null;

        if (identity == 0) {
            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .gettingMyAcceptedRequests(user_id);
        }else if (identity == 1){
            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .gettingMyAllRequests(user_id);
        }

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

                        if (identity == 1){
                            adapter = new RequestAdapter(context, dupList, 100);
                        }else {
                            adapter = new RequestAdapter(context, dupList, 101);
                        }
                        newReqRecView.setAdapter(adapter);

                    } else {

                        dupList = new ArrayList<>();
                        dupList.add(null);

                        if (identity == 1){
                            adapter = new RequestAdapter(context, dupList, 100);
                        }else {
                            adapter = new RequestAdapter(context, dupList, 101);
                        }
                        newReqRecView.setAdapter(adapter);

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
            refershingData();
        }

    }

    public void refershingData() {

        Call<BloodRequestsListModel> call = null;

        if (identity == 0) {
            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .gettingMyAcceptedRequests(user_id);
        }else if (identity == 1){
            call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .gettingMyAllRequests(user_id);
        }

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_filter) {

            choosingBloodGroup();
        }


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.filter_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {

        TextView textView = (TextView) v;

        adapter.filter(textView.getText().toString());

    }

    private void choosingBloodGroup() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Blood Group");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("All");
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

                if (which == 0) {
                    adapter.filter("");
                } else {
                    adapter.filter(strName);
                }

            }
        });
        builderSingle.show();

    }




}

