package com.nerdapplabs.msoauth2.oauth.response;

import com.google.gson.annotations.SerializedName;
import com.nerdapplabs.msoauth2.pojo.AccessToken;

public class SignUpResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("username")
    private String userName;

    @SerializedName("oauth")
    private AccessToken accessToken;

    @SerializedName("show_message")
    private String showMessage;


    public String getUserName() {
        return userName;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getShowMessage() {
        return showMessage;
    }

    public int getCode() {
        return code;
    }
}
