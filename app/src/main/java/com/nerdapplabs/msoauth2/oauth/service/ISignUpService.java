package com.nerdapplabs.msoauth2.oauth.service;

import com.nerdapplabs.msoauth2.oauth.request.SignUpRequest;
import com.nerdapplabs.msoauth2.oauth.response.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */

public interface ISignUpService {
    @POST("api/user/register")
    Call<SignUpResponse> signUp(@Body SignUpRequest signUpRequest);
}
