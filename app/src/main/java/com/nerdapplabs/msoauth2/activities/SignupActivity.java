package com.nerdapplabs.msoauth2.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.nerdapplabs.msoauth2.MSOAuth2;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.client.SignUpServiceClient;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.request.SignUpRequest;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.Utility;
import com.nerdapplabs.msoauth2.validation.UserName;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener, Validator.ValidationListener {
    private static final String TAG = SignupActivity.class.getSimpleName();
    private DatePickerDialog datePickerDialog;

    @NotEmpty
    private EditText edtFirstName;

    @NotEmpty
    private EditText edtLastName;

    @Email
    private EditText edtEmail;

    @NotEmpty
    private EditText edtDateOfBirth;

    @UserName(messageResId = R.string.username_validation_error, scheme = UserName.Scheme.ALPHA_NUMERIC)
    private EditText edtDisplayName;

    @Password(messageResId = R.string.password_validation_error, scheme = Password.Scheme.ANY)
    private EditText edtPassword;

    @ConfirmPassword
    private EditText edtConfirmPassword;


    private Button btnSignUp;

    private int year, month, day;

    private Validator validator;

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
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.signup_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // current date after 3 months
        Calendar yearBeforeHundredYears = Calendar.getInstance();
        yearBeforeHundredYears.add(Calendar.YEAR, 100);

        // current date before 3 months
        Calendar yearBeforeFiveYears = Calendar.getInstance();
        yearBeforeFiveYears.add(Calendar.YEAR, -5);


        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(SignupActivity.this, year, month, day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setYearRange(Utility.getYearBeforeHundredYears(), Utility.getYearBeforeFiveYears());
                datePickerDialog.setAccentColor(ContextCompat.getColor(SignupActivity.this, R.color.white));
                datePickerDialog.setTitle(getString(R.string.date_picker_title));
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        btnSignUp.setOnClickListener(this);

        // Instantiate a new Validator
        validator = new Validator(this);
        validator.setValidationListener(this);
        Validator.registerAnnotation(UserName.class);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        MSOAuth2.getInstance().setConnectivityListener(this);
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
            validator.validate();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(SignupActivity.this, isConnected);
    }

    @Override
    public void onValidationSucceeded() {
        new SignupAsyncTaskRunner().execute();
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
    private class SignupAsyncTaskRunner extends AsyncTask<String, Void, Boolean> {
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String userName = edtDisplayName.getText().toString();
        String password = edtConfirmPassword.getText().toString();
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
            progressDialog.setMessage(getString(R.string.sign_up_authentication));
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
            // TODO: Need to fix for valid json request
            signUpRequest.setEmailConfirmation("0");
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    responseMessage = new SignUpServiceClient().registerUser(SignupActivity.this, signUpRequest);
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
                NetworkConnectivity.showNetworkConnectMessage(SignupActivity.this, false);
                return;
            }

            String loggedInUser = Preferences.getString(OAuthConstant.USERNAME, null);
            if (loggedInUser == null) {
                MessageSnackbar.showMessage(SignupActivity.this, responseMessage, ErrorType.ERROR);
                return;
            }

            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
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
}
