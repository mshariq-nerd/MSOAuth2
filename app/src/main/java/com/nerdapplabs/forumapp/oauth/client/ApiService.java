package com.nerdapplabs.forumapp.oauth.client;


import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.oauth.service.IApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private IApiService _apiService;

    public IApiService getMessage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OauthConstant.AUTHENTICATION_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _apiService = retrofit.create(IApiService.class);
        return _apiService;
    }
}


