package com.nerdapplabs.msoauth2.oauth.client;

import android.content.Context;
import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.HeaderInterceptor;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.oauth.service.IOauthService;
import com.nerdapplabs.msoauth2.pojo.AccessToken;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */

public class OauthService {

    public IOauthService accessTokenService() throws IOException {
        String URL = ReadProperties.buildURL();
        Boolean isValid = URLUtil.isValidUrl(URL);
        if (URL.isEmpty() || !isValid) {
            return null;
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addNetworkInterceptor(new HeaderInterceptor());
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        IOauthService _oauthService = retrofit.create(IOauthService.class);
        return _oauthService;
    }

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
        IOauthService iOauthService = accessTokenService();
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