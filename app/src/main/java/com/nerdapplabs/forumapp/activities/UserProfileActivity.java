package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.response.UserResponse;
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

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        new UserProfileAsyncTaskRunner().execute();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private class UserProfileAsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);
        UserResponse user;

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
                user = new UserResponse();
                // Get user profile details
                user = user.getUserProfile(accessToken);
                Log.d(TAG, user.getFirstname());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txtUserProfileName.setText(user.getFirstname() + " " + user.getLastname());
            txtUserName.setText(user.getUsername());
            txtUserEmail.setText(user.getEmail());
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return super.onOptionsItemSelected(item);
    }

}
