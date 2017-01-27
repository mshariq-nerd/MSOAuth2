package com.nerdapplabs.forumapp.oauth.service;


import com.nerdapplabs.forumapp.oauth.response.AccessTokenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


public interface IOauthService {

    @GET("/authoauth/web/api/user/access/token")
    Call<AccessTokenResponse> getAccessToken(@QueryMap Map<String, String> options);
}
