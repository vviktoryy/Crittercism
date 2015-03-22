package com.crittercism;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class WebViewActivity extends Activity {
    private WebView webview;
    private Button forwardButton;
    private Button backButton;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setView();
    }

    public void setView(){
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.google.com/");
        webview.setWebViewClient(new WebViewClient());

        //pd = ProgressDialog.show(WebViewActivity.this, "Progress Dialog", "Loading...");

        //webview.setWebViewClient(new WebClient());

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) {
                    setViewButtons();
                }
            }
        });

        Button networkButton = (Button) findViewById(R.id.networkButton);
        buttonEffect(networkButton);
        networkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        backButton = (Button) findViewById(R.id.backButton);
        buttonEffect(backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.goBack();
            }
        });

        forwardButton = (Button) findViewById(R.id.forwardButton);
        buttonEffect(forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.goForward();
            }
        });
    }

   /* class WebClient extends WebViewClient {  //Inner class
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setViewButtons();
            if(pd.isShowing()) pd.dismiss();
        }

    }*/

    private void setViewButtons(){
        if (webview.canGoBack()){
            backButton.setTextColor(0xff4381b3);
            backButton.setEnabled(true);
        }else{
            backButton.setTextColor(Color.GRAY);
            backButton.setEnabled(false);
        }
        if (webview.canGoForward()){
            forwardButton.setTextColor(0xff4381b3);
            forwardButton.setEnabled(true);
        }else{
            forwardButton.setTextColor(Color.GRAY);
            forwardButton.setEnabled(false);
        }
    }

    public void finish(){
        super.finish();
    }

    @Override
    protected void onResume(){
        overridePendingTransition(android.R.anim.fade_in, R.anim.abc_fade_out);
        super.onResume();
    }

    /*for click effect on transparent buttons putting*/
    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ((TextView)v).setTextColor(Color.GRAY);
                        v.invalidate();                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ((TextView)v).setTextColor(0xff4381b3);
                        v.invalidate();                        break;
                    }
                }
                return false;
            }
        });
    }
}
