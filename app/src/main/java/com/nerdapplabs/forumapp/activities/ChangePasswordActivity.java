package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.utility.Duration;
import com.nerdapplabs.forumapp.utility.ErrorType;
import com.nerdapplabs.forumapp.utility.MessageSnackbar;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = (EditText) findViewById(R.id.edt_old_password);
        edtNewPassword = (EditText) findViewById(R.id.edt_new_password);
        edtConfirmPassword = (EditText) findViewById(R.id.edt_confirm_password);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);

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

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }


    private void changePassword() {
        if (!validate()) {
            return;
        } else {
            new ChangePasswordAsyncTaskRunner().execute();
        }
    }


    public boolean validate() {
        boolean valid = true;

        String oldPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (oldPassword.isEmpty() || oldPassword.length() < 4 || oldPassword.length() > 10) {
            edtOldPassword.setError(getString(R.string.password_validation_error));
            valid = false;
        } else {
            edtOldPassword.setError(null);
        }

        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            edtNewPassword.setError(getString(R.string.password_validation_error));
            valid = false;
        } else {
            edtNewPassword.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            edtConfirmPassword.setError(getString(R.string.password_validation_error));
            valid = false;
        } else {
            if (!confirmPassword.equals(newPassword)) {
                edtConfirmPassword.setError(getString(R.string.password_match_error));
                valid = false;
            } else {
                edtConfirmPassword.setError(null);
            }
        }

        if (newPassword.equals(oldPassword)) {
            edtNewPassword.setError(getString(R.string.password_duplicate_error));
            valid = false;
        } else {
            edtNewPassword.setError(null);
        }

        return valid;
    }

    /**
     * Inner class for handling Async data loading
     */
    private class ChangePasswordAsyncTaskRunner extends AsyncTask<String, Void, Boolean> {
        String oldPassword = edtOldPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        BaseResponse baseResponse;
        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnChangePassword.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            changePasswordRequest.setOldPassword(oldPassword);
            changePasswordRequest.setNewPassword(confirmPassword);
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    UserService userService = new UserService();
                    baseResponse = userService.changeOldPassword(changePasswordRequest);
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
                if (baseResponse != null) {
                    if (baseResponse.getCode() == OAuthConstant.HTTP_OK || baseResponse.getCode() == OAuthConstant.HTTP_CREATED) {
                        Preferences.clear();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("success_msg", baseResponse.getShowMessage());
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } else if (baseResponse.getCode() == OAuthConstant.HTTP_INTERNAL_SERVER_ERROR) {
                        MessageSnackbar.with(ChangePasswordActivity.this, null).type(ErrorType.ERROR)
                                .message(getString(R.string.server_error)).duration(Duration.LONG).show();
                    } else {
                        MessageSnackbar.with(ChangePasswordActivity.this, null).type(ErrorType.ERROR)
                                .message(baseResponse.getShowMessage()).duration(Duration.LONG).show();
                    }
                } else {
                    NetworkConnectivity.showNetworkConnectMessage(ChangePasswordActivity.this, false);
                }
            }
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
}
