package com.sequencing.androidoauth.core.connectto;

/**
 * Created by omazurova on 3/24/2017.
 */

public class ConnectToSQParameters {

    /**
     * Define basic parameters for connection to Sequencing
     */
    private ConnectToSQ parameters;

    private static final ConnectToSQParameters instance = new ConnectToSQParameters();

    private ConnectToSQParameters(){}

    public static ConnectToSQParameters getInstance(){
        return instance;
    }

    public void setParameters(ConnectToSQ parameters) {
        ConnectToSQParameters.instance.parameters = parameters;
    }

    public ConnectToSQ getParameters(){
        return parameters;
    }
}
