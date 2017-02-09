package com.nerdapplabs.forumapp.oauth.client;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.request.HeaderInterceptor;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.oauth.response.ResetPasswordResponse;
import com.nerdapplabs.forumapp.oauth.service.IUserService;
import com.nerdapplabs.forumapp.pojo.User;

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

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class UserService {
    private IUserService _userService;

    public IUserService userService() throws IOException {
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addNetworkInterceptor(new HeaderInterceptor());
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
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
     * @param token
     * @return
     * @throws IOException
     */
    public User getUser(Activity activity, final String token) throws IOException {

        // TODO: Need to check how to pass multiple header values in HeaderInterceptor.java class
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + " " + token);
        headerMap.put(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"));

        Call<User> call = userService().profile(headerMap);
        Response<User> response = call.execute();
        User user = new User();
        if (response.isSuccessful() && response.body() != null) {
            user = response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            BaseResponse baseResponse;
            String message;
            try {
                baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                if (null != baseResponse && baseResponse.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                    message = activity.getString(R.string.server_error);
                    user.setShowMessage(message);
                } else {
                    message = baseResponse.getErrorDescription();
                    user.setShowMessage(message);
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
     * @param token String AccessToken for network request
     * @return BaseResponse
     * @throws IOException
     */
    public BaseResponse updateProfile(User user, String token) throws IOException {

        // TODO: Need to check how to pass multiple header values in HeaderInterceptor.java class
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(OAuthConstant.AUTHORIZATION, OAuthConstant.BEARER + " " + token);
        headerMap.put(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"));

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
     * Method to reset user password
     *
     * @param userName String userName for password reset
     * @return ResetPasswordResponse object of password reset
     * @throws IOException
     */

    public String resetPassword(final String userName) throws IOException {
        Call<ResetPasswordResponse> call = userService().request(userName);
        Response<ResetPasswordResponse> response = call.execute();
        String message = null;
        if (response.isSuccessful() && response.body() != null) {
            message = response.body().getMessage();
        } else {
            Gson gson = new GsonBuilder().create();
            BaseResponse baseResponse;
            try {
                baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                message = baseResponse.getShowMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }
}

