package com.orbaic.miner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_horizontal_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position != 0){
            holder.removableView.setVisibility(View.GONE);
        }
        Team team = list.get(position);
        Glide.with(holder.itemView.getContext())
                .load(team.getImageUrl())
                .error(R.drawable.demo_avatar2)
                .into(holder.imageView);
//        holder.tvUserName.setText(team.getUserName());

/*        if (team.getMiningStatus().equals(Constants.STATUS_ON)) {
            holder.iconOn.setVisibility(View.VISIBLE);
            holder.iconOff.setVisibility(View.GONE);
        }
        else {
            holder.iconOn.setVisibility(View.GONE);
            holder.iconOff.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View removableView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            removableView = itemView.findViewById(R.id.removableView);

        }
    }
}
