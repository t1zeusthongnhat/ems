package com.example.expensemanagementstudent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.UserDB;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnVerifyEmail;
    UserDB userDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.editTextEmail);
        btnVerifyEmail = findViewById(R.id.buttonVerifyEmail);
        userDB = new UserDB(this);

        btnVerifyEmail.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra email trong cơ sở dữ liệu
            boolean emailExists = userDB.checkEmailExists(email);

            if (emailExists) {
                // Chuyển sang màn hình đổi mật khẩu
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email); // Truyền email sang màn hình tiếp theo
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Email does not exist!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

