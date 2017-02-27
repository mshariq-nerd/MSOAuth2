package com.nerdapplabs.msoauth2.oauth.service;

import com.nerdapplabs.msoauth2.pojo.AccessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */
public interface IOauthService {

    @POST("api/user/access/token")
    Call<AccessToken> getAccessToken(@Body AccessToken accessTokenRequest);

    @POST("api/user/refresh/token")
    Call<AccessToken> getRefreshAccessToken(@Body AccessToken accessTokenRequest);
}
