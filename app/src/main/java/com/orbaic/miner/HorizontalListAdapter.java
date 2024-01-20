package com.orbaic.miner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.myTeam.Team;

import java.util.List;

public class HorizontalListAdapter extends RecyclerView.Adapter<HorizontalListAdapter.ViewHolder> {

    List<Team> list;
    private Context context;

    public HorizontalListAdapter(Context context, List<Team> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_team, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Team team = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(team.getImageUrl())
                .error(R.drawable.demo_avatar2)
                .into(holder.imageView);
        holder.tvUserName.setText(team.getUserName());

        if (team.getMiningStatus().equals(Constants.STATUS_ON)) {
            holder.cvMininigStatus.setVisibility(View.VISIBLE);
        }
        else {
            holder.cvMininigStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView imageView;
        TextView tvUserName;
        CardView cvMininigStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.circularImageTeam);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            cvMininigStatus = itemView.findViewById(R.id.cvMininigStatus);

        }
    }
}
