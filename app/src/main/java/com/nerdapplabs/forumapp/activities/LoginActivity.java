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
import com.nerdapplabs.forumapp.oauth.client.OauthService;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.oauth.response.AccessTokenResponse;
import com.nerdapplabs.forumapp.oauth.response.UserResponse;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Response;

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

    /**
     * Method for client side validation
     */

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

    /**
     * Method for displaying the network connection status message
     */
    private void showErrorMessage(boolean isConnected) {
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
        protected Integer doInBackground(String... params) {
            int httpStatusCode = 0;
            if (NetworkConnectivity.isConnected()) {
                try {
                    AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
                    // Api call for access token
                    httpStatusCode = getAccessToken(LoginActivity.this, userName, password);
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
                txtEmailId.setText("");
                txtPassword.setText("");
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_layout);
                Snackbar.make(linearLayout, "Login failed. Try again", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method to get accessToken for a valid user
     *
     * @param context  Context reference
     * @param userName String  name of logged in user
     * @param password String password for user login
     * @return statusCode  String HTTP status code return by network call
     * @throws IOException
     */
    public int getAccessToken(final Context context, String userName, String password) throws IOException {
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
        int statusCode = 0;
        if (response.isSuccessful()) {
            if (response.body() == null) {
                statusCode = 0;
            } else {
                statusCode = response.code();
                // save access token in Preferences
                Preferences.putString("accessToken", response.body().getAccess_token());
            }
        } else {
            statusCode = response.code();
            Log.e("Error Code", String.valueOf(response.code()));
        }
        return statusCode;
    }

}
