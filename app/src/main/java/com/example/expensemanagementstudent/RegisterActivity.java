package com.example.expensemanagementstudent;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.example.expensemanagementstudent.db.UserDB;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtEmail, edtAddress, edtPassword;
    Button btnRegister;
    UserDB userDB;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);

        // Khởi tạo view
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.editTextAddress);
        edtPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegis);

        userDB = new UserDB(RegisterActivity.this);

        btnRegister.setOnClickListener(view -> registerUser());

        findViewById(R.id.tvSignIn).setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Ẩn bàn phím khi chạm ngoài EditText
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ConstraintLayout parentLayout = findViewById(R.id.constraintLayout);
        parentLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Lấy giới tính từ RadioGroup
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        String gender = "";

        if (selectedGenderId == R.id.radioMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.radioFemale) {
            gender = "Female";
        }

        // Kiểm tra tính hợp lệ của đầu vào
        if (!validateInput(username, password, email, address, gender)) return;

        // Kiểm tra email đã tồn tại chưa
        if (userDB.isEmailExists(email)) {
            Toast.makeText(this, "This email is already registered!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userDB.isUsernameExists(username)) {
            Toast.makeText(this, "This username is already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long result = userDB.addNewAccountUser(username, password, email, gender, address);
            if (result != -1) {
                Toast.makeText(this, "Register successfully!", Toast.LENGTH_SHORT).show();
                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Register failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error during registration: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private boolean validateInput(String username, String password, String email, String address, String gender) {
        // Kiểm tra username
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 3 || username.length() > 20) {
            Toast.makeText(this, "Username must be between 3 and 20 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra mật khẩu
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isPasswordLengthValid(password)) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!containsUppercase(password)) {
            Toast.makeText(this, "Password must contain at least one uppercase letter.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!containsDigit(password)) {
            Toast.makeText(this, "Password must contain at least one number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!containsSpecialCharacter(password)) {
            Toast.makeText(this, "Password must contain at least one special character.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra email
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra địa chỉ
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Address cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra giới tính
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please select a gender.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordLengthValid(String password) {
        return password.length() >= 6;
    }

    private boolean containsUppercase(String password) {
        return Pattern.compile("[A-Z]").matcher(password).find();
    }

    private boolean containsDigit(String password) {
        return Pattern.compile("\\d").matcher(password).find();
    }

    private boolean containsSpecialCharacter(String password) {
        return Pattern.compile("[@#$%^&+=!]").matcher(password).find();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
