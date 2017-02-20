package com.nerdapplabs.msoauth2.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nerdapplabs.msoauth2.MSOAuth2;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.client.UserServiceClient;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.pojo.User;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;

import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener,
        View.OnClickListener {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private TextView txtUserProfileName, txtUserName,
            txtUserEmail, txtUserDOB;
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
        btnEditProfile = (FloatingActionButton) findViewById(R.id.btn_edit_profile);

        Button btnLogout = (Button) findViewById(R.id.btn_logout);
        TextView btnChangePassword = (TextView) findViewById(R.id.btn_change_password);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.user_profile_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnLogout.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);

        new UserProfileAsyncTaskRunner().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        MSOAuth2.getInstance().setConnectivityListener(this);
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
            intent.putExtra("User", user);
            startActivity(intent);
            finish();
        }

        if (view.getId() == R.id.btn_change_password) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
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
                    // Get user profile details
                    user = new UserServiceClient().getUserProfile(accessToken);
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
            if (!isConnected) {
                NetworkConnectivity.showNetworkConnectMessage(UserProfileActivity.this, false);
                return;
            }

            switch (user.getCode()) {
                case OAuthConstant.HTTP_INTERNAL_SERVER_ERROR:
                    MessageSnackbar.showMessage(UserProfileActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_UNAUTHORIZED:
                    Preferences.clear();
                    pageNavigationActions(OAuthConstant.HTTP_UNAUTHORIZED);
                    break;
                case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                    Preferences.clear();
                    pageNavigationActions(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
                    break;
                case OAuthConstant.HTTP_OK:
                case OAuthConstant.HTTP_CREATED:
                    updateUserProfile();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            pageNavigationActions(id);

        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_alert_message);
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, " " + which);
                Preferences.clear();
                pageNavigationActions(which);
            }
        });
        builder.setNegativeButton(R.string.alert_cancel, null);
        builder.show();
    }

    private void pageNavigationActions(int code) {
        Intent intent;
        switch (code) {
            case OAuthConstant.HTTP_UNAUTHORIZED:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("failure_msg", getString(R.string.session_expired_message));
                break;
            case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("failure_msg", getString(R.string.server_not_found_error));
                break;
            default:
                intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void updateUserProfile() {
        txtUserProfileName.setText(user.getFirstName() + " " + user.getLastName());
        txtUserName.setText(user.getUserName());
        txtUserEmail.setText(user.getEmailAddress());
        txtUserDOB.setText(user.getDob());
        MessageSnackbar.showMessage(UserProfileActivity.this, user.getShowMessage(), ErrorType.SUCCESS);
    }
}