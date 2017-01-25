package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.response.AccessTokenResponse;
import com.nerdapplabs.forumapp.oauth.response.UserResponse;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText txtUserName;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView txtForgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        txtUserName = (EditText) findViewById(R.id.edt_user_name);
        txtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        txtForgotPasswordLink = (TextView) findViewById(R.id.txt_link_forgot_password);
        btnLogin.setOnClickListener(this);
        txtForgotPasswordLink.setOnClickListener(this);
    }


    /**
     * Method to login into Application
     */
    public void login() {
        Log.d(TAG, "login");
        if (!validate()) {
            onLoginFailed();
            return;
        } else {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.login_error), Toast.LENGTH_LONG).show();
    }

    /**
     * Method for client side validation
     */

    public boolean validate() {
        boolean valid = true;

        String email = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.isEmpty() || email.length() < 4) {
            txtUserName.setError(getString(R.string.username_validation_error));
            valid = false;
        } else {
            txtUserName.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            txtPassword.setError(getString(R.string.username_validation_error));
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        return valid;
    }

    /**
     * Method for displaying the network connection status message
     */
    private void showErrorMessage(boolean isConnected) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_layout);
        String message;
        int color;
        if (isConnected) {
            message = getString(R.string.internet_connected);
            color = Color.WHITE;
        } else {
            message = getString(R.string.internet_connection_error);
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(linearLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    /**
     * OnClick method for handling user login and forgot password actions
     *
     * @param v View type for handling actions
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            login();
        } else {
            Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    /**
     * Method to check network connection status
     *
     * @param isConnected Boolean value
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showErrorMessage(isConnected);
    }

    /**
     * Inner class for handling Async data loading
     */
    private class AsyncTaskRunner extends AsyncTask<String, Void, Integer> {
        String userName = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnLogin.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int httpStatusCode = 0;
            if (NetworkConnectivity.isConnected()) {
                try {
                    AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
                    // Api call for access token
                    httpStatusCode = accessTokenResponse.getAccessToken(LoginActivity.this, userName, password);
                    // Read access token from preferences
                    String accessToken = Preferences.getString("accessToken", null);
                    if (httpStatusCode == 200 && accessToken != null) {
                        UserResponse userResponse = new UserResponse();
                        httpStatusCode = userResponse.login(accessToken);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showErrorMessage(false);
            }
            return httpStatusCode;
        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            super.onPostExecute(statusCode);
            progressDialog.dismiss();
            if (statusCode == 200) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            } else {
                txtUserName.setText("");
                txtPassword.setText("");
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_layout);
                Snackbar.make(linearLayout, getString(R.string.login_error), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
