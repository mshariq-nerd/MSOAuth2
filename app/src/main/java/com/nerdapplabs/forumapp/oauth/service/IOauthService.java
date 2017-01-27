package com.nerdapplabs.forumapp.oauth.service;


import com.nerdapplabs.forumapp.oauth.request.AccessTokenRequest;
import com.nerdapplabs.forumapp.oauth.response.AccessTokenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


public interface IOauthService {

    @POST("/authoauth/web/api/user/access/token")
    Call<AccessTokenResponse> getAccessToken(@Body AccessTokenRequest accessTokenRequest);
}
