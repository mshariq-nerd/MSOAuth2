package com.nerdapplabs.forumapp.oauth.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohd. Shariq on 09/02/17.
 */

public class ChangePasswordRequest {

    @SerializedName("old_password")
    private String oldPassword;

    @SerializedName("password")
    private String newPassword;

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
