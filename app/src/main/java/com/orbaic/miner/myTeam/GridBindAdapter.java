package com.orbaic.miner.myTeam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orbaic.miner.R;
import com.orbaic.miner.common.Constants;

import java.util.List;

public class GridBindAdapter extends RecyclerView.Adapter<GridBindAdapter.ViewHolder> {

    Context context;
    List<Team> list;

    public GridBindAdapter(Context context, List<Team> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.grid_single_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(team.getImageUrl())
                .error(R.drawable.demo_avatar2)
                .into(holder.imageView);
        holder.userName.setText(team.getUserName());

        if (team.miningStatus.equals(Constants.STATUS_ON)) {
            holder.cvMininigStatus.setVisibility(View.VISIBLE);
        }
        else holder.cvMininigStatus.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView imageView;
        TextView userName;
        RelativeLayout layout;
        CardView cvMininigStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.circularImageTeam);
            userName = itemView.findViewById(R.id.tvUserName);
            cvMininigStatus = itemView.findViewById(R.id.cvMininigStatus);


        }
    }

}
