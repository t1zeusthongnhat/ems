package com.example.expensemanagementstudent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.UserDB;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText edtNewPassword, edtConfirmPassword;
    Button btnResetPassword;
    UserDB userDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtNewPassword = findViewById(R.id.editTextNewPassword);
        edtConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnResetPassword = findViewById(R.id.buttonResetPassword);
        userDB = new UserDB(this);

        String email = getIntent().getStringExtra("email");

        btnResetPassword.setOnClickListener(view -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra tính hợp lệ của mật khẩu và lấy thông báo lỗi
            String validationError = validatePassword(newPassword);
            if (validationError != null) {
                Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Đặt lại mật khẩu
            boolean resetSuccess = userDB.resetPassword(email, newPassword);

            if (resetSuccess) {
                Toast.makeText(this, "Password reset successful.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error resetting password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức kiểm tra và trả về lỗi đầu tiên phát hiện
    private String validatePassword(String password) {
        if (password.length() < 6) {
            return "Password must be at least 6 characters long!";
        }
        if (!containsUpperCase(password)) {
            return "Password must include at least one uppercase letter!";
        }
        if (!containsLowerCase(password)) {
            return "Password must include at least one lowercase letter!";
        }
        if (!containsDigit(password)) {
            return "Password must include at least one digit!";
        }
        if (!containsSpecialCharacter(password)) {
            return "Password must include at least one special character!";
        }
        return null; // Không có lỗi
    }

    // Kiểm tra nếu có ít nhất một chữ cái viết hoa
    private boolean containsUpperCase(String password) {
        return password.matches(".*[A-Z].*");
    }

    // Kiểm tra nếu có ít nhất một chữ cái viết thường
    private boolean containsLowerCase(String password) {
        return password.matches(".*[a-z].*");
    }

    // Kiểm tra nếu có ít nhất một chữ số
    private boolean containsDigit(String password) {
        return password.matches(".*\\d.*");
    }

    // Kiểm tra nếu có ít nhất một ký tự đặc biệt
    private boolean containsSpecialCharacter(String password) {
        return password.matches(".*[@#$%^&+=!].*");
    }

}

