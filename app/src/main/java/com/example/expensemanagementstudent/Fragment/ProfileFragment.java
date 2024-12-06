package com.example.expensemanagementstudent.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensemanagementstudent.LoginActivity;
import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.db.UserDB;

public class ProfileFragment extends Fragment {
    private TextView profileName;
    private EditText tvAddress;
    private EditText emailAddress;
    private EditText tvGender;
    private Button logoutButton;
    private UserDB userDB;
private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImage;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileName = view.findViewById(R.id.profileName);
        tvAddress = view.findViewById(R.id.emailAddress);
        emailAddress = view.findViewById(R.id.tvAddress);
        logoutButton = view.findViewById(R.id.logoutButton);
        profileImage = view.findViewById(R.id.profileImage);
        tvGender = view.findViewById(R.id.tvGender);
        Button editProfileButton = view.findViewById(R.id.btnEditProfile);
        // Retrieve user information from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);


        String username = sharedPreferences.getString("username", "Username");
        String email = sharedPreferences.getString("email", "Not Provided");
        String address = sharedPreferences.getString("address", "Not Provided");
        String gender = sharedPreferences.getString("gender", "Not provided");
        // Load saved image URI
        SharedPreferences sharedPreferencess = requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);
        String savedImageUri = sharedPreferencess.getString("profileImageUri", null);

        if (savedImageUri != null) {
            try {
                Uri imageUri = Uri.parse(savedImageUri);
                // Kiểm tra xem URI có hợp lệ hay không
                if (imageUri != null) {
                    profileImage.setImageURI(imageUri);
                } else {
                    // URI không hợp lệ, hiển thị ảnh mặc định
                    profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                }
            } catch (Exception e) {
                // Bắt lỗi và hiển thị ảnh mặc định
                profileImage.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            // Nếu chưa lưu URI, hiển thị ảnh mặc định
            profileImage.setImageResource(R.drawable.ic_profile_placeholder);
        }

        // Other code...
        profileImage.setOnClickListener(v -> openImageChooser());

        // Update the UI
        profileName.setText(username);
        emailAddress.setText(email);  // Set email or other details
        tvAddress.setText(address);  // Set email or other details
        tvGender.setText(gender);
         // Setup edit profile click listener
        editProfileButton.setOnClickListener(v -> enableEditing());
        // Setup logout click listener
        logoutButton.setOnClickListener(v -> logout());


        return view;
    }

  private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Hiển thị hình ảnh trên ImageView
            profileImage.setImageURI(imageUri);

            // Lưu URI vào SharedPreferences
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profileImageUri", imageUri.toString());
            editor.apply();
        }
    }
private void enableEditing() {
    // Cho phép chỉnh sửa các trường thông tin
    profileName.setEnabled(true);
    emailAddress.setEnabled(true);
    tvAddress.setEnabled(true);
    tvGender.setEnabled(true);

    // Thay đổi nút thành "Save"
    Button editProfileButton = getView().findViewById(R.id.btnEditProfile);
    editProfileButton.setText("Save");
    editProfileButton.setOnClickListener(v -> saveProfileChanges());
}

private void saveProfileChanges() {
    // Lưu các thay đổi vào SharedPreferences
    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("username", profileName.getText().toString());
    editor.putString("email", emailAddress.getText().toString());
    editor.putString("address", tvAddress.getText().toString());
    editor.putString("gender", tvGender.getText().toString());
    editor.apply();

    // Vô hiệu hóa chỉnh sửa
    profileName.setEnabled(false);
    emailAddress.setEnabled(false);
    tvAddress.setEnabled(false);
    tvGender.setEnabled(false);

    // Thay đổi nút trở lại "Edit Profile"
    Button editProfileButton = getView().findViewById(R.id.btnEditProfile);
    editProfileButton.setText("Edit Profile");
    editProfileButton.setOnClickListener(v -> enableEditing());
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