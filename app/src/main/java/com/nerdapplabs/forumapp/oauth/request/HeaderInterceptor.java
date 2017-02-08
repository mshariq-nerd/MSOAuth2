package com.nerdapplabs.forumapp.oauth.request;

import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

/**
 * Created by Mohd Shariq on 08/02/17.
 */

public class HeaderInterceptor implements Interceptor {
    Properties properties;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Properties  properties = ReadForumProperties.getPropertiesValues(getContext());;
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader(OauthConstant.X_ACCEPT_VERSION, properties.getProperty("X_Accept_Version"))
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
