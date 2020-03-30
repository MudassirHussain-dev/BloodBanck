package com.aampower.bloodbank.adapter;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.model.FulFillModel;
import com.aampower.bloodbank.model.FullFillListModel;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FulfillReqsAdapter extends RecyclerView.Adapter<FulfillReqsAdapter.ViewHolder> {

    private Activity context;
    private List<FulFillModel> modelList;

    private LayoutInflater layoutInflater;

    private Alert dialog;
    private String req_id;

    public FulfillReqsAdapter (Activity context, List<FulFillModel> modelList, String req_id){
        this.context = context;
        this.modelList = modelList;
        this.req_id = req_id;

        this.layoutInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.donors_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String id = modelList.get(position).getId();
        String user_id = modelList.get(position).getUser_id();
        String name = modelList.get(position).getName();
        String city = modelList.get(position).getCity();
        String phoneNumber = modelList.get(position).getPhoneNumber();
        String blood_group = modelList.get(position).getBlood_group();
        String profile_image = modelList.get(position).getProfile_image();
        String token = modelList.get(position).getToken();
        String last_bleed = modelList.get(position).getLast_bleed();

        holder.tvDoName.setText(name);
        holder.tvDoCityName.setText(city);
        holder.tvBloodGroup.setText(blood_group);


        if (last_bleed == null || last_bleed.isEmpty()) {
            holder.tvLastBleed.setText("Last bleed date not provided");
        } else {
            try {

                SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dt1 = format1.parse(last_bleed);
                DateFormat format2 = new SimpleDateFormat("EEEE");
                String finalDay = format2.format(dt1);
                String day = new SimpleDateFormat("dd").format(dt1);
                String monthString = new SimpleDateFormat("MMM").format(dt1);
                String year = new SimpleDateFormat("yyyy").format(dt1);


                holder.tvLastBleed.setText("Last date of donation " + day + " " + monthString + " " + year);


            } catch (ParseException e) {
                e.printStackTrace();

                holder.tvLastBleed.setText("Last bleed date: " + last_bleed);
            }

        }


        Picasso.get().load(profile_image).resize(512, 512).into(holder.imgUser);


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgUser;
        TextView tvDoName, tvDoCityName, tvBloodGroup, tvLastBleed;
        Button btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            tvDoName = itemView.findViewById(R.id.tvDoName);
            tvDoCityName = itemView.findViewById(R.id.tvDoCityName);
            tvBloodGroup = itemView.findViewById(R.id.tvBloodGroup);
            tvLastBleed = itemView.findViewById(R.id.tvLastBleed);
            btnCall = itemView.findViewById(R.id.btnCall);

            btnCall.setVisibility(View.VISIBLE);
            btnCall.setText("Select");

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Is this person fulfilled your request?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            settingFulfilledReq(req_id, modelList.get(getAdapterPosition()).getUser_id());

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create().show();

                }
            });

        }
    }


    private void settingFulfilledReq(final String req_id, String donor_id){

        dialog = new Alert(context);
        dialog.setMessage("Submitting...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .settingFulfilledReq(req_id, donor_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();

                String s = null;

                try {

                    if (response.code() == Constants.STATUS_OK) {
                        s = response.body().string();
                        context.finish();
                    }else {
                        s = response.errorBody().string();
                    }

                    if (s != null){

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
