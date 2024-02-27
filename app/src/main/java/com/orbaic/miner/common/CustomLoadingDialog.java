package com.orbaic.miner.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import com.orbaic.miner.databinding.LoadingLayoutBinding;

public class CustomLoadingDialog {
    private final Dialog dialog;

    public CustomLoadingDialog(Context context) {
        LoadingLayoutBinding binding = LoadingLayoutBinding.inflate(LayoutInflater.from(context), null, false);
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void start(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
