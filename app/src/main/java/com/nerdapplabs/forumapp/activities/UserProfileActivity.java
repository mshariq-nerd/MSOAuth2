package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nerdapplabs.forumapp.ForumApplication;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.pojo.User;
import com.nerdapplabs.forumapp.utility.Duration;
import com.nerdapplabs.forumapp.utility.ErrorType;
import com.nerdapplabs.forumapp.utility.MessageSnackbar;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.io.Serializable;

public class UserProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, View.OnClickListener {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private TextView txtUserProfileName, txtUserName,
            txtUserEmail, txtUserDOB, btnLogout;
    FloatingActionButton btnEditProfile;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtUserProfileName = (TextView) findViewById(R.id.txt_user_profile_name);
        txtUserName = (TextView) findViewById(R.id.txt_user_name);
        txtUserEmail = (TextView) findViewById(R.id.txt_user_email);
        txtUserDOB = (TextView) findViewById(R.id.txt_user_dob);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnEditProfile = (FloatingActionButton) findViewById(R.id.btn_edit_profile);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        btnLogout.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);

        new UserProfileAsyncTaskRunner().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        ForumApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(this, isConnected);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_logout) {
            logout();
        }

        if (view.getId() == R.id.btn_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("User", (Serializable) user);
            startActivity(intent);
            finish();
        }
    }

    private class UserProfileAsyncTaskRunner extends AsyncTask<Void, Void, Boolean> {
        final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    // Read access token from preferences
                    String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
                    UserService userService = new UserService();
                    // Get user profile details
                    user = userService.getUser(UserProfileActivity.this, accessToken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return isNetworkConnected;
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            super.onPostExecute(isConnected);
            progressDialog.dismiss();
            if (isConnected) {
                if (user != null && user.getUserName() != null) {
                    txtUserProfileName.setText(user.getFirstName() + " " + user.getLastName());
                    txtUserName.setText(user.getUserName());
                    txtUserEmail.setText(user.getEmailAddress());
                    txtUserDOB.setText(user.getDob());
                    MessageSnackbar.with(UserProfileActivity.this, null).type(ErrorType.SUCCESS).message(user.getShowMessage())
                            .duration(Duration.SHORT).show();
                } else {
                    MessageSnackbar.with(UserProfileActivity.this, null).type(ErrorType.ERROR).message(user.getShowMessage())
                            .duration(Duration.SHORT).show();
                }
            } else {
                NetworkConnectivity.showNetworkConnectMessage(UserProfileActivity.this, false);
            }
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


    private void logout() {
        Preferences.clear();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        Toast.makeText(getApplicationContext(), "Logout successfully", Toast.LENGTH_LONG).show();
    }

}
