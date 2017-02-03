package com.nerdapplabs.forumapp.oauth.client;

import android.content.Context;
import android.util.Log;

import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.service.IOauthService;
import com.nerdapplabs.forumapp.pojo.AccessToken;

import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class OauthService {
    private IOauthService _oauthService;

    public IOauthService accessTokenService() throws IOException {
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getProperty("AUTHENTICATION_SERVER_URL"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _oauthService = retrofit.create(IOauthService.class);
        return _oauthService;
    }

    public IOauthService getApiService() {
        return _oauthService;
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
    public String getAccessToken(final Context context, String userName, String password) throws IOException {
        ReadForumProperties readForumProperties = new ReadForumProperties();
        Properties properties = readForumProperties.getPropertiesValues(context);
        AccessToken accessTokenRequest = new AccessToken();
        accessTokenRequest.setClientId(properties.getProperty("CLIENT_ID"));
        accessTokenRequest.setClientSecret(properties.getProperty("CLIENT_SECRET"));
        accessTokenRequest.setGrantType("password");
        accessTokenRequest.setUserName(userName);
        accessTokenRequest.setPassword(password);
        Call<AccessToken> call = accessTokenService().getAccessToken(accessTokenRequest);
        Response<AccessToken> response = call.execute();
        String accessToken = null;
        if (response.isSuccessful()) {
            // save access token in Preferences
            accessToken = response.body().getAccessToken();
        } else {
            Log.e("Error Code", String.valueOf(response.code()));
        }
        return accessToken;
    }
}