package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoryDB {
    private SQLiteDatabase db;

    public CategoryDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // Thêm danh mục
    public long addCategory(String name, String icon) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME_COL, name);
        values.put(DatabaseHelper.CATEGORY_ICON_COL, icon);

        return db.insert(DatabaseHelper.CATEGORY_TABLE, null, values);
    }

    // Lấy tất cả danh mục
    public Cursor getAllCategories() {
        return db.query(DatabaseHelper.CATEGORY_TABLE, null, null, null, null, null, null);
    }

    // Lấy danh mục theo ID
    public Cursor getCategoryById(long id) {
        return db.query(
                DatabaseHelper.CATEGORY_TABLE,
                null,
                DatabaseHelper.CATEGORY_ID_COL + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    // Cập nhật danh mục
    public boolean updateCategory(long id, String name, String icon) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME_COL, name);
        values.put(DatabaseHelper.CATEGORY_ICON_COL, icon);

        return db.update(
                DatabaseHelper.CATEGORY_TABLE,
                values,
                DatabaseHelper.CATEGORY_ID_COL + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // Xóa danh mục
    public boolean deleteCategory(long id) {
        return db.delete(
                DatabaseHelper.CATEGORY_TABLE,
                DatabaseHelper.CATEGORY_ID_COL + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
}
