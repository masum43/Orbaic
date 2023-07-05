package com.orbaic.miner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ViewHolder>{

    Context context;
    ArrayList<ReferralDataRecive> list;

    public ReferralAdapter(Context context, ArrayList<ReferralDataRecive> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReferralAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemview_for_ref, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralAdapter.ViewHolder holder, int position) {
        ReferralDataRecive data = list.get(position);
        holder.name.setText(data.getName());

        if(data.getStatus().equals("active")){
            holder.status.setText("Inactive");
        }else {
            long time = Long.parseLong(data.getStatus());
            long current = System.currentTimeMillis();
            if (time<current){
                holder.status.setText("Inactive");
            }else{
                holder.status.setText("Active");
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_id_in_ref);
            status = itemView.findViewById(R.id.active_status);
        }
    }
}
