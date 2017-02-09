package com.nerdapplabs.forumapp.activities;

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

import com.nerdapplabs.forumapp.ForumApplication;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.client.UserService;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.response.BaseResponse;
import com.nerdapplabs.forumapp.pojo.User;
import com.nerdapplabs.forumapp.utility.Duration;
import com.nerdapplabs.forumapp.utility.ErrorType;
import com.nerdapplabs.forumapp.utility.MessageSnackbar;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private EditText edtUserName, edtFirstName,
            edtLastName, edtEmail, edtDateOfBirth;

    private Button btnSave;

    private Calendar calendar;
    private int year, month, day;
    private DatePickerDialog datePickerDialog;

    User userObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtEmail = (EditText) findViewById(R.id.edt_user_email);
        edtDateOfBirth = (EditText) findViewById(R.id.edt_dob);
        edtUserName = (EditText) findViewById(R.id.edt_user_name);
        btnSave = (Button) findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        // Adding Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(EditProfileActivity.this, year, month, day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(ContextCompat.getColor(EditProfileActivity.this, R.color.white));
                datePickerDialog.setTitle(getString(R.string.date_picker_title));
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        // To retrieve object in second Activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("User");
        Log.i(TAG, userObj.getUserName());

        // Set User information to EditFields to update
        if (userObj == null) {
            throw new NullPointerException("User must not be null");
        } else {
            if (userObj.getUserName() != null) {
                edtUserName.setText(userObj.getUserName());
            }
            if (userObj.getFirstName() != null) {
                edtFirstName.setText(userObj.getFirstName());
            }
            if (userObj.getLastName() != null) {
                edtLastName.setText(userObj.getLastName());
            }
            if (userObj.getDob() != null) {
                edtDateOfBirth.setText(userObj.getDob());
            }
            if (userObj.getEmailAddress() != null) {
                edtEmail.setText(userObj.getEmailAddress());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        ForumApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            if (!validate()) {
                return;
            } else {
                EditProfileAsyncTaskRunner editProfileAsyncTaskRunner = new EditProfileAsyncTaskRunner();
                editProfileAsyncTaskRunner.execute();
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String userName = edtUserName.getText().toString();

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

        if (userName.isEmpty() || userName.length() < 4) {
            edtUserName.setError(getString(R.string.display_name_validation_error));
            valid = false;
        } else {
            edtUserName.setError(null);
        }

        return valid;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(EditProfileActivity.this, isConnected);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (++monthOfYear) + "/" + dayOfMonth + "/" + year;
        edtDateOfBirth.setText(date);
    }

    /**
     * Inner class for handling Async data loading
     */
    private class EditProfileAsyncTaskRunner extends AsyncTask<String, Void, Boolean> {
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String userName = edtUserName.getText().toString();
        User user = new User();
        BaseResponse baseResponse;
        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnSave.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDob(dob);
            user.setUserName(userName);
            user.setEmailAddress(email);
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    // Read access token from preferences
                    String accessToken = Preferences.getString(OAuthConstant.ACCESS_TOKEN, null);
                    UserService userService = new UserService();
                    baseResponse = userService.updateProfile(EditProfileActivity.this, user, accessToken);
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
                    if (baseResponse.getCode() == 200 || baseResponse.getCode() == 201) {
                        MessageSnackbar.with(EditProfileActivity.this, null).type(ErrorType.SUCCESS)
                                .message(baseResponse.getShowMessage()).duration(Duration.LONG).show();
                    } else {
                        MessageSnackbar.with(EditProfileActivity.this, null).type(ErrorType.ERROR)
                                .message(baseResponse.getShowMessage()).duration(Duration.LONG).show();
                    }
                } else {
                    NetworkConnectivity.showNetworkConnectMessage(EditProfileActivity.this, false);
                }
            }
        }
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
