package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.response.UserProfileResponse;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private TextView txtUserProfileName, txtUserName,
            txtUserEmail, txtUserDOB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtUserProfileName = (TextView) findViewById(R.id.txt_user_profile_name);
        txtUserName = (TextView) findViewById(R.id.txt_user_name);
        txtUserEmail = (TextView) findViewById(R.id.txt_user_email);
        txtUserDOB = (TextView) findViewById(R.id.txt_user_dob);

        new UserProfileAsyncTaskRunner().execute();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private class UserProfileAsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);
        UserProfileResponse userProfile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Read access token from preferences
                String accessToken = Preferences.getString("accessToken", null);
                userProfile = new UserProfileResponse();
                // Get user profile details
                userProfile = userProfile.getUserProfile(accessToken);
                Log.d(TAG, userProfile.getFirstname());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txtUserProfileName.setText(userProfile.getFirstname() + " " + userProfile.getLastname());
            txtUserName.setText(userProfile.getUsername());
            txtUserEmail.setText(userProfile.getEmail());
            progressDialog.dismiss();
        }
    }


}
