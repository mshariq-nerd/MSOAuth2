package com.nerdapplabs.msoauth2.oauth.service;

import com.nerdapplabs.msoauth2.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.pojo.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mohd. Shariq on 01/02/17.
 */

public interface IUserService {

    @POST("user/profile/show")
    Call<User> profile();

    @POST("user/profile/edit")
    Call<BaseResponse> editProfile(@Body User user);


    @GET("user/resetting/request")
    Call<BaseResponse> requestNewPassword(@Query("username") String username);

    @POST("user/profile/change-password")
    Call<BaseResponse> changeOldPassword(@Body ChangePasswordRequest user);
}
