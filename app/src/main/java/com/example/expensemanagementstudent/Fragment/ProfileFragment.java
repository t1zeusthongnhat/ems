package com.example.expensemanagementstudent.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.db.UserDB;

public class ProfileFragment extends Fragment {
    private TextView profileName;
    private TextView phoneNumber;
    private TextView emailAddress;
    private UserDB userDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Khởi tạo views
        profileName = view.findViewById(R.id.profileName);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        emailAddress = view.findViewById(R.id.emailAddress);
        
        // Lấy thông tin user từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Username");
        
        // Cập nhật UI
        profileName.setText(username);
        
        // Khởi tạo click listeners
        setupClickListeners(view);
        
        return view;
    }
    
    private void setupClickListeners(View view) {
        view.findViewById(R.id.currencySettings).setOnClickListener(v -> {
            // Xử lý currency settings
        });
        
        view.findViewById(R.id.exportRecords).setOnClickListener(v -> {
            // Xử lý export records
        });
        
        view.findViewById(R.id.backupRestore).setOnClickListener(v -> {
            // Xử lý backup & restore
        });
        
        view.findViewById(R.id.deleteReset).setOnClickListener(v -> {
            // Xử lý delete & reset
        });
        
        view.findViewById(R.id.themeSettings).setOnClickListener(v -> {
            // Xử lý theme settings
        });
        
        view.findViewById(R.id.moneyTracking).setOnClickListener(v -> {
            // Xử lý money tracking settings
        });
    }
}