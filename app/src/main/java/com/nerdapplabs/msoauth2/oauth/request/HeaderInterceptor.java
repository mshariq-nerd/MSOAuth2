package com.nerdapplabs.msoauth2.oauth.request;

import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.utility.Preferences;
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
    private String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Properties properties = ReadProperties.getPropertiesValues();
        Request request = chain.request();
        if (null != accessToken) {
            request = request.newBuilder()
                    .header(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"))
                    .header(OAuthConstant.AUTHORIZATION, String.format("%s %s", OAuthConstant.BEARER, accessToken))
                    .build();
            return chain.proceed(request);
        }
        request = request.newBuilder()
                .header(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"))
                .build();

        return chain.proceed(request);
    }
}
