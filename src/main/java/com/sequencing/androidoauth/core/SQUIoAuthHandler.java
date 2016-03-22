package com.sequencing.androidoauth.core;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.sequencing.androidoauth.activity.LoginSequencingActivity;

import java.io.Serializable;

/**
 * Class determines basic action in relation to Sequencing.com
 */
public class SQUIoAuthHandler {

    /**
     * App context
     */
    private Context context;

    /**
     * User callback of authentication
     */
    private static ISQAuthCallback authCallback;

    public SQUIoAuthHandler(Context context){
        this.context = context;
    }

    /**
     * Authenticate user and and execute user callback
     * @param viewLogin authentication listener
     * @param authCallback user callback of authentication
     */
    public void authenticate(View viewLogin, final ISQAuthCallback authCallback){
        if (authCallback == null)
            throw new RuntimeException();
        this.authCallback = authCallback;

        viewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginSequencingActivity.class);
                context.startActivity(intent);
            }
        });
    }

    public static ISQAuthCallback getAuthCallback(){
        return authCallback;
    }
}
