package com.nerdapplabs.forumapp.oauth.response;

import android.util.Log;

import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by mohd on 26/01/17.
 */

public class UserProfileResponse {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String dob;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * To get logged in user profile
     *
     * @param token
     * @return
     * @throws IOException
     */
    public UserProfileResponse getUserProfile(final String token) throws IOException {
        UserProfileResponse userProfileObject = null;
        UserService userService = new UserService();
        Call<UserProfileResponse> call = userService.getUserProfile().profile(OauthConstant.BEARER + " " + token);
        Response<UserProfileResponse> response = call.execute();
        if (response.isSuccessful()) {
            userProfileObject = new UserProfileResponse();
            userProfileObject.setFirstname(response.body().getFirstname());
            userProfileObject.setLastname(response.body().getLastname());
            userProfileObject.setUsername(response.body().getUsername());
            // TODO: Need to fix api for correct json object format
            //userProfileObject.setDob(response.body().getDob());
            userProfileObject.setEmail(response.body().getEmail());
        } else {
            Log.e("Error in profile()", String.valueOf(response.code()));
        }

        return userProfileObject;
    }
}
