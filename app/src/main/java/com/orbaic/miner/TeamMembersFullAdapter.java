package com.orbaic.miner;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.myTeam.GridBindAdapter;
import com.orbaic.miner.myTeam.Team;

import java.util.List;
import java.util.Random;

public class TeamMembersFullAdapter extends RecyclerView.Adapter<TeamMembersFullAdapter.ViewHolder> {

    Context context;
    List<Team> list;

    public TeamMembersFullAdapter(Context context, List<Team> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_member_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(team.getImageUrl())
                .error(R.drawable.demo_avatar2)
                .into(holder.ivProfile);
        holder.tvUserName.setText(team.getUserName());

        if (team.getMiningStatus().equals(Constants.STATUS_ON)) {
            holder.iconOn.setVisibility(View.VISIBLE);
            holder.iconOff.setVisibility(View.GONE);
        }
        else {
            holder.iconOn.setVisibility(View.GONE);
            holder.iconOff.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfile, iconOn, iconOff;
        TextView tvUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            iconOn = itemView.findViewById(R.id.statusOnIcon);
            iconOff = itemView.findViewById(R.id.statusOffIcon);
        }
    }
}
