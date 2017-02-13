package com.nerdapplabs.forumapp.activities;

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

import com.nerdapplabs.forumapp.MSOAuth2;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.utility.ErrorType;
import com.nerdapplabs.forumapp.utility.MessageSnackbar;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;

public class ResetPasswordActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener,
        View.OnClickListener {
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    private EditText edtUserName;
    private Button btnRestPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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

        edtUserName = (EditText) findViewById(R.id.edt_username);
        btnRestPassword = (Button) findViewById(R.id.btn_reset_password);
        btnRestPassword.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        MSOAuth2.getInstance().setConnectivityListener(this);
    }

    /**
     * Method for client side validation
     */
    public boolean validate() {
        boolean valid = true;

        String email = edtUserName.getText().toString();

        if (email.isEmpty() || email.length() < 4) {
            edtUserName.setError(getString(R.string.username_validation_error));
            valid = false;
        } else {
            edtUserName.setError(null);
        }


        return valid;
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_reset_password) {
            Log.d(TAG, "ResetPassword");
            if (!validate()) {
                return;
            } else {
                new ResetPasswordAsyncTaskRunner().execute();
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(this, isConnected);
    }

    private class ResetPasswordAsyncTaskRunner extends AsyncTask<Void, Void, Boolean> {
        final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this,
                R.style.AppTheme_Dark_Dialog);
        String userName = edtUserName.getText().toString();
        BaseResponse response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnRestPassword.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
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
                    response = new UserService().resetPassword(userName.trim());
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
                if (null != response) {
                    if (response.getCode() == OAuthConstant.HTTP_OK || response.getCode() == OAuthConstant.HTTP_CREATED) {
                        Intent intent = new Intent(getApplicationContext(), LoginActionsActivity.class);
                        intent.putExtra("EMAIL_SENT_MESSAGE", response.getShowMessage());
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } else if (response.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                        MessageSnackbar.showMessage(ResetPasswordActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                    } else if (response.getCode() == OAuthConstant.HTTP_UNAUTHORIZED) {
                        Preferences.clear();
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.putExtra("failure_msg", getString(R.string.session_expired_message));
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        finish();
                    } else {
                        MessageSnackbar.showMessage(ResetPasswordActivity.this, response.getShowMessage(), ErrorType.ERROR);
                    }
                } else {
                    MessageSnackbar.showMessage(ResetPasswordActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                }
            } else {
                NetworkConnectivity.showNetworkConnectMessage(ResetPasswordActivity.this, false);
            }
        }
    }
}
