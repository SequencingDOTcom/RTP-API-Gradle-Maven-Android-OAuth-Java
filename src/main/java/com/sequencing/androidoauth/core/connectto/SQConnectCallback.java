package com.sequencing.androidoauth.core.connectto;

/**
 * Created by omazurova on 3/24/2017.
 */

public interface SQConnectCallback {

    /**
     * Callback for handling success connection to Sequencing
     * @param successMessage message of success connection to Sequencing
     */
    void onSuccessConnect(String successMessage);

    /**
     * Callback of handling failure connection to Sequencing
     * @param message message of failure
     */
    void onFailedConnect(String message);
}
