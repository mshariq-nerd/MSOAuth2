package com.nerdapplabs.forumapp.oauth.response;

public abstract class BaseResponse {

    private String error = null;
    private String error_description = null;

    public String getError() {
        return error;
    }

    public String getError_description() {

        return error_description;
    }

}
