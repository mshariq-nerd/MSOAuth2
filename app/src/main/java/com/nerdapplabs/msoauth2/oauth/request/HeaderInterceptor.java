package com.nerdapplabs.msoauth2.oauth.request;

import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mohd. Shariq on 08/02/17.
 */

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Properties properties = ReadProperties.getPropertiesValues();
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"))
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
