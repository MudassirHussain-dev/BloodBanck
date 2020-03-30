package com.aampower.bloodbank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.model.RecentDonor;
import com.aampower.bloodbank.ui.AddBloodRequestActivity;
import com.aampower.bloodbank.ui.FullImageActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentDonorsAdapter extends RecyclerView.Adapter<RecentDonorsAdapter.ViewHolder> {

    private Activity context;
    private List<RecentDonor> donorList;

    private LayoutInflater inflater;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public RecentDonorsAdapter(Activity context, List<RecentDonor> donorList) {
        this.context = context;
        this.donorList = donorList;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recent_donors_list_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvRecentName.setText(donorList.get(position).getName());
        holder.tvRBloodGroup.setText(donorList.get(position).getBlood_group());

        Picasso.get()
                .load(donorList.get(position).getProfile_image())
                .resize(220, 220)
                .centerCrop()
                .into(holder.imgRecentUser);

    }


    @Override
    public int getItemCount() {
        return donorList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgRecentUser;
        TextView tvRecentName, tvRBloodGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecentUser = itemView.findViewById(R.id.imgRecentUser);
            tvRecentName = itemView.findViewById(R.id.tvRecentName);
            tvRBloodGroup = itemView.findViewById(R.id.tvRBloodGroup);

            imgRecentUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("imgURL", donorList.get(getAdapterPosition()).getProfile_image());
                    bundle.putString("accName", donorList.get(getAdapterPosition()).getName());

                    Intent intent = new Intent(context, FullImageActivity.class);

                    intent.putExtras(bundle);

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, imgRecentUser, ViewCompat.getTransitionName(imgRecentUser));

                    context.startActivity(intent, optionsCompat.toBundle());
                }
            });

        }
    }

}
