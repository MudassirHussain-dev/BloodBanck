package com.aampower.bloodbank.ui.fragments;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.aampower.bloodbank.R;
import com.aampower.bloodbank.adapter.CityRecAdapter;
import com.aampower.bloodbank.model.CitiesModel;
import com.aampower.bloodbank.model.CityModel;
import com.aampower.bloodbank.ui.PhoneVeriActivity;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCityFragment extends DialogFragment implements View.OnClickListener {

    //    Toolbar cityToolbar;
    private EditText citySearchView;
    private RecyclerView cityRecView;
    private ImageView fullscreen_dialog_close;
    ProgressBar progressView;

    private Activity context;

    private CityCallback cityCallback;
    private CountryCallback countryCallback;

    private List<CityModel> citiesArrayList;

    private ArrayList<CityModel> arrayList = new ArrayList<>();

    private CityRecAdapter adapter;

    private int id;

    public void setCityCallback(CityCallback callback) {
        this.cityCallback = callback;
    }

    public void setCountryCallback(CountryCallback callback) {
        this.countryCallback = callback;
    }

    public static SelectCityFragment newInstance() {
        return new SelectCityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();

//        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view =  inflater.inflate(R.layout.select_city_fragment, container, false);


        cityRecView = view.findViewById(R.id.recViewCity);
        fullscreen_dialog_close = view.findViewById(R.id.fullscreen_dialog_close);
        citySearchView = view.findViewById(R.id.citySearchView);
        progressView = view.findViewById(R.id.progressView);

        progressView.setVisibility(View.GONE);

        citySearchView.setFocusableInTouchMode(false);
        citySearchView.setFocusable(false);
        citySearchView.setFocusableInTouchMode(true);
        citySearchView.setFocusable(true);

        citySearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filterCity(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cityRecView.setLayoutManager(new LinearLayoutManager(context));
//        cityRecView.addItemDecoration(new DividerItemDecoration(cityRecView.getContext(), DividerItemDecoration.VERTICAL));

//        if (id == 1 || id == 3) {
//
//            for (int i = 0; i < citiesList.length; i++) {
//
//                CityModel cityModel = new CityModel();
//
//                cityModel.setCityID("1");
//                cityModel.setCityName(citiesList[i]);
//
//                arrayList.add(cityModel);
//
//            }
//
//        } else {
//
//            for (int i = 0; i < countriesList.length; i++) {
//
//                CityModel cityModel = new CityModel();
//
//                cityModel.setCityID("1");
//                cityModel.setCityName(countriesList[i]);
//
//                arrayList.add(cityModel);
//
//            }
//
//        }

//        adapter = new CityRecAdapter(context, arrayList, SelectCityFragment.this, id);
//        cityRecView.setAdapter(adapter);

        fullscreen_dialog_close.setOnClickListener(this);

        settingCitiesList();

        return view;
    }


    private void settingCitiesList(){

        progressView.setVisibility(View.VISIBLE);


        Call<CitiesModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .gettingCitiesList();

        call.enqueue(new Callback<CitiesModel>() {
            @Override
            public void onResponse(Call<CitiesModel> call, Response<CitiesModel> response) {

                progressView.setVisibility(View.GONE);

                if (response.code() == Constants.STATUS_OK){

                    citiesArrayList = response.body().getCityModels();

                    if (citiesArrayList != null && citiesArrayList.size() > 0) {

                        adapter = new CityRecAdapter(context, citiesArrayList, SelectCityFragment.this, id);
                        cityRecView.setAdapter(adapter);

                    }else {
                        Toast.makeText(context, "Cities list not found", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<CitiesModel> call, Throwable t) {

                progressView.setVisibility(View.GONE);

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


    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public void cityName(String city) {

        cityCallback.onCityActionClick(city);

    }

    public void countryName(String country) {

        countryCallback.onCountryActionClick(country);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

         if (id == R.id.fullscreen_dialog_close) {
            hideKeyboard(context);
            dismiss();
        }

    }

    public interface CityCallback {

        void onCityActionClick(String cityName);

    }

    public interface CountryCallback {

        void onCountryActionClick(String countryName);

    }

    public static SelectCityFragment cityFragInstance() {

        SelectCityFragment cityFragment = new SelectCityFragment().newInstance();

        Bundle bundle = new Bundle();
        bundle.putInt("id", 2);

        cityFragment.setArguments(bundle);

        return cityFragment;

    }

}
