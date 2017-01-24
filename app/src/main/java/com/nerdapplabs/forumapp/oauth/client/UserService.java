package com.nerdapplabs.forumapp.oauth.client;

import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.oauth.service.IUserService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService {
    private IUserService _userService;

    public IUserService getUser() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OauthConstant.AUTHENTICATION_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _userService = retrofit.create(IUserService.class);
        return _userService;
    }
}
