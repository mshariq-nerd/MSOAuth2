package com.nerdapplabs.forumapp.oauth.response;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.nerdapplabs.forumapp.oauth.client.SignUpService;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Response;

public class SignUpResponse {
    private String username;
    private String msg;

    @SerializedName("authRtn")
    private AccessTokenResponse accessTokenResponse;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    /**
     * Method to get accessToken for a valid user
     *
     * @param context Context reference
     * @return statusCode  String HTTP status code return by network call
     * @throws IOException
     */
    public int getSignupUser(final Context context, SignUpRequest requestObject) throws IOException {
        SignUpService service = new SignUpService();
        ReadForumProperties readForumProperties = new ReadForumProperties();
        Properties properties = readForumProperties.getPropertiesValues(context);
        requestObject.setClient_id(properties.getProperty("CLIENT_ID"));
        requestObject.setClient_secret(properties.getProperty("CLIENT_SECRET"));
        Call<SignUpResponse> call = service.signUpService().signUp(requestObject);
        Response<SignUpResponse> response = call.execute();
        int statusCode = 0;
        if (response.isSuccessful()) {
            if (response.body() == null) {
                statusCode = 0;
            } else {
                statusCode = response.code();
                if (null != response.body().accessTokenResponse) {
                    String accessToken = response.body().accessTokenResponse.getAccess_token();
                    String userName = response.body().getUsername();
                    Log.e("Access Token", accessToken);
                    // save access token and user name in Preferences
                    Preferences.putString("accessToken", accessToken);
                    Preferences.putString("userName", userName);
                } else {
                    statusCode = 0;
                }
            }
        } else {
            statusCode = response.code();
            Log.e("Error Code", String.valueOf(response.code()));
        }
        return statusCode;
    }
}
