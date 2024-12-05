package com.example.expensemanagementstudent.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExpenseDB {

    private SQLiteDatabase db;

    public ExpenseDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }
    //substr(e.date, 7, 4) lấy năm từ dd-MM-yyyy.
    //substr(e.date, 4, 2) lấy tháng.
    //substr(e.date, 1, 2) lấy ngày.
    //Sau đó, ghép chúng lại thành định dạng yyyy-MM-dd trước khi sử dụng strftime.
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
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), month });

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


}
