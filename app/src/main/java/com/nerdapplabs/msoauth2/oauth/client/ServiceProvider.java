package com.nerdapplabs.msoauth2.oauth.client;

import android.webkit.URLUtil;

import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.HeaderInterceptor;
import com.nerdapplabs.msoauth2.oauth.service.IOauthService;
import com.nerdapplabs.msoauth2.pojo.AccessToken;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mohd. Shariq on 17/02/17.
 */
class ServiceProvider {
    static <S> S createService(Class<S> serviceClass) {
        try {
            String URL = ReadProperties.buildURL();
            Boolean isValid = URLUtil.isValidUrl(URL);
            if (URL.isEmpty() || !isValid) {
                return null;
            }
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
            httpClient.addNetworkInterceptor(new HeaderInterceptor());
            OkHttpClient client = httpClient.addInterceptor(interceptor).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            return retrofit.create(serviceClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    static <S> S createService(Class<S> serviceClass, final String accessToken) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        try {
            String API_BASE_URL = ReadProperties.buildURL();
            Boolean isValid = URLUtil.isValidUrl(API_BASE_URL);
            if (API_BASE_URL.isEmpty() || !isValid) {
                return null;
            }

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            if (accessToken != null) {
                httpClient.addInterceptor(new HeaderInterceptor());
                httpClient.authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (responseCount(response) >= 2) {
                            // If original call and the call with refresh token both failed,
                            // then don't try again.
                            return null;
                        }

                        final Properties properties = ReadProperties.getPropertiesValues();
                        final AccessToken accessTokenObj = new AccessToken();
                        accessTokenObj.setClientId(properties.getProperty("CLIENT_ID"));
                        accessTokenObj.setClientSecret(properties.getProperty("CLIENT_SECRET"));
                        accessTokenObj.setRefreshToken(Preferences.getString(OAuthConstant.REFRESH_TOKEN, null));

                        IOauthService iOauthService = createService(IOauthService.class);
                        if (null == iOauthService) {
                            return null;
                        }

                        Call<AccessToken> call = iOauthService.getRefreshAccessToken(accessTokenObj);
                        try {
                            retrofit2.Response<AccessToken> tokenResponse = call.execute();
                            if (tokenResponse.code() == 200) {
                                AccessToken newToken = tokenResponse.body();
                                Preferences.putString(OAuthConstant.ACCESS_TOKEN, newToken.getAccessToken());
                                Preferences.putString(OAuthConstant.REFRESH_TOKEN, newToken.getRefreshToken());
                                Preferences.putString(OAuthConstant.TOKEN_TYPE, newToken.getTokenType());
                                return response.request().newBuilder()
                                        .header(OAuthConstant.X_ACCEPT_VERSION, properties.getProperty("API_VERSION"))
                                        .header(OAuthConstant.AUTHORIZATION, String.format("%s %s", OAuthConstant.BEARER, newToken.getAccessToken()))
                                        .build();
                            } else {
                                return null;
                            }
                        } catch (IOException e) {
                            return null;
                        }
                    }
                });
            }

            OkHttpClient client = httpClient.addInterceptor(interceptor).build();
            Retrofit retrofit = builder.client(client).build();
            return retrofit.create(serviceClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}