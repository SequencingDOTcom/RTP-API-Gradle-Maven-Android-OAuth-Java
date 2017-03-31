package com.sequencing.androidoauth.core.importdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by omazurova on 3/14/2017.
 */

public class Response23andMe {

    @SerializedName("SecurityQuestion")
    public String securityQuestion;

    @SerializedName("SessionId")
    public String sessionId;

    @SerializedName("StatusCode")
    public int statusCode;
}
