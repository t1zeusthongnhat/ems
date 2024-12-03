package com.example.expensemanagementstudent;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin;
    UserDB userDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        // Khởi tạo UserDB
        userDB = new UserDB(this);

        edtUsername = findViewById(R.id.editTextUsername);
        edtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonGetStarted);

        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Kiểm tra cờ showUsernameOnce
        boolean showUsernameOnce = sharedPreferences.getBoolean("showUsernameOnce", false);
        if (showUsernameOnce) {
            // Hiển thị username nếu cờ được bật
            String savedUsername = sharedPreferences.getString("username", "");
            edtUsername.setText(savedUsername);

            // Xóa cờ để username không hiển thị lại lần sau
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("showUsernameOnce"); // Xóa cờ
            editor.apply();
        } else {
            // Không hiển thị username
            edtUsername.setText("");
        }

        // Xóa mật khẩu mỗi khi vào màn hình đăng nhập
        edtPassword.setText("");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // Kiểm tra thông tin đăng nhập
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter complete information!", Toast.LENGTH_SHORT).show();

                } else {
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
                            // Lấy userId từ database
                            int userId = userDB.getUserId(username);

                            // Lưu trạng thái đăng nhập
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("username", username);
                            editor.putInt("userId", userId); // Lưu userId vào SharedPreferences
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
                }
            }
        });

        TextView tvRegis;
        tvRegis = findViewById(R.id.textViewSignUp);
        tvRegis.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        ConstraintLayout parentLayout = findViewById(R.id.constraintLayout);
        parentLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
