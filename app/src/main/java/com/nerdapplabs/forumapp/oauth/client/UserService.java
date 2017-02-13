package com.nerdapplabs.forumapp.oauth.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.forumapp.oauth.request.HeaderInterceptor;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.oauth.service.IUserService;
import com.nerdapplabs.forumapp.pojo.User;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by Mohd. Shariq on 23/01/17.
 */

public class UserService {
    private IUserService _userService;

    public IUserService userService() throws IOException {
        Properties properties = ReadForumProperties.getPropertiesValues();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addNetworkInterceptor(new HeaderInterceptor());
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getProperty("AUTHENTICATION_SERVER_URL"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _userService = retrofit.create(IUserService.class);
        return _userService;
    }

    /**
     * To get logged in user profile
     *
     * @param token AccessToken for valid user
     * @return Object User
     * @throws IOException
     */
    public User getUser(final String token) throws IOException {

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + " " + token);
        Call<User> call = userService().profile(headerMap);
        Response<User> response = call.execute();
        User user = new User();
        if (response.isSuccessful() && response.body() != null) {
            user = response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            BaseResponse baseResponse;
            try {
                baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                if (null != baseResponse && baseResponse.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                    user.setCode(baseResponse.getCode());
                } else if (null != baseResponse && baseResponse.getCode() == 0) {
                    user.setCode(OAuthConstant.HTTP_UNAUTHORIZED);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }


    /**
     * Method to update user profile
     *
     * @param user  User  object to update
     * @param token String AccessToken for network requestNewPassword
     * @return BaseResponse
     * @throws IOException
     */
    public BaseResponse updateProfile(User user, String token) throws IOException {

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + " " + token);
        Call<BaseResponse> call = userService().editProfile(headerMap, user);
        Response<BaseResponse> response = call.execute();
        BaseResponse baseResponse = new BaseResponse();
        if (response.isSuccessful() && response.body() != null) {
            baseResponse.setShowMessage(response.body().getShowMessage());
            baseResponse.setCode(response.body().getCode());
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
        }
        return baseResponse;
    }


    /**
     * Method to send reset user password email
     *
     * @param userName String userName for password reset
     * @return BaseResponse
     * @throws IOException
     */
    public BaseResponse resetPassword(final String userName) throws IOException {
        Call<BaseResponse> call = userService().requestNewPassword(userName);
        Response<BaseResponse> response = call.execute();
        BaseResponse baseResponse = new BaseResponse();
        if (response.isSuccessful() && response.body() != null) {
            baseResponse.setShowMessage(response.body().getShowMessage());
            baseResponse.setCode(response.body().getCode());
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
        }
        return baseResponse;
    }

    /**
     *
     * @param changePasswordRequest object with change password values
     * @return Success Response
     * @throws IOException
     */
    public BaseResponse changeOldPassword(ChangePasswordRequest changePasswordRequest) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + " " + Preferences.getString(OAuthConstant.ACCESS_TOKEN, null));
        Call<BaseResponse> call = userService().changeOldPassword(headerMap, changePasswordRequest);
        Response<BaseResponse> response = call.execute();
        BaseResponse baseResponse = new BaseResponse();
        if (response.isSuccessful() && response.body() != null) {
            baseResponse.setShowMessage(response.body().getShowMessage());
            baseResponse.setCode(response.body().getCode());
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
        }
        return baseResponse;
    }
}

