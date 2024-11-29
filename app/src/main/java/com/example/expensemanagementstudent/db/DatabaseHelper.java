package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String USER_TABLE = "users";
    public static final String CATEGORY_TABLE = "categories";
    public static final String EXPENSE_TABLE = "expenses";

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

    // Columns for "expenses" table
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
                CATEGORY_ICON_COL + " TEXT NOT NULL);";
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
        onCreate(db);
    }

    // Add a new transaction (income or expense)
    public long addTransaction(int type, double amount, String description, String date, int userId, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TYPE_COL, type);
        values.put(AMOUNT_COL, amount);
        values.put(DESCRIPTION_COL, description);
        values.put(DATE_COL, date);
        values.put(EXPENSE_USER_ID_COL, userId);
        values.put(EXPENSE_CATEGORY_ID_COL, categoryId);
        return db.insert(EXPENSE_TABLE, null, values);
    }

    // Update a transaction
    public int updateTransaction(int id, int type, double amount, String description, String date, int userId, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TYPE_COL, type);
        values.put(AMOUNT_COL, amount);
        values.put(DESCRIPTION_COL, description);
        values.put(DATE_COL, date);
        values.put(EXPENSE_USER_ID_COL, userId);
        values.put(EXPENSE_CATEGORY_ID_COL, categoryId);
        return db.update(EXPENSE_TABLE, values, EXPENSE_ID_COL + "=?", new String[]{String.valueOf(id)});
    }

    // Delete a transaction
    public int deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(EXPENSE_TABLE, EXPENSE_ID_COL + "=?", new String[]{String.valueOf(id)});
    }

    // Retrieve all transactions
    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " ORDER BY " + DATE_COL + " DESC", null);
    }

    // Retrieve transactions by type (1 for income, 0 for expense)
    public Cursor getTransactionsByType(int type) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE " + TYPE_COL + "=? ORDER BY " + DATE_COL + " DESC", new String[]{String.valueOf(type)});
    }

    // Retrieve total income or expense
    public double getTotalByType(int type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + AMOUNT_COL + ") FROM " + EXPENSE_TABLE + " WHERE " + TYPE_COL + "=?", new String[]{String.valueOf(type)});
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        cursor.close();
        return 0;
    }
}
