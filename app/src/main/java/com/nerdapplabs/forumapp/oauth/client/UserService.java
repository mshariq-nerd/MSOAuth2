package com.nerdapplabs.forumapp.oauth.client;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.constant.OauthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.response.ErrorResponse;
import com.nerdapplabs.forumapp.oauth.response.ResetPasswordResponse;
import com.nerdapplabs.forumapp.oauth.service.IUserService;
import com.nerdapplabs.forumapp.pojo.User;

import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nerdapplabs.forumapp.ForumApplication.getContext;

public class UserService {
    private IUserService _userService;

    public IUserService userService() throws IOException {
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

    /**
     * To get logged in user profile
     *
     * @param token
     * @return
     * @throws IOException
     */
    public User getUser(Activity activity, final String token) throws IOException {
        Call<User> call = userService().profile(OauthConstant.BEARER + " " + token);
        Response<User> response = call.execute();
        User user = new User();
        if (response.isSuccessful() && response.body() != null) {
            user = response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            ErrorResponse errorResponse;
            String message;
            try {
                errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                if (null != errorResponse && errorResponse.getCode() == "500") {
                    message = activity.getString(R.string.login_error);
                    user.setShowMessage(message);
                } else {
                    message = errorResponse.getErrorDescription();
                    user.setShowMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }


    /**
     * Method to reset user password
     *
     * @param userName String userName for password reset
     * @return ResetPasswordResponse object of password reset
     * @throws IOException
     */
    public String resetPassword(final String userName) throws IOException {
        Call<ResetPasswordResponse> call = userService().request(userName);
        Response<ResetPasswordResponse> response = call.execute();
        String message = null;
        if (response.isSuccessful() && response.body() != null) {
            message = response.body().getMessage();
        } else {
            Gson gson = new GsonBuilder().create();
            ErrorResponse errorResponse;
            try {
                errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                message = errorResponse.getShowMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }
}

