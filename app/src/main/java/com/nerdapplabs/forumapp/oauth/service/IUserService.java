package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.pojo.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface IUserService {
    @GET("/users")
    Call<List<User>> user(@Header("Authorization") String header);


    @GET("/authoauth/web/api/user/profile/show")
    Call<User> profile(@Header("Authorization") String header);
}
