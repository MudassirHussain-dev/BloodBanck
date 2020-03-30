package com.aampower.bloodbank.adapter;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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
import com.aampower.bloodbank.model.RequestModel;
import com.aampower.bloodbank.ui.AddBloodRequestActivity;
import com.aampower.bloodbank.ui.BloodRequestActivity;
import com.aampower.bloodbank.ui.FullImageActivity;
import com.aampower.bloodbank.ui.NewRequestsActivity;
import com.aampower.bloodbank.ui.RequestFulfilledActivity;
import com.aampower.bloodbank.utils.Alert;
import com.aampower.bloodbank.utils.Constants;
import com.aampower.bloodbank.utils.RetrofitClient;
import com.facebook.share.Share;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Activity context;
    private List<RequestModel> arrayList;

    private LayoutInflater inflater;
    private List<RequestModel> duplicateList;

    int aa;

    private String userID, phoneNumber;
    private Alert dialog;

    public RequestAdapter(Activity context, List<RequestModel> arrayList, int aa) {
        this.context = context;
        this.arrayList = arrayList;

        this.aa = aa;

        this.duplicateList = new ArrayList<>();
        this.duplicateList.addAll(arrayList);

        this.inflater = LayoutInflater.from(context);

        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
        userID = preferences.getString("user_id", "");
        phoneNumber = preferences.getString("phoneNumber", "");

    }

    public void refreshData(List<RequestModel> arrayList) {

        this.arrayList.clear();
        this.arrayList = arrayList;


        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ViewHolder1(inflater.inflate(R.layout.donors_list_header_item, viewGroup, false));
            case TYPE_ITEM:
                return new ViewHolder2(inflater.inflate(R.layout.requests_list_item, viewGroup, false));
        }


        return new ViewHolder2(inflater.inflate(R.layout.donors_list_header_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder1 viewHolder0 = (ViewHolder1) holder;

                viewHolder0.tvDonorsCount.setText(String.valueOf(arrayList.size() - 1));

                viewHolder0.itemView.findViewById(R.id.btnBecomeDonor).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        context.startActivity(new Intent(context, AddBloodRequestActivity.class));
                    }
                });

                break;

            case 1:
                final ViewHolder2 viewHolder2 = (ViewHolder2) holder;

                viewHolder2.tvReqName.setText(arrayList.get(position).getName());
                viewHolder2.tvReqCity.setText(arrayList.get(position).getCity());
                viewHolder2.tvReqBloodGroup.setText(arrayList.get(position).getBlood_group());

                String status = arrayList.get(position).getStatus();

                if (status.equals("1")) {
                    viewHolder2.tvRequested.setText("Requested");
                } else if (status.equals("0")) {
                    viewHolder2.tvRequested.setText("Canceled");
                    viewHolder2.tvReqPhone.setEnabled(false);
                    viewHolder2.tvReqPhone.setAlpha(0.5f);
                    viewHolder2.tvRequested.setBackgroundResource(R.drawable.gradiant_yellow);
                } else if (status.equals("2")) {
                    viewHolder2.tvRequested.setText("Fulfilled");
                    viewHolder2.tvReqPhone.setEnabled(false);
                    viewHolder2.tvReqPhone.setAlpha(0.5f);
                    viewHolder2.tvRequested.setBackgroundResource(R.drawable.gradiant_green);
                }
                else if (status.equals("3") && aa == 101) {

//                    String donor_id = arrayList.get(position).getDonor_id();
//
//                    if (userID.equals(donor_id)) {

                        viewHolder2.tvReqPhone.setText("SHOW DETAILS");

//                    }

                }

                if (aa != 101) {

                    String user_id = arrayList.get(position).getUser_id();
                    if (user_id != null && user_id.equals(userID)) {
                        viewHolder2.tvReqPhone.setText("Cancel | Fulfill");
                    }

                }

                Picasso.get().load(arrayList.get(position).getProfile_image()).resize(512, 512).centerCrop().into(viewHolder2.imgReqUser);


                break;
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) == null) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {

        TextView tvDonorsCount;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);

            this.tvDonorsCount = itemView.findViewById(R.id.tvDonorsCountt);

