package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.oauth.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface IUserService {
    @GET("/users")
    Call<List<UserResponse>> user(@Header("Authorization") String header);
}
