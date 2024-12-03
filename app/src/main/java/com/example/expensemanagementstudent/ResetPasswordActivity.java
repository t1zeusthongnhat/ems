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

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
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

