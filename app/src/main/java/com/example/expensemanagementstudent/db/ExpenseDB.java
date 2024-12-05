package com.example.expensemanagementstudent.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExpenseDB {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper; // Add dbHelper as a field

    public ExpenseDB(Context context) {
        dbHelper = new DatabaseHelper(context); // Initialize dbHelper
        db = dbHelper.getWritableDatabase();
    }

    // Provide access to DatabaseHelper's getReadableDatabase
    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase(); // Use the initialized dbHelper
    }

    // Get expenses by category and month
    public Cursor getExpenseByCategoryAndMonth(int userId, String month, String year) {
        String query = "SELECT c.name, SUM(e.amount) " +
                "FROM expenses e " +
                "INNER JOIN categories c ON e.category_id = c._id " +
                "WHERE e.user_id = ? AND e.type = 1 " + // Thêm điều kiện lọc e.type = 1
                "AND strftime('%m', substr(e.date, 7, 4) || '-' || substr(e.date, 4, 2) || '-' || substr(e.date, 1, 2)) = ? " +
                "AND strftime('%Y', substr(e.date, 7, 4) || '-' || substr(e.date, 4, 2) || '-' || substr(e.date, 1, 2)) = ? " +
                "GROUP BY c.name";
        return db.rawQuery(query, new String[]{String.valueOf(userId), month, year});
    }

    public Cursor getTotalByType(int userId, String month, String year, int type) {
        String query = "SELECT SUM(e.amount) " +
                "FROM expenses e " +
                "WHERE e.user_id = ? AND e.type = ? AND strftime('%m', substr(e.date, 7, 4) || '-' || substr(e.date, 4, 2) || '-' || substr(e.date, 1, 2)) = ? " +
                "AND strftime('%Y', substr(e.date, 7, 4) || '-' || substr(e.date, 4, 2) || '-' || substr(e.date, 1, 2)) = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(type), month, year});
    }

    @SuppressLint("Range")
    public void logExpensesForMonth(int userId, String month) {
        String query = "SELECT * " +
                "FROM expenses " +
                "WHERE user_id = ? AND strftime('%m', date) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), month});

        if (cursor.moveToFirst()) {
            do {
                Log.d("ExpenseDB", "ID: " + cursor.getInt(cursor.getColumnIndex("id")) +
                        ", Amount: " + cursor.getFloat(cursor.getColumnIndex("amount")) +
                        ", Date: " + cursor.getString(cursor.getColumnIndex("date")) +
                        ", Category ID: " + cursor.getInt(cursor.getColumnIndex("category_id")));
            } while (cursor.moveToNext());
        } else {
            Log.d("ExpenseDB", "No expenses found for the month: " + month);
        }
        cursor.close();
    }

    // Add a transaction (income or expense)
    public long addTransaction(int type, double amount, String description, String date, int userId, int categoryId) {
        ContentValues values = new ContentValues();

        // Set column values
        values.put(DatabaseHelper.TYPE_COL, type); // Type (0: income, 1: expense)
        values.put(DatabaseHelper.AMOUNT_COL, amount); // Amount
        values.put(DatabaseHelper.DESCRIPTION_COL, description); // Description
        values.put(DatabaseHelper.DATE_COL, date); // Date
        values.put(DatabaseHelper.EXPENSE_USER_ID_COL, userId); // User ID
        values.put(DatabaseHelper.EXPENSE_CATEGORY_ID_COL, categoryId); // Category ID

        // Insert into the "expenses" table
        return db.insert(DatabaseHelper.EXPENSE_TABLE, null, values);
    }

    public Cursor getAllTransactions() {
        String query = "SELECT e.*, c.name AS category_name " +
                "FROM " + DatabaseHelper.EXPENSE_TABLE + " e " +
                "INNER JOIN " + DatabaseHelper.CATEGORY_TABLE + " c " +
                "ON e." + DatabaseHelper.EXPENSE_CATEGORY_ID_COL + " = c." + DatabaseHelper.CATEGORY_ID_COL +
                " ORDER BY e." + DatabaseHelper.DATE_COL + " DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getTransactionsByType(int type) {
        String query = "SELECT e.*, c.name AS category_name " +
                "FROM " + DatabaseHelper.EXPENSE_TABLE + " e " +
                "INNER JOIN " + DatabaseHelper.CATEGORY_TABLE + " c " +
                "ON e." + DatabaseHelper.EXPENSE_CATEGORY_ID_COL + " = c." + DatabaseHelper.CATEGORY_ID_COL +
                " WHERE e." + DatabaseHelper.TYPE_COL + " = ? " +
                "ORDER BY e." + DatabaseHelper.DATE_COL + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(type)});
    }

    public Cursor getTransactionsByCategory(String categoryName) {
        String query = "SELECT e.*, c.name AS category_name " +
                "FROM " + DatabaseHelper.EXPENSE_TABLE + " e " +
                "INNER JOIN " + DatabaseHelper.CATEGORY_TABLE + " c " +
                "ON e." + DatabaseHelper.EXPENSE_CATEGORY_ID_COL + " = c." + DatabaseHelper.CATEGORY_ID_COL +
                " WHERE c." + DatabaseHelper.CATEGORY_NAME_COL + " = ? " +
                "ORDER BY e." + DatabaseHelper.DATE_COL + " DESC";
        return db.rawQuery(query, new String[]{categoryName});
    }
    public Cursor getTransactionsByUserId(int userId) {
        String query = "SELECT e.*, c.name AS category_name " +
                "FROM expenses e " +
                "INNER JOIN categories c ON e.category_id = c._id " +
                "WHERE e.user_id = ? " +
                "ORDER BY e.date DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public Cursor getFilteredTransactions(int type, int categoryId) {
        String query = "SELECT e.*, c.name AS category_name " +
                "FROM " + DatabaseHelper.EXPENSE_TABLE + " e " +
                "INNER JOIN " + DatabaseHelper.CATEGORY_TABLE + " c " +
                "ON e." + DatabaseHelper.EXPENSE_CATEGORY_ID_COL + " = c." + DatabaseHelper.CATEGORY_ID_COL +
                " WHERE e." + DatabaseHelper.TYPE_COL + " = ? AND e." + DatabaseHelper.EXPENSE_CATEGORY_ID_COL + " = ? " +
                "ORDER BY e." + DatabaseHelper.DATE_COL + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(type), String.valueOf(categoryId)});
    }
}
