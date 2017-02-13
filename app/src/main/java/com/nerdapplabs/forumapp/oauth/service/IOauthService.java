package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.pojo.AccessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */
public interface IOauthService {

    @POST("user/access/token")
    Call<AccessToken> getAccessToken(@Body AccessToken accessTokenRequest);
}
