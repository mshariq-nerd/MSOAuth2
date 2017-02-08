package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.oauth.response.ResetPasswordResponse;
import com.nerdapplabs.forumapp.pojo.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface IUserService {

    @POST("user/profile/show")
    Call<User> profile(@HeaderMap Map<String, String> headers);


    @GET("user/resetting/request/email")
    Call<ResetPasswordResponse> request(@Query("username") String username);
}
