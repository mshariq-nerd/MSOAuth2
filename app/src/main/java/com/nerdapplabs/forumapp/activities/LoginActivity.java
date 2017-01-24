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
    private EditText txtEmailId;
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
        txtEmailId = (EditText) findViewById(R.id.edt_email);
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
        Toast.makeText(getBaseContext(), "login failed.", Toast.LENGTH_LONG).show();
    }


    public boolean validate() {
        boolean valid = true;

        String email = txtEmailId.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.isEmpty() || email.length() < 4) {
            txtEmailId.setError("enter a valid email address");
            valid = false;
        } else {
            txtEmailId.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            txtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        return valid;
    }

    // Showing the network status
    private void showNetworkErrorMsg(boolean isConnected) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_layout);
        String message;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showNetworkErrorMsg(isConnected);
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {
        String userName = txtEmailId.getText().toString();
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
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if (NetworkConnectivity.isConnected()) {
                AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
                try {
                    accessTokenResponse.getAccessToken(LoginActivity.this, userName, password);
                    String accessToken = Preferences.getString("accessToken", null);
                    if (accessToken != null) {
                        UserResponse userResponse = new UserResponse();
                        userName = userResponse.login(LoginActivity.this, accessToken);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return userName;
            } else {
                showNetworkErrorMsg(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            progressDialog.dismiss();
            if (name != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        }
    }
}
