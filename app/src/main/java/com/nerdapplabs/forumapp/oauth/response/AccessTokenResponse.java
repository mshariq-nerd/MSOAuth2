package com.nerdapplabs.forumapp.oauth.response;

import android.content.Context;
import android.util.Log;

import com.nerdapplabs.forumapp.oauth.client.OauthService;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.request.AccessTokenRequest;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Response;

public class AccessTokenResponse extends BaseResponse {

    private String access_token;
    private String refresh_token;
    private int expires_in;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    @Override
    public String toString() {

        if (super.getError() != null && super.getError_description() != null) {
            return super.getError() + super.getError_description();
        }
        return "AccessToken{" +
                "accessToken='" + access_token + '\'' +
                ", tokenType='" + token_type + '\'' +
                ", expiresIn=" + expires_in +
                ", refreshToken='" + refresh_token + '\'' +
                '}';
    }

    /**
     * Method to get accessToken for a valid user
     *
     * @param context  Context reference
     * @param userName String  name of logged in user
     * @param password String password for user login
     * @return statusCode  String HTTP status code return by network call
     * @throws IOException
     */
    public int getAccessToken(final Context context, String userName, String password) throws IOException {
        OauthService service = new OauthService();
        ReadForumProperties readForumProperties = new ReadForumProperties();
        Properties properties = readForumProperties.getPropertiesValues(context);
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setClient_id(properties.getProperty("CLIENT_ID"));
        accessTokenRequest.setClient_secret(properties.getProperty("CLIENT_SECRET"));
        accessTokenRequest.setGrant_type("password");
        accessTokenRequest.setUsername(userName);
        accessTokenRequest.setPassword(password);
        Call<AccessTokenResponse> call = service.getAccessToken().getAccessToken(accessTokenRequest);
        Response<AccessTokenResponse> response = call.execute();
        int statusCode = 0;
        if (response.isSuccessful()) {
            if (response.body() == null) {
                statusCode = 0;
            } else {
                statusCode = response.code();
                // save access token in Preferences
                Preferences.putString("accessToken", response.body().getAccess_token());
            }
        } else {
            statusCode = response.code();
            Log.e("Error Code", String.valueOf(response.code()));
        }
        return statusCode;
    }

}
