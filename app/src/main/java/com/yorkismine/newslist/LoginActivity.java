package com.yorkismine.newslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText login_et;
    private EditText password_et;
    private Button loginBtn;

    private String loginText;
    private String passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        login_et = findViewById(R.id.login_et);
        password_et = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login_btn);

        Intent fromMainIntent = getIntent();

        if (fromMainIntent.getStringExtra("main_log") != null) {
            Log.d("CHECKER", "Done1IF");
            login_et.setText(fromMainIntent.getStringExtra("main_log"));
        }

        if (fromMainIntent.getStringExtra("main_pass") != null) {
            Log.d("CHECKER", "Done2IF");
            password_et.setText(fromMainIntent.getStringExtra("main_pass"));
        }


        loginBtn.setOnClickListener(v -> {
            loginText = login_et.getText().toString();
            passwordText = password_et.getText().toString();

            if (TextUtils.isEmpty(loginText) || TextUtils.isEmpty(passwordText)) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (loginText.trim().length() < 6 || passwordText.trim().length() < 6) {
                Toast.makeText(this, "few digits", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("pass", passwordText);
            intent.putExtra("log", loginText);

            Log.d("CHECKER", "logACT: log: " + loginText);
            Log.d("CHECKER", "logACT: pas: " + passwordText);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

}
