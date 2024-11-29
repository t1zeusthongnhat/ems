package com.example.expensemanagementstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.onboarding.ContainerActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Kiểm tra trạng thái đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (isLoggedIn) {
                    // Nếu đã đăng nhập, chuyển tới màn hình chính
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Nếu chưa đăng nhập, chuyển tới màn hình đăng nhập
                    intent = new Intent(SplashActivity.this, ContainerActivity.class);
                }

                startActivity(intent);
                finish();
            }
        };

        handler.postDelayed(runnable, 3200); // Delay 3.2 seconds
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
