package com.nerdapplabs.msoauth2.oauth.client;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.SignUpRequest;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.oauth.response.SignUpResponse;
import com.nerdapplabs.msoauth2.oauth.service.ISignUpService;
import com.nerdapplabs.msoauth2.pojo.AccessToken;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Response;

import static com.nerdapplabs.msoauth2.oauth.client.ServiceProvider.createService;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */
public class SignUpServiceClient {
    /**
     * Method for new user registration
     *
     * @param context       Context for resource string reference
     * @param requestObject SignUpRequest parameters objects
     * @return Message String success message
     * @throws IOException
     */
    public String registerUser(final Context context, SignUpRequest requestObject) throws IOException {
        Preferences.clear();  // clear preference
        Properties properties = ReadProperties.getPropertiesValues();
        requestObject.setClientId(properties.getProperty("CLIENT_ID"));
        requestObject.setClientSecret(properties.getProperty("CLIENT_SECRET"));
        ISignUpService iSignUpService = createService(ISignUpService.class);
        String message = null;
        if (null == iSignUpService) {
            message = context.getString(R.string.server_not_found_error);
            return message;
        }
        Call<SignUpResponse> call = iSignUpService.signUp(requestObject);
        Response<SignUpResponse> response = call.execute();
        if (response.isSuccessful()) {
            if ((response.body().getCode() == OAuthConstant.HTTP_OK) || (response.body().getCode() == OAuthConstant.HTTP_CREATED)) {
                AccessToken token = response.body().getAccessToken();
                String userName = response.body().getUserName();
                // save access token and user name in Preferences
                Preferences.putString(OAuthConstant.ACCESS_TOKEN, token.getAccessToken());
                Preferences.putString(OAuthConstant.REFRESH_TOKEN, token.getRefreshToken());
                Preferences.putString(OAuthConstant.USERNAME, userName);
                message = response.body().getShowMessage();
            } else {
                return response.body().getShowMessage();
            }
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
