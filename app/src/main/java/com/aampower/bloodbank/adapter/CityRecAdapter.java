package com.aampower.bloodbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.aampower.bloodbank.R;
import com.aampower.bloodbank.model.CityModel;
import com.aampower.bloodbank.ui.fragments.SelectCityFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CityRecAdapter extends RecyclerView.Adapter<CityRecAdapter.ViewHolder>{

    private Activity context;
    private List<CityModel> arrayList;
    private LayoutInflater inflater;

    int id;

    SelectCityFragment fragment;

    private List<CityModel> duplicateList;

    public CityRecAdapter(Activity context, List<CityModel> arrayList, SelectCityFragment fragment, int id){

        this.context = context;
        this.arrayList = arrayList;
        this.id = id;
        this.fragment = fragment;

        this.inflater = context.getLayoutInflater();

        this.duplicateList = new ArrayList<>();
        this.duplicateList.addAll(arrayList);



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.city_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String cityID = arrayList.get(position).getCityID();
        String cityName = arrayList.get(position).getCityName();


        viewHolder.tvCityName.setText(cityName);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout rootView;
        TextView tvCityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.cityListRootView);
            tvCityName = itemView.findViewById(R.id.tvCityName);


            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (id == 1 || id == 3) {

                        fragment.cityName(arrayList.get(getAdapterPosition()).getCityName());

                    }else if (id == 2){

                        fragment.countryName(arrayList.get(getAdapterPosition()).getCityName());

                    }
                }
            });

        }
    }


    public void filterCity(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(duplicateList);
        } else {
            for (CityModel wp : duplicateList) {

                if (wp.getCityName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
