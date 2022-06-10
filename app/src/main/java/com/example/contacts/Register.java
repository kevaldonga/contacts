package com.example.contacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {
    Button log_in, sign_up;
    TextInputLayout email, password, username;
    TextInputEditText emailEdittext, passwordEdittext, usernameEdittext;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    loading_alertdialog_box mloading_alertdialog_box;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        intial_tasks();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Welcome");
        log_in = findViewById(R.id.log_in);
        sign_up = findViewById(R.id.sign_up);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        emailEdittext = findViewById(R.id.emailedt);
        passwordEdittext = findViewById(R.id.passwordedt);
        usernameEdittext = findViewById(R.id.usernameedt);
        username = findViewById(R.id.username);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkIfOffline();
        mloading_alertdialog_box = new loading_alertdialog_box(Register.this);
        usernameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = usernameEdittext.getText().toString();
                if (!data.equals("")) {
                    username.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = emailEdittext.getText().toString();
                if (data.equals("")) {
                    email.setError(null);
                    return;
                }
                if (Patterns.EMAIL_ADDRESS.matcher(data).matches()) {
                    email.setError(null);
                } else {
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
                if (data.equals("")) {
                    password.setError(null);
                    return;
                }
                if (data.length() < 8) {
                    password.setError("Too short !!");
                } else {
                    password.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        log_in.setOnClickListener(v -> {
            if (preCheck()) {
                Toast.makeText(this, "Remove error before submitting !!", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = emailEdittext.getText().toString(), password = passwordEdittext.getText().toString();
            mloading_alertdialog_box.start_dialog_box();
            firebaseAuth.signInWithEmailAndPassword(
                    email, password)
                    .addOnCompleteListener(Register.this, task -> {
                        if (task.isSuccessful()) {
                            Log.i("users", "User with email id - " + email + " has been logged in.");
                            Toast.makeText(this, "Logged in successfully !!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("data", "login");
                            editor.putString(email, usernameEdittext.getText().toString());
                            editor.putString("email", email);
                            editor.apply();
                            startActivity(intent);
                        } else {
                            Log.i("users", "Error occurred while trying to log in a user with email id of " + email);
                            Toast.makeText(this, "There was an error occurred !!", Toast.LENGTH_SHORT).show();
                            mloading_alertdialog_box.dismiss();
                            return;
                        }
                        mloading_alertdialog_box.dismiss();
                    });

        });
        sign_up.setOnClickListener(v -> {
            if (preCheck()) {
                Toast.makeText(this, "Remove error before submitting !!", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = emailEdittext.getText().toString(), password = passwordEdittext.getText().toString();
            mloading_alertdialog_box.start_dialog_box();
            firebaseAuth.createUserWithEmailAndPassword(
                    email, password)
                    .addOnCompleteListener(Register.this, task -> {
                        if (task.isSuccessful()) {
                            Log.i("users", "User with email id - " + email + " has been created.");
                            Toast.makeText(this, "Account created successfully !!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("data", "register");
                            editor.putString(email, usernameEdittext.getText().toString());
                            editor.putString("email", email);
                            editor.apply();
                            startActivity(intent);
                        } else {
                            Log.i("users", "Error occurred while creating user with email id of " + password);
                            Toast.makeText(this, "There was an error occurred !!", Toast.LENGTH_SHORT).show();
                            mloading_alertdialog_box.dismiss();
                            return;
                        }
                        mloading_alertdialog_box.dismiss();
                    });
        });
    }

    private void checkIfOffline() {
        if (firebaseUser != null) {
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You are offline !!")
                .setMessage("You need Internet to login into our app ,You don't have internet connection proceed !!!")
                .setNeutralButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                    finishAffinity();
                    finish();
                })
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void intial_tasks() {
        loading_alertdialog_box mloading_alertdialog_box = new loading_alertdialog_box(Register.this);
        mloading_alertdialog_box.start_dialog_box();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            mloading_alertdialog_box.dismiss();
            return;
        }
        mloading_alertdialog_box.dismiss();
        Toast.makeText(this, "Logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("data", "login");
        startActivity(intent);
    }

    private boolean preCheck() {
        String pass = passwordEdittext.getText().toString();
        String email = emailEdittext.getText().toString();
        String musername = usernameEdittext.getText().toString();
        if (pass.isEmpty() || email.isEmpty()) {
            return true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Entered email is in invalid format !!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (pass.length() < 8) {
            Toast.makeText(this, "Your password is too short !!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (musername.equals("")) {
            username.setError("Empty field !!");
            return true;
        }
        return false;
    }
}