package com.nerdapplabs.forumapp.oauth.service;

import com.nerdapplabs.forumapp.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.oauth.response.ResetPasswordResponse;
import com.nerdapplabs.forumapp.pojo.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface IUserService {

    @POST("user/profile/show")
    Call<User> profile(@HeaderMap Map<String, String> headers);

    @POST("user/profile/edit")
    Call<BaseResponse> editProfile(@HeaderMap Map<String, String> headers, @Body User user);


    @GET("user/resetting/requestNewPassword/email")
    Call<ResetPasswordResponse> requestNewPassword(@Query("username") String username);

    @POST("user/change/password")
    Call<BaseResponse> changeOldPassword(@HeaderMap Map<String, String> headers, @Body ChangePasswordRequest user);
}
