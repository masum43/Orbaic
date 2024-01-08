package com.orbaic.miner;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import java.security.PrivateKey;

public class CommonWebview extends Fragment {
    ProgressDialog progressDialog;
    String url;

    public CommonWebview() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        WebView webView = view.findViewById(R.id.view_fqa);
        updateNotice();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getLoadsImagesAutomatically();
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        //WebViewClient webViewClient =  new WebViewClient();
        // webView.setWebViewClient(webViewClient);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;

/*                if (url.startsWith("fb://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true; // Return true to indicate the URL was handled
                    } catch (ActivityNotFoundException e) {
                        // Facebook app is not installed, handle this scenario if needed
                        // You may choose to open the link in a web browser as a fallback
                        return false;
                    }
                }*/
               /* if (url.startsWith("https://www.instagram.com/") || url.startsWith("http://instagram.com/")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user"));
                    intent.setPackage("com.instagram.android");

                    if (isAppInstalled(intent)) {
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                    return true;
                } else if (url.startsWith("https://twitter.com/") || url.startsWith("http://twitter.com/")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user"));
                    intent.setPackage("com.twitter.android");

                    if (isAppInstalled(intent)) {
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                    return true;
                } else if (url.startsWith("https://t.me/") || url.startsWith("http://t.me/")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve"));
                    intent.setPackage("org.telegram.messenger");

                    if (isAppInstalled(intent)) {
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                    return true;
                }*/
       /*         else {
                    // Load other URLs in the WebView itself
                    view.loadUrl(url);
                    return true; // Return true to indicate the URL was handled
                }*/
            }

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

        url = getArguments().getString("url");
        webView.loadUrl(url);







        return view;
    }

    private void updateNotice() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.progressdialog_title));
        progressDialog.setMessage(getString(R.string.progressdialog_message));
    }

    private boolean isAppInstalled(Intent intent) {
        return requireActivity().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null;
    }
}