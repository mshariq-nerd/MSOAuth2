package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.oauth.response.SignUpResponse;
import com.nerdapplabs.forumapp.pojo.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISignUpService {
    @POST("user/register")
    Call<SignUpResponse> signUp(@Body SignUpRequest signUpRequest);
}
