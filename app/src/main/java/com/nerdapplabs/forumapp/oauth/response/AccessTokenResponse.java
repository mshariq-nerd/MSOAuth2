package com.nerdapplabs.forumapp.oauth.response;

import android.content.Context;
import android.widget.Toast;

import com.nerdapplabs.forumapp.oauth.client.OauthService;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Response;

public class AccessTokenResponse extends BaseResponse {

    private String access_token;
    private String refresh_token;
    private int expires_in;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getToken_type() {
        return token_type;
    }


    @Override
    public String toString() {

        if (super.getError() != null && super.getError_description() != null) {
            return super.getError() + super.getError_description();
        }
        return "AccessToken{" +
                "accessToken='" + access_token + '\'' +
                ", tokenType='" + token_type + '\'' +
                ", expiresIn=" + expires_in +
                ", refreshToken='" + refresh_token + '\'' +
                '}';
    }

    /**
     * Method to get accessToken for api calls
     *
     * @param context  Context reference
     * @param userName String  name of logged in user
     * @param password String password for user login
     * @throws IOException
     */
    public void getAccessToken(final Context context, String userName, String password) throws IOException {

        // TODO: changes into POST  request
        OauthService service = new OauthService();
        ReadForumProperties readForumProperties = new ReadForumProperties();
        Properties properties = readForumProperties.getPropertiesValues(context);
        Map<String, String> data = new HashMap<>();
        data.put("client_id", properties.getProperty("CLIENT_ID"));
        data.put("client_secret", properties.getProperty("CLIENT_SECRET"));
        data.put("grant_type", "password");
        data.put("username", userName);
        data.put("password", password);
        Call<AccessTokenResponse> call = service.getAccessToken().getAccessToken(data);
        Response<AccessTokenResponse> response = call.execute();
        if (response.isSuccessful()) {
            if (response.body() == null) {
                Toast.makeText(context, call.execute().body().getError(), Toast.LENGTH_LONG).show();
            } else {
                // save access token in Preferences
                Preferences.putString("accessToken", response.body().getAccess_token());
            }
        }
    }

}
