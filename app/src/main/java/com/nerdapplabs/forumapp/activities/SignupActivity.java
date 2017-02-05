package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nerdapplabs.forumapp.ForumApplication;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.client.SignUpService;
import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.utility.Duration;
import com.nerdapplabs.forumapp.utility.MessageSnackbar;
import com.nerdapplabs.forumapp.utility.ErrorType;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private static final String TAG = SignupActivity.class.getSimpleName();
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private EditText edtFirstName, edtLastName, edtEmail, edtDateOfBirth,
            edtDisplayName, edtPassword, edtConfirmPassword;
    private TextView txtLoginLink;
    private Button btnSignUp;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtFirstName = (EditText) findViewById(R.id.edt_fname);
        edtLastName = (EditText) findViewById(R.id.edt_lname);
        edtEmail = (EditText) findViewById(R.id.edt_user_email);
        edtDateOfBirth = (EditText) findViewById(R.id.edt_dob);
        edtDisplayName = (EditText) findViewById(R.id.edt_display_name);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText) findViewById(R.id.edt_confirm_password);

        txtLoginLink = (TextView) findViewById(R.id.txt_link_login);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(SignupActivity.this, year, month, day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(ContextCompat.getColor(SignupActivity.this, R.color.white));
                datePickerDialog.setTitle(getString(R.string.date_picker_title));
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        btnSignUp.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        ForumApplication.getInstance().setConnectivityListener(this);
    }

    public boolean validate() {
        boolean valid = true;

        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String displayName = edtDisplayName.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (firstName.isEmpty() || firstName.length() < 4) {
            edtFirstName.setError(getString(R.string.username_validation_error));
            valid = false;
        } else {
            edtFirstName.setError(null);
        }

        if (lastName.isEmpty()) {
            edtLastName.setError(getString(R.string.username_validation_error));
            valid = false;
        } else {
            edtLastName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getString(R.string.email_validation_error));
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (dob.isEmpty()) {
            edtDateOfBirth.setError(getString(R.string.dob_validation_error));
            valid = false;
        } else {
            edtDateOfBirth.setError(null);
        }

        if (displayName.isEmpty() || displayName.length() < 4) {
            edtDisplayName.setError(getString(R.string.display_name_validation_error));
            valid = false;
        } else {
            edtDisplayName.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtPassword.setError(getString(R.string.password_validation_error));
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            edtConfirmPassword.setError(getString(R.string.password_validation_error));
            valid = false;
        } else {
            if (!confirmPassword.equals(password)) {
                edtConfirmPassword.setError(getString(R.string.password_match_error));
                valid = false;
            } else {
                edtConfirmPassword.setError(null);
            }
        }

        return valid;
    }


    public void onLoginLinkClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (++monthOfYear) + "/" + dayOfMonth + "/" + year;
        edtDateOfBirth.setText(date);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signup) {
            signUp();
        }
    }

    /**
     * Method to login into Application
     */
    public void signUp() {
        Log.d(TAG, "Signup");
        if (!validate()) {
            return;
        } else {
            SignupAsyncTaskRunner signupAsyncTaskRunner = new SignupAsyncTaskRunner();
            signupAsyncTaskRunner.execute();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(SignupActivity.this, isConnected);
    }

    /**
     * Inner class for handling Async data loading
     */
    private class SignupAsyncTaskRunner extends AsyncTask<String, Void, Boolean> {
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String userName = edtDisplayName.getText().toString();
        String password = edtPassword.getText().toString();
        SignUpRequest signUpRequest = new SignUpRequest();
        String responseMessage;
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnSignUp.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            signUpRequest.setFirstName(firstName);
            signUpRequest.setLastName(lastName);
            signUpRequest.setDob(dob);
            signUpRequest.setUserName(userName);
            signUpRequest.setPassword(password);
            signUpRequest.setEmailAddress(email);
            signUpRequest.setEmailConfirmation("0");
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    SignUpService signUpResponse = new SignUpService();
                    responseMessage = signUpResponse.registerUser(SignupActivity.this, signUpRequest);
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
                String loggedInUser = Preferences.getString("userName", null);
                if (loggedInUser != null) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                } else {
                    if (!responseMessage.isEmpty()) {
                        MessageSnackbar.with(SignupActivity.this, null).type(ErrorType.ERROR)
                                .message(responseMessage).duration(Duration.LONG).show();
                    }
                }
            } else {
                NetworkConnectivity.showNetworkConnectMessage(SignupActivity.this, false);
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
