package com.nerdapplabs.forumapp.oauth.response;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("code")
    private String code = null;

    @SerializedName("error")
    private String error = null;

    @SerializedName("error_description")
    private String errorDescription = null;

    @SerializedName("show_message")
    private String showMessage = null;

    public String getError() {
        return error;
    }

    public String getErrorDescription() {

        return errorDescription;
    }

    public String getCode() {
        return code;
    }

    public String getShowMessage() {
        return showMessage;
    }
}
