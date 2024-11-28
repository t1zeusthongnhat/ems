package com.example.expensemanagementstudent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

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
    private void signupWithDataFile(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if(TextUtils.isEmpty(username)) {
                    edtUsername.setError("Username can be not empty");
                    return;
                }     if(TextUtils.isEmpty(password)) {
                    edtPassword.setError("Password can be not empty");
                    return;
                }
                FileOutputStream fileOutputStream = null;
                try {
                    username = username + "|";
                    fileOutputStream = openFileOutput("account.txt", Context.MODE_APPEND);
                    fileOutputStream.write(username.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write('\n');
                    fileOutputStream.close();
                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(RegisterActivity.this, "Succesfully", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void singupWithSQLite() {
        String user = edtUsername.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Lấy RadioGroup và kiểm tra tùy chọn được chọn
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        String gender = "";

        if (selectedGenderId == R.id.radioMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.radioFemale) {
            gender = "Female";
        }

        // Kiểm tra các trường dữ liệu
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm dữ liệu vào SQLite
        long insert = userDB.addNewAccountUser(user, pass, email, gender, address);
        if (insert == -1) {
            Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show();
        }

        // Chuyển đến màn hình đăng nhập
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
