package com.nerdapplabs.forumapp.oauth.client;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.request.HeaderInterceptor;
import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.oauth.response.SignUpResponse;
import com.nerdapplabs.forumapp.oauth.service.ISignUpService;
import com.nerdapplabs.forumapp.pojo.AccessToken;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.MSOAuth2.getContext;

public class SignUpService {
    private ISignUpService _signUpService;

    public ISignUpService signUpService() throws IOException {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addNetworkInterceptor(new HeaderInterceptor());
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        Properties properties = ReadForumProperties.getPropertiesValues();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getProperty("AUTHENTICATION_SERVER_URL"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _signUpService = retrofit.create(ISignUpService.class);
        return _signUpService;
    }


    /**
     * @param context
     * @param requestObject
     * @return
     * @throws IOException
     */
    public String registerUser(final Context context, SignUpRequest requestObject) throws IOException {
        ReadForumProperties readForumProperties = new ReadForumProperties();
        Properties properties = readForumProperties.getPropertiesValues();
        requestObject.setClientId(properties.getProperty("CLIENT_ID"));
        requestObject.setClientSecret(properties.getProperty("CLIENT_SECRET"));
        Call<SignUpResponse> call = signUpService().signUp(requestObject);
        Response<SignUpResponse> response = call.execute();
        String message = null;
        if (response.isSuccessful() && response.body() != null) {
            AccessToken token = response.body().getAccessToken();
            String userName = response.body().getUserName();
            // save access token and user name in Preferences
            Preferences.putString(OAuthConstant.ACCESS_TOKEN, token.getAccessToken());
            Preferences.putString("userName", userName);
            message = response.body().getShowMessage();
        } else {
            Gson gson = new GsonBuilder().create();
            BaseResponse baseResponse;
            try {
                baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                if (baseResponse.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                    message = context.getString(R.string.server_error);
                } else {
                    message = baseResponse.getShowMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }
}
