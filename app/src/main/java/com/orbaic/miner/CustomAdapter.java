package com.orbaic.miner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    ArrayList<DataReturn> list;

    public CustomAdapter(Context context, ArrayList<DataReturn> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataReturn dataReturn = list.get(position);
        holder.number.setText(dataReturn.getNumber());
        holder.point.setText(dataReturn.getPoint());
        holder.date.setText(dataReturn.getDate());
        holder.method.setText(dataReturn.getMethod());
        holder.status.setText(dataReturn.getStatus());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView number, point, date, method, status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.item_number);
            point = itemView.findViewById(R.id.item_point);
            date = itemView.findViewById(R.id.item_date);
            method = itemView.findViewById(R.id.item_method);
            status = itemView.findViewById(R.id.item_status);
        }
    }
}
