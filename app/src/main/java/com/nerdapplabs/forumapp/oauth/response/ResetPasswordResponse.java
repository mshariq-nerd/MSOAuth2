package com.nerdapplabs.forumapp.oauth.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mohd on 27/01/17.
 */

public class ResetPasswordResponse  extends  BaseResponse{
    @SerializedName("show_message")
    String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
