package com.nerdapplabs.forumapp.oauth.client;


import android.content.Context;

import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.oauth.response.SignUpResponse;
import com.nerdapplabs.forumapp.oauth.service.ISignUpService;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class SignUpService {
    private ISignUpService _signUpService;

    public ISignUpService signUpService() throws IOException {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES).addInterceptor(interceptor).build();
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getProperty("AUTHENTICATION_SERVER_URL"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _signUpService = retrofit.create(ISignUpService.class);
        return _signUpService;
    }


    /**
     *
     * @param context
     * @param requestObject
     * @return
     * @throws IOException
     */
    public SignUpResponse registerUser(final Context context, SignUpRequest requestObject) throws IOException {
        ReadForumProperties readForumProperties = new ReadForumProperties();
        Properties properties = readForumProperties.getPropertiesValues(context);
        requestObject.setClientId(properties.getProperty("CLIENT_ID"));
        requestObject.setClientSecret(properties.getProperty("CLIENT_SECRET"));
        Call<SignUpResponse> call = signUpService().signUp(requestObject);
        Response<SignUpResponse> response = call.execute();
        return response.body();
    }
}
