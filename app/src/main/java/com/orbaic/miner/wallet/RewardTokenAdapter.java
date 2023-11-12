package com.orbaic.miner.wallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.orbaic.miner.databinding.ItemRewardTokensBinding;

import java.util.ArrayList;
import java.util.List;

public class RewardTokenAdapter extends RecyclerView.Adapter<RewardTokenAdapter.MyViewHolder> {

    private List<RewardTokenItem> itemList;

    public RewardTokenAdapter() {
        this.itemList = new ArrayList<>();
    }

    public void updateItems(List<RewardTokenItem> newItems) {
        MyDiffCallback diffCallback = new MyDiffCallback(itemList, newItems);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        itemList.clear();
        itemList.addAll(newItems);

        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRewardTokensBinding binding = ItemRewardTokensBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RewardTokenItem item = itemList.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class MyDiffCallback extends DiffUtil.Callback {
        private final List<RewardTokenItem> oldList;
        private final List<RewardTokenItem> newList;

        public MyDiffCallback(List<RewardTokenItem> oldList, List<RewardTokenItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Implement your logic to check if items are the same based on unique identifiers
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            // Implement your logic to check if item contents are the same
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemRewardTokensBinding binding;

        public MyViewHolder(ItemRewardTokensBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(RewardTokenItem item) {
            binding.tvName.setText(item.getName());
            binding.tvCoin.setText(item.getCoin());
            Glide.with(binding.ivIcon.getContext()).load(item.icon).into(binding.ivIcon);

            binding.tvWithdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}
