package com.sequencing.androidoauth.core.importdata.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by omazurova on 3/16/2017.
 */

public class ResponseAncestry {

    @SerializedName("SessionId")
    public String sessionId;

    @SerializedName("StatusCode")
    public int statusCode;
}
