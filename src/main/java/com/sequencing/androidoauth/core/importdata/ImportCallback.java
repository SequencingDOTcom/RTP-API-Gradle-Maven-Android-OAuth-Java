package com.sequencing.androidoauth.core.importdata;

/**
 * Created by omazurova on 3/27/2017.
 */

public interface ImportCallback {

    /**
     * Callback for handling success starting import data
     * @param successMessage message of success starting import data
     */
    void onSuccessImportStarted(String successMessage);

    /**
     * Callback of handling failure starting import data
     * @param message message of failure
     */
    void onFailedImport(String message);

}
