package com.nerdapplabs.msoauth2.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nerdapplabs.msoauth2.MSOAuth2;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.client.OauthServiceClient;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txtForgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.login_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        edtUserName = (EditText) findViewById(R.id.edt_user_name);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        txtForgotPasswordLink = (TextView) findViewById(R.id.txt_link_forgot_password);
        btnLogin.setOnClickListener(this);
        txtForgotPasswordLink.setOnClickListener(this);

        // Handle password change messages through intent from ChangePasswordActivity
        Intent intent = getIntent();
        if (null != intent.getStringExtra("success_msg")) {
            MessageSnackbar.showMessage(LoginActivity.this, intent.getStringExtra("success_msg"), ErrorType.SUCCESS);
            intent.removeExtra("success_msg");
        } else if (null != intent.getStringExtra("failure_msg")) {
            MessageSnackbar.showMessage(LoginActivity.this, intent.getStringExtra("failure_msg"), ErrorType.ERROR);
            intent.removeExtra("failure_msg");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        MSOAuth2.getInstance().setConnectivityListener(this);
    }

    /**
     * Method to login into Application
     */
    public void login() {
        Log.d(TAG, "login");
        if (!validate()) {
            return;
        }
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    /**
     * Method for client side form data validation
     *
     * @return valid Boolean type for valid data
     */
    public boolean validate() {
        boolean valid = true;

        String userName = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();

        if (userName.isEmpty()) {
            edtUserName.setError(getString(R.string.username_validation_error));
            valid = false;
        } else {
            edtUserName.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtPassword.setError(getString(R.string.password_validation_error));
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
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
        NetworkConnectivity.showNetworkConnectMessage(LoginActivity.this, isConnected);
    }

    /**
     * Inner class for handling Async data loading
     */
    private class AsyncTaskRunner extends AsyncTask<String, Void, Boolean> {
        String userName = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        String accessToken = null;
        String responseMessage = getString(R.string.server_error);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideSoftKeyboard();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.login_authentication));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    // Api call for access token
                    responseMessage = new OauthServiceClient().getAccessToken(LoginActivity.this, userName, password);
                    // Read access token from preferences
                    accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
                    if (accessToken != null) {
                        Preferences.putString(OAuthConstant.USERNAME, userName);
                    }
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
                NetworkConnectivity.showNetworkConnectMessage(LoginActivity.this, false);
                return;
            }
            if (null == accessToken) {
                MessageSnackbar.showMessage(LoginActivity.this, responseMessage, ErrorType.ERROR);
                return;
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), LoginActionsActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btnLogin.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
