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

import java.util.regex.Pattern;

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

            // Kiểm tra trường nhập rỗng
            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận trùng nhau
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra độ dài mật khẩu
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra có chứa chữ hoa
            if (!Pattern.compile("[A-Z]").matcher(newPassword).find()) {
                Toast.makeText(this, "Password must contain at least one uppercase letter!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra có chứa chữ số
            if (!Pattern.compile("[0-9]").matcher(newPassword).find()) {
                Toast.makeText(this, "Password must contain at least one digit!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra có chứa ký tự đặc biệt
            if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(newPassword).find()) {
                Toast.makeText(this, "Password must contain at least one special character!", Toast.LENGTH_SHORT).show();
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
}