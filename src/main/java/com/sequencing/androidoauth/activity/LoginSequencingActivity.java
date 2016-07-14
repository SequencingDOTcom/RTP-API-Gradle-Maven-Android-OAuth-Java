package com.sequencing.androidoauth.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.sequencing.androidoauth.OauthWebViewClient;
import com.sequencing.androidoauth.R;
import com.sequencing.androidoauth.core.OAuth2Parameters;

/**
 * Activity realizes custom web-browser for Sequencing oAuth authentication
 */
public class LoginSequencingActivity extends  AppCompatActivity {

    /**
     * WebView for handling custom scheme and load data from sequencing.com
     */
    private static WebView oauthWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sequencing_login);


        OauthWebViewClient oauthWebViewClient = new OauthWebViewClient(this.getApplicationContext());

        oauthWebView = (WebView) findViewById(R.id.oauthWebView);
        oauthWebView.setWebViewClient(oauthWebViewClient);
        oauthWebView.setVisibility(View.GONE);
        oauthWebView.loadUrl(OAuth2Parameters.getInstance().getAppConfig().getRedirectUri());
    }

    public static WebView getOauthWebView() {
        return oauthWebView;
    }
}
