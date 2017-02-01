package com.nerdapplabs.forumapp.oauth.response;

import com.google.gson.annotations.SerializedName;
import com.nerdapplabs.forumapp.pojo.AccessToken;

public class SignUpResponse {
    private String username;
    private String msg;

    @SerializedName("access_token")
    private AccessToken accessToken;

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
}
