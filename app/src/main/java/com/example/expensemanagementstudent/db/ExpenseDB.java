package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ExpenseDB {

    private SQLiteDatabase db;

    public ExpenseDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // Thêm một khoản thu/chi
    public long addExpense(int type, double amount, String description, String date, int userId, int categoryId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TYPE_COL, type);
        values.put(DatabaseHelper.AMOUNT_COL, amount);
        values.put(DatabaseHelper.DESCRIPTION_COL, description);
        values.put(DatabaseHelper.DATE_COL, date);
        values.put(DatabaseHelper.EXPENSE_USER_ID_COL, userId);
        values.put(DatabaseHelper.EXPENSE_CATEGORY_ID_COL, categoryId);

        return db.insert(DatabaseHelper.EXPENSE_TABLE, null, values);
    }

    // Lấy tất cả các khoản thu/chi
    public Cursor getAllExpenses() {
        return db.query(DatabaseHelper.EXPENSE_TABLE, null, null, null, null, null, DatabaseHelper.DATE_COL + " DESC");
    }

    // Lấy các khoản thu/chi theo loại (1: income, 0: expense)
    public Cursor getExpensesByType(int type) {
        return db.query(DatabaseHelper.EXPENSE_TABLE, null, DatabaseHelper.TYPE_COL + " = ?", new String[]{String.valueOf(type)}, null, null, DatabaseHelper.DATE_COL + " DESC");
    }

    // Xóa một khoản thu/chi
    public int deleteExpense(int id) {
        return db.delete(DatabaseHelper.EXPENSE_TABLE, DatabaseHelper.EXPENSE_ID_COL + " = ?", new String[]{String.valueOf(id)});
    }

    // Cập nhật một khoản thu/chi
    public int updateExpense(int id, int type, double amount, String description, String date, int userId, int categoryId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TYPE_COL, type);
        values.put(DatabaseHelper.AMOUNT_COL, amount);
        values.put(DatabaseHelper.DESCRIPTION_COL, description);
        values.put(DatabaseHelper.DATE_COL, date);
        values.put(DatabaseHelper.EXPENSE_USER_ID_COL, userId);
        values.put(DatabaseHelper.EXPENSE_CATEGORY_ID_COL, categoryId);

        return db.update(DatabaseHelper.EXPENSE_TABLE, values, DatabaseHelper.EXPENSE_ID_COL + " = ?", new String[]{String.valueOf(id)});
    }
}
