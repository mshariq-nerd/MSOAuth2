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

import com.nerdapplabs.msoauth2.MSOAuth2;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.client.UserServiceClient;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.oauth.response.BaseResponse;
import com.nerdapplabs.msoauth2.pojo.User;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener,
        DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private EditText edtUserName, edtFirstName,
            edtLastName, edtEmail, edtDateOfBirth;
    private Button btnSave;
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

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.edit_profile_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Calendar calendar = Calendar.getInstance();
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

        // Set User information to Edit Fields to update
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
                // change date format
                String dob = userObj.getDob().replace("-", "/");
                Log.d(TAG, dob);
                edtDateOfBirth.setText(dob);
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
        MSOAuth2.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onClick(View view) {
        // Update record when save button clicked
        if (view.getId() == R.id.btn_save) {
            if (!validate()) {
                return;
            }
            EditProfileAsyncTaskRunner editProfileAsyncTaskRunner = new EditProfileAsyncTaskRunner();
            editProfileAsyncTaskRunner.execute();
        }
    }

    /**
     * Method used to validate form data
     *
     * @return valid Boolean for valid data
     */
    public boolean validate() {
        boolean valid = true;

        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String dob = edtDateOfBirth.getText().toString();
        String userName = edtUserName.getText().toString();

        if (firstName.isEmpty()) {
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

        if (userName.isEmpty()) {
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
            progressDialog.setMessage(getString(R.string.update_profile_message));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDob(dob);
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    baseResponse = new UserServiceClient().updateProfile(user);
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
                NetworkConnectivity.showNetworkConnectMessage(EditProfileActivity.this, false);
                return;
            }
            switch (baseResponse.getCode()) {
                case OAuthConstant.HTTP_INTERNAL_SERVER_ERROR:
                    MessageSnackbar.showMessage(EditProfileActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                    MessageSnackbar.showMessage(EditProfileActivity.this, getString(R.string.server_not_found_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_BAD_REQUEST:
                    MessageSnackbar.showMessage(EditProfileActivity.this, baseResponse.getShowMessage(), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_OK:
                case OAuthConstant.HTTP_CREATED:
                    MessageSnackbar.showMessage(EditProfileActivity.this, baseResponse.getShowMessage(), ErrorType.SUCCESS);
                    break;
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
