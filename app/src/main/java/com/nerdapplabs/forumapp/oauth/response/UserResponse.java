package com.nerdapplabs.forumapp.oauth.response;

import android.util.Log;

import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
     * Method to use for successful user login
     *
     * @param token String accessToken for valid user check
     * @return statusCode int value of http status code
     * @throws IOException
     */
    public int login(final String token) throws IOException {
        UserService userService = new UserService();
        Call<List<UserResponse>> call = userService.getUser().user(OauthConstant.BEARER + " " + token);
        Response<List<UserResponse>> response = call.execute();
        List<UserResponse> usersList = null;
        int statusCode = 0;
        if (response.isSuccessful()) {
            usersList = response.body();
            if (token != null) {
                Preferences.putString("userName", usersList.get(0).getUsername());
                Preferences.putString("email", usersList.get(0).getEmail());
                statusCode = response.code();
            }
        } else {
            Log.e("Error Code: login() -> ", String.valueOf(response.code()));
            statusCode = response.code();
        }
        return statusCode;
    }
}
