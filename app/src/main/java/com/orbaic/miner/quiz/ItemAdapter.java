package com.orbaic.miner.quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.orbaic.miner.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(HtmlCompat.fromHtml(item.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));

        // Set visibility of description based on item's expanded state
        if (item.isExpanded()) {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setAlpha(1.0f); // Set initial alpha to fully visible
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
            holder.descriptionTextView.setAlpha(0.0f); // Set initial alpha to fully transparent
        }

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());

            // Apply fade-in/fade-out animation
            if (item.isExpanded()) {
                holder.descriptionTextView.animate().alpha(1.0f).setDuration(300).start();
                holder.descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                holder.descriptionTextView.animate().alpha(0.0f).setDuration(300).start();
                holder.descriptionTextView.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
