package com.aampower.bloodbank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.adapter.FulfillReqsAdapter;
import com.aampower.bloodbank.model.FulFillModel;
import com.aampower.bloodbank.model.FullFillListModel;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestFulfilledActivity extends AppCompatActivity {

    SwipeRefreshLayout fulfillPullToRefresh;
    RecyclerView fulfillRecView;
    Toolbar fulfillToolBar;

    String req_id;
    List<FulFillModel> fulFillModelsList;

    FulfillReqsAdapter adapter;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = RequestFulfilledActivity.this;
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
        setContentView(R.layout.activity_request_fulfilled);
        fulfillRecView = findViewById(R.id.fulfillRecView);
        fulfillPullToRefresh = findViewById(R.id.fulfillPullToRefresh);
        fulfillToolBar = findViewById(R.id.fulfillToolBar);

        setSupportActionBar(fulfillToolBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);

        }

        if (getIntent().getExtras() != null){

            Bundle bundle = getIntent().getExtras();

            req_id = bundle.getString("req_id");

        }

        fulfillRecView.setLayoutManager(new LinearLayoutManager(this));

        fulfillPullToRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        fulfillPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                gettingFulfillReqs(req_id);

            }
        });

        gettingFulfillReqs(req_id);

    }


    private void gettingFulfillReqs(final String req_id){

        fulfillPullToRefresh.setRefreshing(true);

        Call<FullFillListModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingFulfillReqs(req_id);

        call.enqueue(new Callback<FullFillListModel>() {
            @Override
            public void onResponse(Call<FullFillListModel> call, Response<FullFillListModel> response) {

                fulfillPullToRefresh.setRefreshing(false);

                if (response.code() == Constants.STATUS_OK){

                    fulFillModelsList = response.body().getFulFillModels();

                    if (fulFillModelsList != null && fulFillModelsList.size() > 0){

                        adapter = new FulfillReqsAdapter(context, fulFillModelsList, req_id);
                        fulfillRecView.setAdapter(adapter);

                    }else {
                        Toast.makeText(RequestFulfilledActivity.this, "No one respond your blood request", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<FullFillListModel> call, Throwable t) {

                fulfillPullToRefresh.setRefreshing(false);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }


        return true;
    }

}
