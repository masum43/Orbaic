package com.orbaic.miner;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

public class TeamMembersFullAdapter extends RecyclerView.Adapter<TeamMembersFullAdapter.ViewHolder> {

    Context context;

    public TeamMembersFullAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_member_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Random random = new Random();
        int ran = random.nextInt();
        if(ran % 2 == 0){
            Log.d("TAG", "onBindViewHolder: "+ ran);
            holder.iconOn.setVisibility(View.VISIBLE);
            holder.iconOff.setVisibility(View.GONE);
        } else {
            holder.iconOn.setVisibility(View.GONE);
            holder.iconOff.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconOn, iconOff;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconOn = itemView.findViewById(R.id.statusOnIcon);
            iconOff = itemView.findViewById(R.id.statusOffIcon);
        }
    }
}
