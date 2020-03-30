package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.adapter.DonorsAdapter;
import com.aampower.bloodbank.model.BloodRequestsListModel;
import com.aampower.bloodbank.model.DonorsListModel;
import com.aampower.bloodbank.model.DonorsModel;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonorsListActivity extends AppCompatActivity {

    Toolbar donToolbar;
    RecyclerView donRecView;
    SwipeRefreshLayout donorPullToRefresh;

    List<DonorsModel> arrayList;

    int refresh = 0;
    Alert dialog;

    DonorsAdapter adapter;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = DonorsListActivity.this;
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
        setContentView(R.layout.activity_donors_list2);
        donToolbar = findViewById(R.id.donToolBar);
        donRecView = findViewById(R.id.donRecView);
        donorPullToRefresh = findViewById(R.id.donorPullToRefresh);

        setSupportActionBar(donToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        donRecView.setLayoutManager(layoutManager);

        donorPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refresh = 0;

                donorsTask();

            }
        });


    }

    private void donorsTask() {


        if (refresh == 0) {
            dialog = new Alert(context);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        Call<DonorsListModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingDonorsList();

        call.enqueue(new Callback<DonorsListModel>() {
            @Override
            public void onResponse(Call<DonorsListModel> call, Response<DonorsListModel> response) {
                if (refresh == 0) {
                    dialog.dismiss();
                }

                donorPullToRefresh.setRefreshing(false);

                if (response.code() == Constants.STATUS_OK){

                    arrayList = response.body().getMessage();

                    if (arrayList != null && arrayList.size() > 0) {

                        if (refresh == 0) {

                            adapter = new DonorsAdapter(context, arrayList);
                            donRecView.setAdapter(adapter);

                        }else if (refresh == 1){

                            adapter.refreshData(arrayList);

                        }

                    }else {
                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                    }

                }

                refresh = 1;

            }

            @Override
            public void onFailure(Call<DonorsListModel> call, Throwable t) {
                if (refresh == 0) {
                    dialog.dismiss();
                }

                donorPullToRefresh.setRefreshing(false);

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

        donorsTask();

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

        getMenuInflater().inflate(R.menu.filter_menu_red, menu);

        return true;
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

                if (which == 0){
                    adapter.filter("");
                }else {
                    adapter.filter(strName);
                }

            }
        });
        builderSingle.show();

    }

}
