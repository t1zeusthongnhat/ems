package com.example.expensemanagementstudent.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.expensemanagementstudent.LoginActivity;
import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.db.UserDB;

public class ProfileFragment extends Fragment {
    private TextView profileName;
    private TextView tvAddress;
    private TextView emailAddress;
    private Button logoutButton;
    private UserDB userDB;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileName = view.findViewById(R.id.profileName);
        tvAddress = view.findViewById(R.id.tvAddress);
        emailAddress = view.findViewById(R.id.emailAddress);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Retrieve user information from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Username");
        String email = sharedPreferences.getString("email", "Not Provided");
        String address = sharedPreferences.getString("address", "Not Provided");

        // Update the UI
        profileName.setText(username);
        emailAddress.setText(email);  // Set email or other details
        tvAddress.setText(address);  // Set email or other details

        // Setup logout click listener
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }


    private void logout() {
        // Clear login state in SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString("username", "");  // Clear username and other info
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();  // Close the current activity
    }

}
