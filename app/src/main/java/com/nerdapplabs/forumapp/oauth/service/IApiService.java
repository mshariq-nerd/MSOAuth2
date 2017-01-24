package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.oauth.response.ApiResponse;

import retrofit2.Callback;
import retrofit2.http.GET;

public interface IApiService {

    @GET("/")
    void getMessage(Callback<ApiResponse> responseCallback);

}
