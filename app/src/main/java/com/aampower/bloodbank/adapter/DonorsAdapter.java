package com.aampower.bloodbank.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.model.DonorsModel;
import com.aampower.bloodbank.ui.FullImageActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private List<DonorsModel> arrayList;

    private List<DonorsModel> duplicateList;

    private LayoutInflater inflater;

    public DonorsAdapter(Activity context, List<DonorsModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

        this.duplicateList = new ArrayList<>();
        this.duplicateList.addAll(arrayList);

        this.inflater = LayoutInflater.from(context);

    }

    public void refreshData(List<DonorsModel> arrayList) {

        this.arrayList.clear();
        this.arrayList = arrayList;

        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new ViewHolder2(inflater.inflate(R.layout.donors_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {


        final ViewHolder2 viewHolder2 = (ViewHolder2) holder;

        viewHolder2.tvDoName.setText(arrayList.get(position).getName());
        viewHolder2.tvDoCityName.setText(arrayList.get(position).getCity());
        viewHolder2.tvBloodGroup.setText(arrayList.get(position).getBlood_group());

        if (arrayList.get(position).getLast_bleed() == null || arrayList.get(position).getLast_bleed().isEmpty()) {
            viewHolder2.tvLastBleed.setText("Last bleed date not provided");
        } else {

            try {

                String input_date = arrayList.get(position).getLast_bleed();
                SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dt1 = format1.parse(input_date);
                DateFormat format2 = new SimpleDateFormat("EEEE");
                String finalDay = format2.format(dt1);
                String day = new SimpleDateFormat("dd").format(dt1);
                String monthString  = new SimpleDateFormat("MMM").format(dt1);
                String year = new SimpleDateFormat("yyyy").format(dt1);


                viewHolder2.tvLastBleed.setText("Last date of donation " + day + " " + monthString + " " +  year);


            } catch (ParseException e) {
                e.printStackTrace();

                viewHolder2.tvLastBleed.setText("Last bleed date: " + arrayList.get(position).getLast_bleed());
            }

        }

        Picasso.get().load(arrayList.get(position).getProfile_image()).into(viewHolder2.imgUser, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                viewHolder2.imgUser.setImageResource(R.drawable.placeholder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class ViewHolder2 extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView tvDoName, tvDoCityName, tvBloodGroup, tvLastBleed;
        Button btnCall;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            tvDoName = itemView.findViewById(R.id.tvDoName);
            tvDoCityName = itemView.findViewById(R.id.tvDoCityName);
            tvBloodGroup = itemView.findViewById(R.id.tvBloodGroup);
            tvLastBleed = itemView.findViewById(R.id.tvLastBleed);
            btnCall = itemView.findViewById(R.id.btnCall);

            imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("imgURL", arrayList.get(getAdapterPosition()).getProfile_image());
                    bundle.putString("accName", arrayList.get(getAdapterPosition()).getName());

                    Intent intent = new Intent(context, FullImageActivity.class);

                    intent.putExtras(bundle);

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, imgUser, ViewCompat.getTransitionName(imgUser));

                    context.startActivity(intent, optionsCompat.toBundle());
                }
            });

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                    builderSingle.setTitle("Choose Action");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
                    arrayAdapter.add("Call");
                    arrayAdapter.add("Send Message");

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);

                            if (which == 0) {

                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + arrayList.get(getAdapterPosition()).getPhone_number()));
                                context.startActivity(intent);

                            } else if (which == 1) {

                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:" + arrayList.get(getAdapterPosition()).getPhone_number()));
                                context.startActivity(sendIntent);
                            }

                        }
                    });
                    builderSingle.show();
                }
            });

        }
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(duplicateList);
        } else {
            for (int i = 0; i < duplicateList.size(); i++) {

                if (duplicateList.get(i) != null) {

                    if (duplicateList.get(i).getBlood_group().toLowerCase(Locale.getDefault()).equals(charText)) {

                        arrayList.add(duplicateList.get(i));

                    }
                }

            }

        }

        if (arrayList.size() == 0) {
            Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show();
        }

        notifyDataSetChanged();
    }

}
