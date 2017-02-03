package com.nerdapplabs.forumapp.oauth.response;

import com.google.gson.annotations.SerializedName;
import com.nerdapplabs.forumapp.pojo.AccessToken;

public class SignUpResponse {

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
}
