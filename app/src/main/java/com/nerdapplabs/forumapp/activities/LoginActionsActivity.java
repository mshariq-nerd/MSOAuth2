package com.nerdapplabs.forumapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.utility.Preferences;

public class LoginActionsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSignup, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_actions);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String successMessage = getIntent().getStringExtra("EMAIL_SENT_MESSAGE");
        if (null != successMessage) {
            // clear preferences
            Preferences.clear();
            Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_LONG).show();
            getIntent().removeExtra("EMAIL_SENT_MESSAGE");
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_signup:
                intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_login:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return super.onOptionsItemSelected(item);
    }
}
