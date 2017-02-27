package com.nerdapplabs.msoauth2.oauth.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.oauth.service.IUserService;
import com.nerdapplabs.msoauth2.pojo.User;
import com.nerdapplabs.msoauth2.utility.Preferences;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.nerdapplabs.msoauth2.oauth.client.ServiceProvider.createService;

/**
 * Created by Mohd. Shariq on 23/01/17.
 */

public class UserServiceClient {
    /**
     * To get logged in user profile
     *
     * @return Object User
     * @throws IOException
     */
    public User getUserProfile() throws IOException {

        String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
        IUserService iUserService = createService(IUserService.class, accessToken);
        User user = new User();
        if (null == iUserService) {
            user.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return user;
        }
        Call<User> call = iUserService.profile();
        Response<User> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            BaseResponse baseResponse;
            try {
                baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                if (null != baseResponse && baseResponse.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                    user.setCode(baseResponse.getCode());
                } else if (null != baseResponse && baseResponse.getCode() == 0) {
                    user.setCode(OAuthConstant.HTTP_UNAUTHORIZED);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }


    /**
     * Method to update user profile
     *
     * @param user User  object to update
     * @return BaseResponse
     * @throws IOException
     */
    public BaseResponse updateProfile(User user) throws IOException {

        String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
        IUserService iUserService = createService(IUserService.class, accessToken);
        BaseResponse baseResponse = new BaseResponse();
        if (null == iUserService) {
            baseResponse.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return baseResponse;
        }
        Call<BaseResponse> call = iUserService.editProfile(user);
        Response<BaseResponse> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
        }
        return baseResponse;
    }


    /**
     * Method to send reset user password email
     *
     * @param userName String userName for password reset
     * @return BaseResponse
     * @throws IOException
     */
    public BaseResponse resetPassword(final String userName) throws IOException {
        IUserService iUserService = createService(IUserService.class);
        BaseResponse baseResponse = new BaseResponse();
        if (null == iUserService) {
            baseResponse.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return baseResponse;
        }
        Call<BaseResponse> call = iUserService.requestNewPassword(userName);
        Response<BaseResponse> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
            if (null != baseResponse && baseResponse.getCode() == 0) {
                baseResponse.setCode(OAuthConstant.HTTP_UNAUTHORIZED);
            }
        }
        return baseResponse;
    }

    /**
     * @param changePasswordRequest object with change password values
     * @return Success Response
     * @throws IOException
     */
    public BaseResponse changeOldPassword(ChangePasswordRequest changePasswordRequest) throws IOException {

        String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
        IUserService iUserService = createService(IUserService.class, accessToken);
        BaseResponse baseResponse = new BaseResponse();
        if (null == iUserService) {
            baseResponse.setCode(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
            return baseResponse;
        }
        Call<BaseResponse> call = iUserService.changeOldPassword(changePasswordRequest);
        Response<BaseResponse> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            Gson gson = new GsonBuilder().create();
            baseResponse = gson.fromJson(response.errorBody().string(), BaseResponse.class);
        }
        return baseResponse;
    }


    public void editProfilePic(String imagePath) throws IOException {
        String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
        File file = new File(imagePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/.jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        IUserService iUserService = createService(IUserService.class, accessToken);

        Call<ResponseBody> call = iUserService.editProfilePic(body);
        Response<ResponseBody> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            Log.d("EditProfilePic", response.body().toString());
        }
    }

    public Bitmap getProfilePic() throws IOException {
        String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
        IUserService iUserService = createService(IUserService.class, accessToken);
        Call<ResponseBody> call = iUserService.getProfilePic();
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return BitmapFactory.decodeStream(response.body().byteStream());
        }
        return null;
    }
}