//            this.tvDonorsCount.setText(String.valueOf(arrayList.size() - 1));

        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        ImageView imgReqUser;
        TextView tvReqName, tvReqCity, tvReqBloodGroup, tvReqPhone, tvRequested;
        Button btnCall;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            imgReqUser = itemView.findViewById(R.id.imgReqUser);
            tvReqName = itemView.findViewById(R.id.tvReqName);
            tvReqCity = itemView.findViewById(R.id.tvReqCity);
            tvReqBloodGroup = itemView.findViewById(R.id.tvReqBloodGroup);
            tvReqPhone = itemView.findViewById(R.id.tvReqPhone);
            tvRequested = itemView.findViewById(R.id.tvRequested);

            imgReqUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("imgURL", arrayList.get(getAdapterPosition()).getProfile_image());
                    bundle.putString("accName", arrayList.get(getAdapterPosition()).getName());

                    Intent intent = new Intent(context, FullImageActivity.class);

                    intent.putExtras(bundle);

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, imgReqUser, ViewCompat.getTransitionName(imgReqUser));

                    context.startActivity(intent, optionsCompat.toBundle());
                }
            });

            tvReqPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (tvReqPhone.getText().equals("Cancel | Fulfill")) {


                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("Select one");

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
                        arrayAdapter.add("Request Fulfilled");
                        arrayAdapter.add("Cancel Request");

                        builder1.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {

                                    Bundle bundle = new Bundle();
                                    bundle.putString("req_id", arrayList.get(getAdapterPosition()).getId());

                                    Intent intent = new Intent(context, RequestFulfilledActivity.class);
                                    intent.putExtras(bundle);

                                    context.startActivity(intent);

                                } else if (which == 1) {
                                    cancellingReqAlert(getAdapterPosition());
                                    dialog.dismiss();
                                }

                            }
                        });

                        builder1.create().show();


                    } else if (tvReqPhone.getText().equals("SHOW DETAILS")) {

                        showingDetailAlert(getAdapterPosition());

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Sure?");
                        builder.setMessage("Are you sure you want to accept this blood request");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
                                String user_id = preferences.getString("user_id", "");

                                sendingGCM(arrayList.get(getAdapterPosition()).getToken(),
                                        arrayList.get(getAdapterPosition()).getBlood_group(),
                                        arrayList.get(getAdapterPosition()).getCity(),
                                        arrayList.get(getAdapterPosition()).getName(),
                                        arrayList.get(getAdapterPosition()).getProfile_image(),
                                        arrayList.get(getAdapterPosition()).getId(),
                                        arrayList.get(getAdapterPosition()).getUser_id(),
                                        user_id, getAdapterPosition()
                                );

                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.create().show();


                    }

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
                } else {
                    arrayList.add(null);
                }

            }
        }

        if (arrayList.size() == 1) {
            Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show();
        }

        notifyDataSetChanged();
    }

    private void sendingGCM(String token, String bloodGroup, String city, String userName, String profile,
                            String id, String recipient_userID, String user_id, final int position) {

        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .sendingGCM(token, bloodGroup, city, userName, profile, id, recipient_userID, user_id, phoneNumber);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();

                String s = null;

                try {

                    if (response.code() == Constants.STATUS_OK) {

                        ((NewRequestsActivity) context).refershingData();

                        showingDetailAlert(position);

                    } else if (response.code() == Constants.STATUS_ERROR) {
                        s = response.errorBody().string();
                    } else {
                        Toast.makeText(context, "Internal error", Toast.LENGTH_SHORT).show();
                    }

                    if (s != null) {
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }

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

    private void showingDetailAlert(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Details");

        View view = context.getLayoutInflater().inflate(R.layout.request_detail_layout, null);
        builder.setView(view);

        TextView tvReqType, tvReason, tvHospitalName, tvPhoneNumber, tvMessage;

        tvReqType = view.findViewById(R.id.tvReqType);
        tvReason = view.findViewById(R.id.tvReason);
        tvHospitalName = view.findViewById(R.id.tvHospitalName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvMessage = view.findViewById(R.id.tvMessage);

        tvReqType.setText(arrayList.get(position).getBlood_request_type());
        tvReason.setText(arrayList.get(position).getReason());
        tvHospitalName.setText(arrayList.get(position).getAddress());
        tvPhoneNumber.setText(arrayList.get(position).getPhone_number_sec_person());
        tvMessage.setText(arrayList.get(position).getMessage());

        final Dialog dialog = builder.create();
        dialog.show();


        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calling(arrayList.get(position).getPhone_number_sec_person());
                dialog.dismiss();
            }
        });

    }


    private void calling(final String number) {

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
                    intent.setData(Uri.parse("tel:" + number));
                    context.startActivity(intent);

                } else if (which == 1) {

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:" + number));
                    context.startActivity(sendIntent);
                }

            }
        });
        builderSingle.show();

    }

    private void cancelingRequest(String req_id, final int position) {

        dialog = new Alert(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .cancelingRequest(req_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();

                String s = null;
                try {

                    if (response.code() == Constants.STATUS_OK) {

                        s = response.body().string();

//                        arrayList.remove(position);
//                        notifyItemRemoved(position);

//                        notifyDataSetChanged();

                        ((NewRequestsActivity) context).refershingData();

                    } else {
                        s = response.errorBody().string();
                    }

                    if (s != null) {

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


    private void cancellingReqAlert(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure you want to cancel this blood request?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelingRequest(arrayList.get(position).getId(), position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

}
