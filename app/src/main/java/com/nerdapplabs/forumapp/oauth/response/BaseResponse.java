package com.nerdapplabs.forumapp.oauth.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("code")
    private int code = 0;

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

    public int getCode() {
        return code;
    }

    public String getShowMessage() {
        return showMessage;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }
}
