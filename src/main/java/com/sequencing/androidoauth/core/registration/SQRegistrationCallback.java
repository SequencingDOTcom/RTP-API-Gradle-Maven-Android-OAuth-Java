package com.sequencing.androidoauth.core.registration;

public interface SQRegistrationCallback {

    /**
     * Callback for handling success registration account
     * @param successMessage message of success registration account
     */
    void onSuccessRegistration(String successMessage);

    /**
     * Callback of handling failure registration account
     * @param message message of failure
     */
    void onFailedRegistration(String message);
}
