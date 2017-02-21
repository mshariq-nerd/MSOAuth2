package com.nerdapplabs.msoauth2.activities;

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
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.client.UserServiceClient;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.ChangePasswordRequest;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;

import java.io.IOException;
import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    @Order(1)
    private EditText edtOldPassword;

    @Password(messageResId = R.string.password_validation_error, scheme = Password.Scheme.ALPHA_NUMERIC)
    @Order(2)
    private EditText edtNewPassword;

    @ConfirmPassword
    @Order(3)
    private EditText edtConfirmPassword;

    private Button btnChangePassword;
    Validator validator;

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
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.change_password_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void changePassword() {
        // form validation
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        new ChangePasswordAsyncTaskRunner().execute();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            ((EditText) view).setError(message);
        }
    }


    /**
     * Inner class for handling Async data loading
     */
    private class ChangePasswordAsyncTaskRunner extends AsyncTask<String, Void, Boolean> {
        String oldPassword = edtOldPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        private BaseResponse baseResponse;
        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnChangePassword.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.password_reset_message));
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
                    baseResponse = new UserServiceClient().changeOldPassword(changePasswordRequest);
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
                NetworkConnectivity.showNetworkConnectMessage(ChangePasswordActivity.this, false);
                return;
            }
            switch (baseResponse.getCode()) {
                case OAuthConstant.HTTP_INTERNAL_SERVER_ERROR:
                    MessageSnackbar.showMessage(ChangePasswordActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_BAD_REQUEST:
                    MessageSnackbar.showMessage(ChangePasswordActivity.this, baseResponse.getShowMessage(), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                    MessageSnackbar.showMessage(ChangePasswordActivity.this, getString(R.string.server_not_found_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_UNAUTHORIZED:
                    pageNavigationActions(OAuthConstant.HTTP_UNAUTHORIZED, getString(R.string.session_expired_message));
                    break;
                case OAuthConstant.HTTP_OK:
                case OAuthConstant.HTTP_CREATED:
                    pageNavigationActions(OAuthConstant.HTTP_CREATED, baseResponse.getShowMessage());
                    break;
            }
        }
    }


    private void pageNavigationActions(int code, String message) {
        Preferences.clear();
        Intent intent;
        switch (code) {
            case OAuthConstant.HTTP_UNAUTHORIZED:
                intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                intent.putExtra("failure_msg", message);
                break;
            default:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("success_msg", message);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return super.onOptionsItemSelected(item);
    }
}
