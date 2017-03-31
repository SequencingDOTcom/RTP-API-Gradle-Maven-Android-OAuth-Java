package com.sequencing.androidoauth.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sequencing.androidoauth.R;
import com.sequencing.androidoauth.core.connectto.ConnectToSQ;
import com.sequencing.androidoauth.core.connectto.ConnectToSQParameters;
import com.sequencing.androidoauth.core.connectto.SQConnectHandler;
import com.sequencing.androidoauth.helper.AESUtil;
import com.sequencing.androidoauth.helper.Base64;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by omazurova on 3/20/2017.
 */

public class OpenWebViewActivity extends AppCompatActivity {

    private static WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sequencing_login);

        webView = (WebView) findViewById(R.id.oauthWebView);
        setWebClient();
        webView.loadUrl(getUrl());
    }

    private void setWebClient() {
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        WebChromeClient webClient = new WebChromeClient(){
            public void onCloseWindow(WebView w){
                super.onCloseWindow(w);
                SQConnectHandler.getConnectToSQCallback().onSuccessConnect("Success");
            }
        };
        webView.setWebChromeClient(webClient);
    }

    private String getUrl(){
        String url = "https://sequencing.com/connect?c=" + getHashMD5() + "&json=" + encryptJson(getJson());
        return url;
    }

    private String getHashMD5(){
        String hash = ConnectToSQParameters.getInstance().getParameters().getClientId();
        MessageDigest messageDigest;
        byte[] resultByte = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(hash.getBytes("UTF8"));
            resultByte = messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Hex.encodeHex(resultByte));
    }

    private String getJson() {
        ConnectToSQ connectToSQ = ConnectToSQParameters.getInstance().getParameters();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(connectToSQ);
    }

    private String encryptJson(String json){
        String clientId = ConnectToSQParameters.getInstance().getParameters().getClientId();
        String encrypted = AESUtil.encrypt(json, clientId);
        try {
             encrypted = URLEncoder.encode(encrypted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encrypted;
    }
}
