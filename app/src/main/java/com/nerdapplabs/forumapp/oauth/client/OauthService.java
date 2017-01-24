package com.nerdapplabs.forumapp.oauth.client;


import com.nerdapplabs.forumapp.ForumApplication;
import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.service.IOauthService;

import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class OauthService {
    private IOauthService _oauthService;
    ReadForumProperties readForumProperties = new ReadForumProperties();
    Properties properties = readForumProperties.getPropertiesValues(getContext());

    public OauthService() throws IOException {
    }


    public IOauthService getAccessToken() {
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
}