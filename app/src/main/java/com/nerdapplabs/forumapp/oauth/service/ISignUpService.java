package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.oauth.response.SignUpResponse;

import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISignUpService {
    @POST("/signup")
    void signUp(@Body SignUpRequest signUpRequest,
                Callback<SignUpResponse> signUpResponseCallback);
}
