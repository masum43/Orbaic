package com.orbaic.miner.wallet;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.orbaic.miner.R;
import com.orbaic.miner.home.MyRewardedTokenItem;

public class RewardAdapter extends ListAdapter<MyRewardedTokenItem, RewardAdapter.RewardViewHolder> {
    private OnRewardItemClickListener onRewardItemClickListener;
    private static final DiffUtil.ItemCallback<MyRewardedTokenItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<MyRewardedTokenItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MyRewardedTokenItem oldItem, @NonNull MyRewardedTokenItem newItem) {
            return oldItem.getCode().equals(newItem.getCode()); // Implement your unique ID comparison logic
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MyRewardedTokenItem oldItem, @NonNull MyRewardedTokenItem newItem) {
            return oldItem.equals(newItem); // Implement your equals() method in MyRewardedTokenItem
        }
    };

    protected RewardAdapter(OnRewardItemClickListener onRewardItemClickListener) {
        super(DIFF_CALLBACK);
        this.onRewardItemClickListener = onRewardItemClickListener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_reward_tokens, parent, false);
        return new RewardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        MyRewardedTokenItem reward = getItem(position);
        holder.bind(reward);

        holder.tvWithdraw.setOnClickListener(v -> {
            onRewardItemClickListener.onRewardItemClick(getItem(position));
            holder.tvWithdraw.setEnabled(false);
            holder.tvWithdraw.setClickable(false);
            holder.tvWithdraw.setTextColor(ContextCompat.getColor(holder.tvWithdraw.getContext(), R.color.black_shadow));

            Drawable[] drawables = holder.tvWithdraw.getCompoundDrawablesRelative();
            Drawable drawable = drawables[0];
            if (drawable != null) {
                drawable = drawable.mutate();
                drawable.setTint(ContextCompat.getColor(holder.tvWithdraw.getContext(), R.color.black_shadow));
            }
        });
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvCoin;
        TextView tvWithdraw;
        ImageView ivIcon;

        RewardViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCoin = itemView.findViewById(R.id.tvCoin);
            tvWithdraw = itemView.findViewById(R.id.tvWithdraw);
            ivIcon = itemView.findViewById(R.id.ivIcon);

        }

        void bind(MyRewardedTokenItem reward) {
            tvName.setText(reward.getName());
            tvCoin.setText(reward.getBalance());
            Glide.with(ivIcon.getContext()).load(reward.getIcon()).into(ivIcon);

            tvWithdraw.setEnabled(false);
            tvWithdraw.setClickable(false);
            tvWithdraw.setTextColor(ContextCompat.getColor(tvWithdraw.getContext(), R.color.black_shadow));

            Drawable[] drawables = tvWithdraw.getCompoundDrawablesRelative();
            Drawable drawable = drawables[0];
            if (drawable != null) {
                drawable = drawable.mutate();
                drawable.setTint(ContextCompat.getColor(tvWithdraw.getContext(), R.color.black_shadow));
            }
        }
    }

    public interface OnRewardItemClickListener {
        void onRewardItemClick(MyRewardedTokenItem reward);
    }
}

