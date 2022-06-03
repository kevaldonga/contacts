package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Register extends AppCompatActivity {
    Button log_in, sign_up;
    TextInputLayout email, password;
    TextInputEditText emailEdittext,passwordEdittext,usernameEdittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        log_in = findViewById(R.id.log_in);
        sign_up = findViewById(R.id.sign_up);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        emailEdittext = findViewById(R.id.emailedt);
        passwordEdittext = findViewById(R.id.passwordedt);
        usernameEdittext = findViewById(R.id.usernameedt);

        emailEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = emailEdittext.getText().toString();
                if(data.equals("")){
                    email.setError(null);
                    return;
                }
                if(Patterns.EMAIL_ADDRESS.matcher(data).matches()){
                    email.setError(null);
                }
                else{
                    email.setError("Invalid Format");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = passwordEdittext.getText().toString();
                if(data.equals("")){
                    password.setError(null);
                    return;
                }
                if(data.length() < 8){
                    password.setError("Too short !!");
                }
                else{
                    password.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        log_in.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        });
        sign_up.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        });
    }
}