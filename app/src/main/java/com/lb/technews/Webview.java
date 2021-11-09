package com.lb.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = (WebView) findViewById(R.id.webview);
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("a_url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }


//    WebView webView = (WebView) findViewById(R.id.webview);
//                        webView.getSettings().setJavaScriptEnabled(true);
//                        webView.setWebViewClient(new WebViewClient());
//                        webView.loadUrl(a_url);
}