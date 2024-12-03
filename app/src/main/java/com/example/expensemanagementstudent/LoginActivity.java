package com.example.expensemanagementstudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.expensemanagementstudent.db.UserDB;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin;
    UserDB userDB;
    TextView textViewForgotPassword;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        passwordLayout = findViewById(R.id.passwordLayout);
        // Khởi tạo UserDB
        userDB = new UserDB(this);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        edtUsername = findViewById(R.id.editTextUsername);
        edtPassword = findViewById(R.id.editTextPasswordLogin);
        btnLogin = findViewById(R.id.buttonGetStarted);
        passwordEditText = findViewById(R.id.editTextPasswordLogin);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        textViewForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        passwordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra trạng thái hiện tại của mật khẩu
                if (passwordEditText.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    // Nếu mật khẩu đang ẩn, thay đổi thành hiển thị
                    passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Nếu mật khẩu đang hiển thị, thay đổi thành ẩn
                    passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Di chuyển con trỏ về cuối cùng của TextInputEditText để không bị mất vị trí
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        // Hiển thị username (nếu có cờ showUsernameOnce)
        boolean showUsernameOnce = sharedPreferences.getBoolean("showUsernameOnce", false);
        if (showUsernameOnce) {
            String savedUsername = sharedPreferences.getString("username", "");
            edtUsername.setText(savedUsername != null ? savedUsername : "");
            // Xóa cờ để tránh hiển thị lại
            sharedPreferences.edit().remove("showUsernameOnce").apply();
        } else {
            edtUsername.setText("");
        }
        edtPassword.setText(""); // Luôn xóa mật khẩu cũ

        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!validateInput(username, password)) return;

            try {
                String loginResult = userDB.checkLogin(username, password);
                switch (loginResult) {
                    case "USER_NOT_FOUND":
                        Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                        break;

                    case "WRONG_PASSWORD":
                        Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                        break;

                    case "LOGIN_SUCCESS":
                        Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                        // Lưu trạng thái đăng nhập
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.putBoolean("showUsernameOnce", true); // Đặt cờ để hiển thị username 1 lần
                        editor.apply();

                        // Chuyển đến màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    default:
                        Toast.makeText(LoginActivity.this, "Unknown error!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "Error during login: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        TextView tvRegis = findViewById(R.id.textViewSignUp);
        tvRegis.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        ConstraintLayout parentLayout = findViewById(R.id.constraintLayout);
        parentLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
    }

    /**
     * Ẩn bàn phím
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    /**
     * Kiểm tra tính hợp lệ của đầu vào
     */
    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 3 || username.length() > 20) {
            Toast.makeText(this, "Username must be between 3 and 20 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches() && username.contains(" ")) {
            Toast.makeText(this, "Username cannot contain spaces!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
