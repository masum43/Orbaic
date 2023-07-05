package com.orbaic.miner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WhitePaper extends Fragment {

    ProgressDialog progressDialog;

    public WhitePaper() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_white_paper, container, false);

        WebView webView = view.findViewById(R.id.white_paper);
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
        webView.loadUrl("https://blog.orbaic.com/whitePaper/white.html");






        return view;
    }

    private void updateNotice() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.progressdialog_title));
        progressDialog.setMessage(getString(R.string.progressdialog_message));
    }
}