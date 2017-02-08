package com.nerdapplabs.forumapp.oauth.request;

import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
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
        Properties properties = ReadForumProperties.getPropertiesValues(getContext());
        ;
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"))
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
