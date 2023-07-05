package com.orbaic.miner;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MiningRules extends Fragment {

    ProgressDialog progressDialog;


    public MiningRules() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mining_rules, container, false);

        WebView webView = view.findViewById(R.id.web);
        updateNotice();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getLoadsImagesAutomatically();
        //WebViewClient webViewClient =  new WebViewClient();
        // webView.setWebViewClient(webViewClient);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressDialog.show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();

            }
        });
        webView.loadUrl("https://blog.orbaic.com/Mining-rules.html");





        return view;
    }

    private void updateNotice() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.progressdialog_title));
        progressDialog.setMessage(getString(R.string.progressdialog_message));
    }
}