package com.orbaic.miner.common;

import android.app.ProgressDialog;
import android.content.Context;

public class Loading {
    private ProgressDialog progressDialog;

    public Loading(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    public void showLoadingDialog() {
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    public void updateProgress(int progress) {
        progressDialog.setProgress(progress);
    }

    public void closeLoadingDialog() {
        progressDialog.dismiss();
    }
}
