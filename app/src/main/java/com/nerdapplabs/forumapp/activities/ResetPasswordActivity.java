package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.nerdapplabs.forumapp.ForumApplication;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;

import java.io.IOException;

public class ResetPasswordActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, View.OnClickListener {
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    private EditText edtUserName;
    private Button btnRestPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
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
        ForumApplication.getInstance().setConnectivityListener(this);
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
        String responseMessage = "";

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
                    UserService userService = new UserService();
                    responseMessage = userService.resetPassword(userName.trim());
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
                if (null != responseMessage && !responseMessage.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActionsActivity.class);
                    intent.putExtra("EMAIL_SENT_MESSAGE", responseMessage);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            } else {
                NetworkConnectivity.showNetworkConnectMessage(ResetPasswordActivity.this, false);
            }
        }
    }
}
