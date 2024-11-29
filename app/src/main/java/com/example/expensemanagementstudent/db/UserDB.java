package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDB {

    private SQLiteDatabase db;

    public UserDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // Thêm người dùng mới
    public long addNewAccountUser(String username, String password, String email, String gender, String address) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USERNAME_COL, username);
        values.put(DatabaseHelper.PASS_COL, password);
        values.put(DatabaseHelper.EMAIL_COL, email);
        values.put(DatabaseHelper.GENDER_COL, gender);
        values.put(DatabaseHelper.ADDRESS_COL, address);

        return db.insert(DatabaseHelper.USER_TABLE, null, values);
    }

    // Hàm kiểm tra thông tin đăng nhập (đơn giản hơn)
    public String checkLogin(String username, String password) {
        // Câu truy vấn SQL
        String query = "SELECT " + DatabaseHelper.PASS_COL +
                " FROM " + DatabaseHelper.USER_TABLE +
                " WHERE " + DatabaseHelper.USERNAME_COL + " = ?";

        // Sử dụng rawQuery để chạy câu truy vấn
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy mật khẩu từ kết quả truy vấn
            String storedPassword = cursor.getString(0);
            cursor.close();

            if (storedPassword.equals(password)) {
                return "LOGIN_SUCCESS";
            } else {
                return "WRONG_PASSWORD";
            }
        } else {
            if (cursor != null) cursor.close();
            return "USER_NOT_FOUND";
        }
    }

}
