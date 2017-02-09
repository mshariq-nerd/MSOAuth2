package com.nerdapplabs.forumapp.oauth.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mohd on 27/01/17.
 */

public class ResetPasswordResponse {

    @SerializedName("code")
    int code;

    @SerializedName("show_message")
    String message;

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
