package com.nerdapplabs.forumapp.oauth.client;

import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.service.IUserService;

import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class UserService {
    private IUserService _userService;

    public IUserService getUser() throws IOException {
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getProperty("AUTHENTICATION_SERVER_URL"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _userService = retrofit.create(IUserService.class);
        return _userService;
    }
}

