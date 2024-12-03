package com.example.expensemanagementstudent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.expensemanagementstudent.db.UserDB;


public class RegisterActivity extends AppCompatActivity {
    TextView tvRegister;
    EditText edtUsername, edtEmail, edtAddress, edtPassword;
    Button btnRegister;

    UserDB userDB;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);

        //nap view cho thong tin
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.editTextAddress);
        edtPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegis);

        userDB = new UserDB(RegisterActivity.this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singupWithSQLite();

            }
        });

        tvRegister = findViewById(R.id.tvSignIn);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Add touch listener to the parent layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ConstraintLayout parentLayout = findViewById(R.id.constraintLayout);
        parentLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void singupWithSQLite() {
        String user = edtUsername.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Get selected gender from RadioGroup
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        String gender = "";

        if (selectedGenderId == R.id.radioMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.radioFemale) {
            gender = "Female";
        }

        // Validate input fields
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user data to SQLite
        long insert = userDB.addNewAccountUser(user, pass, email, gender, address);
        if (insert == -1) {
            Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show();
        } else {
            // Save user info in SharedPreferences after successful registration
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", user);
            editor.putString("address", address);
            editor.putString("email", email);  // Optional: Store more info like email
            editor.putBoolean("isLoggedIn", true); // Mark as logged in
            editor.apply();

            Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show();
        }

        // Redirect to login screen
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
