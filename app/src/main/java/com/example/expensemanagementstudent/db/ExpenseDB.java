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

    // Thêm một giao dịch thu/chi
    public long addTransaction(int type, double amount, String description, String date, int userId, int categoryId) {
        ContentValues values = new ContentValues();

        // Gắn các giá trị tương ứng cho các cột
        values.put(DatabaseHelper.TYPE_COL, type); // Loại giao dịch (0: thu nhập, 1: chi tiêu)
        values.put(DatabaseHelper.AMOUNT_COL, amount); // Số tiền giao dịch
        values.put(DatabaseHelper.DESCRIPTION_COL, description); // Mô tả giao dịch
        values.put(DatabaseHelper.DATE_COL, date); // Ngày giao dịch
        values.put(DatabaseHelper.EXPENSE_USER_ID_COL, userId); // ID người dùng
        values.put(DatabaseHelper.EXPENSE_CATEGORY_ID_COL, categoryId); // ID danh mục giao dịch

        // Thêm dữ liệu vào bảng "expenses"
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
    // Tính tổng thu nhập cho một người dùng
    public double getTotalIncome(int userId) {
        double totalIncome = 0.0;
        String query = "SELECT SUM(" + DatabaseHelper.AMOUNT_COL + ") FROM " + DatabaseHelper.EXPENSE_TABLE +
                " WHERE " + DatabaseHelper.EXPENSE_USER_ID_COL + " = ? AND " + DatabaseHelper.TYPE_COL + " = 0"; // 0: thu nhập
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0);
        }
        cursor.close();
        return totalIncome;
    }

    // Tính tổng chi tiêu cho một người dùng
    public double getTotalExpense(int userId) {
        double totalExpense = 0.0;
        String query = "SELECT SUM(" + DatabaseHelper.AMOUNT_COL + ") FROM " + DatabaseHelper.EXPENSE_TABLE +
                " WHERE " + DatabaseHelper.EXPENSE_USER_ID_COL + " = ? AND " + DatabaseHelper.TYPE_COL + " = 1"; // 1: chi tiêu
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        return totalExpense;
    }




}