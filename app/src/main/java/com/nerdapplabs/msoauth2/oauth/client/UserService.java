package com.nerdapplabs.msoauth2.oauth.client;

import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.msoauth2.oauth.request.HeaderInterceptor;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.oauth.service.IUserService;
import com.nerdapplabs.msoauth2.pojo.User;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    private IUserService userService() throws IOException, IllegalArgumentException {
        String URL = ReadProperties.buildURL();
        Boolean isValid = URLUtil.isValidUrl(URL);
        // To check if base URL is valid
        if (URL.isEmpty() || !isValid) {
            return null;
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addNetworkInterceptor(new HeaderInterceptor());
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(IUserService.class);
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
        IUserService iUserService = userService();
        User user = new User();
        if (null == iUserService) {
            user.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return user;
        }
        Call<User> call = iUserService.profile(headerMap);
        Response<User> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
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
        IUserService iUserService = userService();
        BaseResponse baseResponse = new BaseResponse();
        if (null == iUserService) {
            baseResponse.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return baseResponse;
        }
        Call<BaseResponse> call = iUserService.editProfile(headerMap, user);
        Response<BaseResponse> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
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
        IUserService iUserService = userService();
        BaseResponse baseResponse = new BaseResponse();
        if (null == iUserService) {
            baseResponse.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return baseResponse;
        }
        Call<BaseResponse> call = iUserService.requestNewPassword(userName);
        Response<BaseResponse> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
            if (null != baseResponse && baseResponse.getCode() == 0) {
                baseResponse.setCode(OAuthConstant.HTTP_UNAUTHORIZED);
            }
        }
        return baseResponse;
    }

    /**
     * @param changePasswordRequest object with change password values
     * @return Success Response
     * @throws IOException
     */
    public BaseResponse changeOldPassword(ChangePasswordRequest changePasswordRequest) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + " " + Preferences.getString(OAuthConstant.ACCESS_TOKEN, null));
        IUserService iUserService = userService();
        BaseResponse baseResponse = new BaseResponse();
        if (null == iUserService) {
            baseResponse.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return baseResponse;
        }
        Call<BaseResponse> call = iUserService.changeOldPassword(headerMap, changePasswordRequest);
        Response<BaseResponse> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
        }
        return baseResponse;
    }
}

