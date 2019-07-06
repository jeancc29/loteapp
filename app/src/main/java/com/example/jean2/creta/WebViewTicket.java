package com.example.jean2.creta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewTicket extends AppCompatActivity {
    public static WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_ticket);
        webView = (WebView)findViewById(R.id.webView);
    }
}
