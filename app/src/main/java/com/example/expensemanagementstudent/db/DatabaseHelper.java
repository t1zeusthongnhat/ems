package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseManagement.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to trigger onUpgrade

    // Table names
    public static final String USER_TABLE = "users";
    public static final String CATEGORY_TABLE = "categories";
    public static final String EXPENSE_TABLE = "expenses";
    public static final String BUDGET_TABLE = "budgets";

    // Columns for "users" table
    public static final String USER_ID_COL = "id";
    public static final String USERNAME_COL = "username";
    public static final String EMAIL_COL = "email";
    public static final String ADDRESS_COL = "address";
    public static final String PASS_COL = "password";
    public static final String GENDER_COL = "gender";
    public static final String CREATED_COL = "created_at";
    public static final String UPDATED_COL = "updated_at";

    // Columns for "categories" table
    public static final String CATEGORY_ID_COL = "_id";
    public static final String CATEGORY_NAME_COL = "name";
    public static final String CATEGORY_ICON_COL = "icon";
    public static final String CATEGORY_TYPE_COL = "type"; // 0 for income, 1 for expense

    // Columns for "budgets" table
    public static final String BUDGET_ID_COL = "id";
    public static final String BUDGET_AMOUNT_COL = "amount";
    public static final String BUDGET_TYPE_COL = "type"; // 0: income, 1: expense
    public static final String BUDGET_CATEGORY_ID_COL = "category_id";

    // Columns for "expenses" table
    public static final String EXPENSE_ID_COL = "id";
    public static final String TYPE_COL = "type"; // 0: income, 1: expense
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
        // Create "users" table
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

        // Create "categories" table
        String createCategoryTable = "CREATE TABLE " + CATEGORY_TABLE + " (" +
                CATEGORY_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATEGORY_NAME_COL + " TEXT NOT NULL, " +
                CATEGORY_ICON_COL + " TEXT NOT NULL, " +
                CATEGORY_TYPE_COL + " INTEGER NOT NULL DEFAULT 1);"; // 1: expense (default), 0: income
        db.execSQL(createCategoryTable);

        // Create "expenses" table
        String createExpenseTable = "CREATE TABLE " + EXPENSE_TABLE + " (" +
                EXPENSE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TYPE_COL + " INTEGER NOT NULL, " +
                AMOUNT_COL + " REAL NOT NULL, " +
                DESCRIPTION_COL + " TEXT, " +
                DATE_COL + " TEXT NOT NULL, " +
                EXPENSE_USER_ID_COL + " INTEGER, " +
                EXPENSE_CATEGORY_ID_COL + " INTEGER, " +
                "FOREIGN KEY(" + EXPENSE_USER_ID_COL + ") REFERENCES " + USER_TABLE + "(" + USER_ID_COL + "), " +
                "FOREIGN KEY(" + EXPENSE_CATEGORY_ID_COL + ") REFERENCES " + CATEGORY_TABLE + "(" + CATEGORY_ID_COL + "));";
        db.execSQL(createExpenseTable);

        // Create budgets table
        String createBudgetTable = "CREATE TABLE " + BUDGET_TABLE + " (" +
                BUDGET_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BUDGET_AMOUNT_COL + " REAL NOT NULL, " +
                BUDGET_TYPE_COL + " INTEGER NOT NULL, " +
                BUDGET_CATEGORY_ID_COL + " INTEGER, " +
                "FOREIGN KEY(" + BUDGET_CATEGORY_ID_COL + ") REFERENCES " + CATEGORY_TABLE + "(" + CATEGORY_ID_COL + "));";
        db.execSQL(createBudgetTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {

            // Đổi tên bảng cũ
            db.execSQL("ALTER TABLE " + CATEGORY_TABLE + " RENAME TO " + CATEGORY_TABLE + "_old;");

            // Tạo bảng mới
            String createCategoryTable = "CREATE TABLE " + CATEGORY_TABLE + " (" +
                    CATEGORY_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CATEGORY_NAME_COL + " TEXT NOT NULL, " +
                    CATEGORY_ICON_COL + " TEXT NOT NULL, " +
                    CATEGORY_TYPE_COL + " INTEGER NOT NULL DEFAULT 1);"; // 1: expense (default), 0: income
            db.execSQL(createCategoryTable);

            // Di chuyển dữ liệu từ bảng cũ sang bảng mới
            String migrateData = "INSERT INTO " + CATEGORY_TABLE + " (" +
                    CATEGORY_ID_COL + ", " +
                    CATEGORY_NAME_COL + ", " +
                    CATEGORY_ICON_COL + ", " +
                    CATEGORY_TYPE_COL + ") " +
                    "SELECT " +
                    CATEGORY_ID_COL + ", " +
                    CATEGORY_NAME_COL + ", " +
                    CATEGORY_ICON_COL + ", " +
                    "1 " + // Mặc định tất cả là "expense" nếu không có cột `is_income`
                    "FROM " + CATEGORY_TABLE + "_old;";
            db.execSQL(migrateData);

            // Xóa bảng cũ
            db.execSQL("DROP TABLE " + CATEGORY_TABLE + "_old;");
        }
    }





}