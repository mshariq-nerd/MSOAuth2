package com.nerdapplabs.forumapp.oauth.client;


import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.service.IApiService;

import java.io.IOException;
import java.util.Properties;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class ApiService {
    private IApiService _apiService;

    public IApiService getMessage()  throws IOException {
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getProperty("AUTHENTICATION_SERVER_URL"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _apiService = retrofit.create(IApiService.class);
        return _apiService;
    }
}


