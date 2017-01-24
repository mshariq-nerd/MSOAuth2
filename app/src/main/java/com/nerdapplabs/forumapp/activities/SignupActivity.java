package com.nerdapplabs.forumapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nerdapplabs.forumapp.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private EditText edtDateOfBirth;
    private TextView txtLoginLink;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtLoginLink = (TextView) findViewById(R.id.txt_link_login);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        edtDateOfBirth = (EditText) findViewById(R.id.edt_dob);
        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(SignupActivity.this, year, month, day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(Color.parseColor("#009688"));

                datePickerDialog.setTitle("Select Date");

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "-" + (++monthOfYear) + "-" + year;
        edtDateOfBirth.setText(date);
    }

}
