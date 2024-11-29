package com.example.expensemanagementstudent.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    public static final String USER_TABLE = "users";
    public static final String CATEGORY_TABLE = "categories";
    public static final String EXPENSE_TABLE = "expenses";

    // Các cột cho bảng "users"
    public static final String USER_ID_COL = "id";
    public static final String USERNAME_COL = "username";
    public static final String EMAIL_COL = "email";
    public static final String ADDRESS_COL = "address";
    public static final String PASS_COL = "password";
    public static final String GENDER_COL = "gender";
    public static final String CREATED_COL = "created_at";
    public static final String UPDATED_COL = "updated_at";

    // Các cột cho bảng "categories"
    public static final String CATEGORY_ID_COL = "_id"; // ID column for CursorAdapter
    public static final String CATEGORY_NAME_COL = "name";
    public static final String CATEGORY_ICON_COL = "icon";

    // Các cột cho bảng "expenses"
    public static final String EXPENSE_ID_COL = "id";
    public static final String TYPE_COL = "type"; // 1: income, 0: expense
    public static final String AMOUNT_COL = "amount";
    public static final String DESCRIPTION_COL = "description";
    public static final String DATE_COL = "date";
    public static final String EXPENSE_USER_ID_COL = "user_id";
    public static final String EXPENSE_CATEGORY_ID_COL = "category_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng "users"
        String createUserTable = "CREATE TABLE " + USER_TABLE + " (" +
                USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME_COL + " TEXT NOT NULL, " +
                EMAIL_COL + " TEXT NOT NULL, " +
                ADDRESS_COL + " TEXT, " +
                PASS_COL + " TEXT NOT NULL, " +
                GENDER_COL + " TEXT, " +
                CREATED_COL + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                UPDATED_COL + " TEXT DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(createUserTable);

        // Tạo bảng "categories"
        String createCategoryTable = "CREATE TABLE " + CATEGORY_TABLE + " (" +
                CATEGORY_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATEGORY_NAME_COL + " TEXT NOT NULL, " +
                CATEGORY_ICON_COL + " TEXT NOT NULL);";
        db.execSQL(createCategoryTable);

        // Tạo bảng "expenses"
        String createExpenseTable = "CREATE TABLE " + EXPENSE_TABLE + " (" +
                EXPENSE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TYPE_COL + " INTEGER NOT NULL, " + // 1: income, 0: expense
                AMOUNT_COL + " REAL NOT NULL, " +
                DESCRIPTION_COL + " TEXT, " +
                DATE_COL + " TEXT NOT NULL, " +
                EXPENSE_USER_ID_COL + " INTEGER, " +
                EXPENSE_CATEGORY_ID_COL + " INTEGER, " +
                "FOREIGN KEY(" + EXPENSE_USER_ID_COL + ") REFERENCES " + USER_TABLE + "(" + USER_ID_COL + "), " +
                "FOREIGN KEY(" + EXPENSE_CATEGORY_ID_COL + ") REFERENCES " + CATEGORY_TABLE + "(" + CATEGORY_ID_COL + "));";
        db.execSQL(createExpenseTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
        onCreate(db);
    }
}
