package com.nerdapplabs.forumapp.oauth.client;


import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.oauth.service.ISignUpService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpService {
    private ISignUpService _signUpService;

    public ISignUpService signUpService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OauthConstant.AUTHENTICATION_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _signUpService = retrofit.create(ISignUpService.class);
        return _signUpService;
    }
}
