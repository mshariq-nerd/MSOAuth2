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
import com.nerdapplabs.msoauth2.oauth.client.UserService;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;

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
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.reset_password_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
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

        String userName = edtUserName.getText().toString();

        if (userName.isEmpty()) {
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
            }
            new ResetPasswordAsyncTaskRunner().execute();
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
            if (!isConnected) {
                NetworkConnectivity.showNetworkConnectMessage(ResetPasswordActivity.this, false);
                return;
            }

            Intent intent;
            switch (response.getCode()) {
                case OAuthConstant.HTTP_INTERNAL_SERVER_ERROR:
                    MessageSnackbar.showMessage(ResetPasswordActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_BAD_REQUEST:
                    MessageSnackbar.showMessage(ResetPasswordActivity.this, response.getShowMessage(), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                    MessageSnackbar.showMessage(ResetPasswordActivity.this, getString(R.string.server_not_found_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_UNAUTHORIZED:
                    MessageSnackbar.showMessage(ResetPasswordActivity.this, response.getShowMessage(), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_OK:
                case OAuthConstant.HTTP_CREATED:
                    Preferences.clear();
                    intent = new Intent(getApplicationContext(), LoginActionsActivity.class);
                    intent.putExtra("EMAIL_SENT_MESSAGE", response.getShowMessage());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;
            }
        }
    }
}
