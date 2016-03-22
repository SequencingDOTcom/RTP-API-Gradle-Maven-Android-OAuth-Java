package com.sequencing.androidoauth;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sequencing.androidoauth.core.SQUIoAuthHandler;
import com.sequencing.oauth.core.Token;
import com.sequencing.oauth.exception.*;
import com.sequencing.androidoauth.core.OAuth2Parameters;

/**
 * Implementation of WebViewClient that handler url request to authapp://
 */
public class OauthWebViewClient extends WebViewClient {

    /**
     * Context of app
     */
    private final Context context;

    /**
     * Authentication token
     */
    private Token token;

    private static final String TAG = "OauthWebViewClient";

    public OauthWebViewClient(Context context) {
        this.context = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /**
     * Handling error of unsupported scheme
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.d(TAG, "error code:" + errorCode + " description:" + description + " failingUrl:" + failingUrl);
        if(errorCode == ERROR_UNSUPPORTED_SCHEME)
            return;
    }

    /**
     * Callback for handling our scheme
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!url.startsWith("authapp://")) {
            return false;
        }

        onLoadResource(view, url);
        return true;
    }

    /**
     * Callback for handling our scheme
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        if (url.startsWith("authapp://")) { // if host is "authapp:/"

            // If we don't authorized
            if (!OAuth2Parameters.getInstance().getOauth().isAuthorized()) {
                if (null == Uri.parse(url).getQueryParameter("code")) {
                    // We just being the oauth2 authorization loop. So we redirect the client to
                    // Sequencing website and ask the user to allow our app to use his data.
                    String loginRedirectUrl = OAuth2Parameters.getInstance().getOauth().getLoginRedirectUrl();
                    view.loadUrl(loginRedirectUrl);

                } else {
                    // We came back from Sequencing website and if state argument matches with our
                    // state, then we proceed and exchange the authorization code that we are
                    // given in GET for the access and refresh tokens. The former will be used for
                    // authorization, when we make requests to Sequencing API.
                    try {
                        token = OAuth2Parameters.getInstance().getOauth().authorize(Uri.parse(url).getQueryParameter("code"),
                                Uri.parse(url).getQueryParameter("state"));
                    } catch (BasicAuthenticationFailedException e) {

                        Log.e(TAG, "An unsuccessful attempt to query to the API server", e);
                        Toast.makeText(context, "An unsuccessful attempt to query to the API server", Toast.LENGTH_SHORT).show();
                        SQUIoAuthHandler.getAuthCallback().onFailedAuthentication(e);
                        return;
                    }
                    SQUIoAuthHandler.getAuthCallback().onAuthentication(token);
                }
            }
            else {
                SQUIoAuthHandler.getAuthCallback().onAuthentication(token);
            }
        }
        super.onLoadResource(view, url);
    }
}