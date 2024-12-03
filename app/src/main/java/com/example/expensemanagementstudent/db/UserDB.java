package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

//tao bang du lieu trong class nay
public class UserDB  extends SQLiteOpenHelper {

    public static final String DB_NAME = "users_db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "users";

    //khai bao cac cot trong bang du lieu
    public static final String ID_COL = "id";
    public static final String USERNAME_COL = "username";
    public static final String EMAIL_COL = "email";
    public static final String ADDRESS_COL = "address";
    public static final String PASS_COL = "password";
    public static final String GENDER_COL = "gender";
    public static final String CREATED_COL = "created_at";
    public static final String UPDATED_COL = "updated_at";

    public UserDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //tao bang du lieu voi sqlite
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " VARCHAR(60) NOT NULL, "
                + PASS_COL + " VARCHAR(255) NOT NULL, "
                + EMAIL_COL + " VARCHAR(60) NOT NULL, "
                + GENDER_COL + " VARCHAR(60) NOT NULL, "
                + ADDRESS_COL + " TEXT, "
                + CREATED_COL + " DATETIME, "
                + UPDATED_COL + " DATETIME"
                + ")";
        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //xoa bang du lieu cu
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long addNewAccountUser(String username, String password, String email, String gender, String address){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        ZonedDateTime now = ZonedDateTime.now();
        String created_at = dtf.format(now);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, username);
        values.put(PASS_COL, password);
        values.put(EMAIL_COL, email);
        values.put(GENDER_COL, gender);
        values.put(ADDRESS_COL, address);
        values.put(CREATED_COL, created_at);

        long result = sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return result;
    }

    public String checkLogin(String username, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // Kiểm tra xem tên người dùng đã tồn tại chưa
        String userQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + " = ?";
        Cursor userCursor = sqLiteDatabase.rawQuery(userQuery, new String[]{username});

        if (userCursor.getCount() == 0) {
            userCursor.close();
            sqLiteDatabase.close();
            return "USER_NOT_FOUND"; // Người dùng không tồn tại
        }

        // Kiểm tra mật khẩu
        String loginQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + " = ? AND " + PASS_COL + " = ?";
        Cursor loginCursor = sqLiteDatabase.rawQuery(loginQuery, new String[]{username, password});

        boolean isValid = loginCursor.getCount() > 0;
        loginCursor.close();
        sqLiteDatabase.close();

        if (!isValid) {
            return "WRONG_PASSWORD"; // Sai mật khẩu
        }

        return "LOGIN_SUCCESS"; // Đăng nhập thành công
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean resetPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "email = ?", new String[]{email});
        db.close();
        return rows > 0;
    }

}