package com.nerdapplabs.forumapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.request.SignUpRequest;
import com.nerdapplabs.forumapp.oauth.response.SignUpResponse;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
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

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.login_error), Toast.LENGTH_LONG).show();
    }


    /**
     * Inner class for handling Async data loading
     */
    private class SignupAsyncTaskRunner extends AsyncTask<String, Void, Integer> {
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String userName = edtDisplayName.getText().toString();
        String password = edtPassword.getText().toString();
        SignUpRequest signUpRequestObj = new SignUpRequest();

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            signUpRequestObj.setFirstname(firstName);
            signUpRequestObj.setLastname(lastName);
            signUpRequestObj.setDob(dob);
            signUpRequestObj.setUsername(userName);
            signUpRequestObj.setPassword(password);
            signUpRequestObj.setEmail(email);
            signUpRequestObj.setEmail_confirmation("0");

            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnSignUp.getWindowToken(),
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
                    SignUpResponse signUpResponse = new SignUpResponse();
                    httpStatusCode = signUpResponse.getSignupUser(SignupActivity.this, signUpRequestObj);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //showErrorMessage(false);
            }
            return httpStatusCode;
        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            super.onPostExecute(statusCode);
            progressDialog.dismiss();
            if (statusCode == 200) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            } else {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.signup_layout);
                Snackbar.make(linearLayout, getString(R.string.login_error), Snackbar.LENGTH_LONG).show();
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
