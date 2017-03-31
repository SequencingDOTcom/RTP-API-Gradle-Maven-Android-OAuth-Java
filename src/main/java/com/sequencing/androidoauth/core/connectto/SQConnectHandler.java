package com.sequencing.androidoauth.core.connectto;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.sequencing.androidoauth.activity.OpenWebViewActivity;
import com.sequencing.androidoauth.core.ISQAuthCallback;

/**
 * Created by omazurova on 3/24/2017.
 */

public class SQConnectHandler {

    /**
     * Activity
     */
    Context context;

    /**
     * User callback of registration reset account
     */
    private static SQConnectCallback connectToSQCallback;

    public SQConnectHandler(Context context){
        this.context = context;
    }

    public void connectTo(Button connectTo, SQConnectCallback connectCallback, ConnectToSQ parameters){
        this.connectToSQCallback = connectCallback;
        ConnectToSQParameters.getInstance().setParameters(parameters);
        connectTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OpenWebViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public static SQConnectCallback getConnectToSQCallback(){
        return connectToSQCallback;
    }

}
