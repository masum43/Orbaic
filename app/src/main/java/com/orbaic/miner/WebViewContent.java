package com.orbaic.miner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewContent extends AppCompatActivity {

    private ProgressDialog progressDialog;
    TextView textView;
    WebView webView;

    public static Intent createIntent(Context context, int id, String title,
                                      String excerpt, String content){
        Intent intent = new Intent(context, WebViewContent.class);
        //Setzen des wertes aus dem Intent
        intent.putExtra("postId", id);
        //intent.putExtra("featuredMedia",featuredMedia);
        intent.putExtra("postExcerpt", excerpt);
        intent.putExtra("postTitle", title);
        intent.putExtra("postContent",content);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_content);

        textView = findViewById(R.id.webViewContent);
        ImageView imageView = findViewById(R.id.backButton);
        webView = findViewById(R.id.webViewJson);

        String title =  getIntent().getSerializableExtra("postTitle").toString();
        String content = getIntent().getSerializableExtra("postContent").toString().replaceAll("\\\\n", "" +
                "").replaceAll("\\\\r", "").replaceAll("\\\\", "");;


        progressDialog = new ProgressDialog(WebViewContent.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");
        progressDialog.create();
        progressDialog.setCancelable(false);

        imageView.setOnClickListener(v->{
            super.onBackPressed();
        });

        initPost(title);
        initWebView(content);
        System.out.println(content);
    }

    private void initWebView(String content) {

        content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<script src=\"prism.js\"></script>" +
                "<div class=\"content\">" + content+ "</div>";

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
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

        webView.loadDataWithBaseURL("file:///android_asset/*",content, "text/html; charset=utf-8", "UTF-8", null);

    }

    private void initPost(String title) {

        textView.setText(title);
    }
}