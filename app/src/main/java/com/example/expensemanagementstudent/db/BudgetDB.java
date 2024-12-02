package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BudgetDB {
    private DatabaseHelper dbHelper;

    public BudgetDB(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Add a new budget
    public long addBudget(double amount, int type, int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.BUDGET_AMOUNT_COL, amount);
        values.put(DatabaseHelper.BUDGET_TYPE_COL, type);
        values.put(DatabaseHelper.BUDGET_CATEGORY_ID_COL, categoryId);
        return db.insert(DatabaseHelper.BUDGET_TABLE, null, values);
    }

    // Get all budgets
    public Cursor getAllBudgets() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DatabaseHelper.BUDGET_TABLE, null);
    }

    // Get budgets by type
    public Cursor getBudgetsByType(int type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DatabaseHelper.BUDGET_TABLE +
                " WHERE " + DatabaseHelper.BUDGET_TYPE_COL + "=?", new String[]{String.valueOf(type)});
    }

    // Get total budget for a category
    public double getBudgetByCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.BUDGET_AMOUNT_COL +
                " FROM " + DatabaseHelper.BUDGET_TABLE +
                " WHERE " + DatabaseHelper.BUDGET_CATEGORY_ID_COL + "=?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        cursor.close();
        return 0;
    }

    // Update a budget
    public int updateBudget(int id, double amount, int type, int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.BUDGET_AMOUNT_COL, amount);
        values.put(DatabaseHelper.BUDGET_TYPE_COL, type);
        values.put(DatabaseHelper.BUDGET_CATEGORY_ID_COL, categoryId);
        return db.update(DatabaseHelper.BUDGET_TABLE, values,
                DatabaseHelper.BUDGET_ID_COL + "=?", new String[]{String.valueOf(id)});
    }

    // Delete a budget
    public int deleteBudget(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.BUDGET_TABLE,
                DatabaseHelper.BUDGET_ID_COL + "=?", new String[]{String.valueOf(id)});
    }
}
