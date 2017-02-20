package com.nerdapplabs.msoauth2.oauth.client;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.oauth.service.IOauthService;
import com.nerdapplabs.msoauth2.pojo.AccessToken;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Response;

import static com.nerdapplabs.msoauth2.oauth.client.ServiceProvider.createService;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */

public class OauthServiceClient {

    /**
     * Method to get accessToken for a valid user
     *
     * @param context  Context reference
     * @param userName String  User name
     * @param password String password
     * @return statusCode  String HTTP status code return by network call
     * @throws IOException
     */
    public String getAccessToken(final Context context, String userName, String password) throws IOException {
        Properties properties = ReadProperties.getPropertiesValues();
        AccessToken accessTokenRequest = new AccessToken();
        accessTokenRequest.setClientId(properties.getProperty("CLIENT_ID"));
        accessTokenRequest.setClientSecret(properties.getProperty("CLIENT_SECRET"));
        accessTokenRequest.setGrantType(OAuthConstant.PASSWORD);
        accessTokenRequest.setUserName(userName);
        accessTokenRequest.setPassword(password);
        IOauthService iOauthService = createService(IOauthService.class);
        String message = null;
        if (null == iOauthService) {
            message = context.getString(R.string.server_not_found_error);
            return message;
        }
        Call<AccessToken> call = iOauthService.getAccessToken(accessTokenRequest);
        Response<AccessToken> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            // save access token in Preferences
            String accessToken = response.body().getAccessToken();
            Preferences.putString(OAuthConstant.ACCESS_TOKEN, accessToken);
            Preferences.putString(OAuthConstant.REFRESH_TOKEN, response.body().getRefreshToken());
            Preferences.putString(OAuthConstant.TOKEN_TYPE, response.body().getTokenType());
        } else {
            Gson gson = new GsonBuilder().create();
            BaseResponse baseResponse;
            try {
                baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                if (baseResponse.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                    message = context.getString(R.string.server_error);
                } else {
                    message = baseResponse.getShowMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }
}