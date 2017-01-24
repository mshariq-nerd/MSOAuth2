package com.nerdapplabs.forumapp.oauth.response;

import android.app.Activity;

import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class UserResponse extends BaseResponse {
    private String username;
    private String email;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {

        if (super.getError() != null && super.getError_description() != null) {
            return super.getError() + super.getError_description();
        }
        return "user{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }


    /**
     * This is the method for getting logged in user
     *
     * @param token The access token for api call
     */
    public String login(Activity activity, final String token) throws IOException {
        UserService userService = new UserService();
        Call<List<UserResponse>> call = userService.getUser().user(OauthConstant.BEARER + " " + token);
        List<UserResponse> usersList = call.execute().body();
        if (token != null) {
            Preferences.putString("userName", usersList.get(0).getUsername());
            Preferences.putString("email", usersList.get(0).getEmail());
        }
        return usersList.get(0).getUsername();
    }
}
