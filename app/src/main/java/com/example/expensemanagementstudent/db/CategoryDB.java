package com.example.expensemanagementstudent.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CategoryDB {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public CategoryDB(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }


    public Cursor getCategoryByName(String categoryName) {
        return db.query(
                DatabaseHelper.CATEGORY_TABLE,
                null,
                DatabaseHelper.CATEGORY_NAME_COL + " = ?",
                new String[]{categoryName},
                null,
                null,
                null
        );
    }

    @SuppressLint("Range")
    public ArrayList<String> getCategoryNamesByType(int type) {
        ArrayList<String> categoryNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM categories WHERE type = ?", new String[]{String.valueOf(type)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    categoryNames.add(cursor.getString(cursor.getColumnIndex("name")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return categoryNames;
    }

    /**
     * Add a new category to the database.
     *
     * @param name The name of the category.
     * @param icon The icon associated with the category.
     * @param type The type of the category (0 for income, 1 for expense).
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long addCategory(String name, String icon, int type) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME_COL, name);
        values.put(DatabaseHelper.CATEGORY_ICON_COL, icon);
        values.put(DatabaseHelper.CATEGORY_TYPE_COL, type); // Save category type

        return db.insert(DatabaseHelper.CATEGORY_TABLE, null, values);
    }

    /**
     * Fetch all categories from the database.
     *
     * @return A Cursor object containing all category rows.
     */
    public Cursor getAllCategories() {
        return db.query(
                DatabaseHelper.CATEGORY_TABLE,
                new String[]{DatabaseHelper.CATEGORY_ID_COL, DatabaseHelper.CATEGORY_NAME_COL, DatabaseHelper.CATEGORY_ICON_COL, DatabaseHelper.CATEGORY_TYPE_COL},
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Get all category names as a list of strings for populating UI elements (like Spinner).
     *
     * @return An ArrayList of category names.
     */
    @SuppressLint("Range")
    public ArrayList<String> getCategoryNames() {
        ArrayList<String> categoryNames = new ArrayList<>();
        Cursor cursor = getAllCategories();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                categoryNames.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_NAME_COL)));
            }
            cursor.close();
        }

        return categoryNames;
    }

    public int getCategoryId(String categoryName) {
        // Truy vấn tìm danh mục theo tên
        Cursor cursor = db.query(
                DatabaseHelper.CATEGORY_TABLE,
                new String[]{DatabaseHelper.CATEGORY_ID_COL}, // Chỉ cần lấy cột ID
                DatabaseHelper.CATEGORY_NAME_COL + " = ?", // Điều kiện WHERE
                new String[]{categoryName}, // Tham số điều kiện
                null,
                null,
                null
        );

        // Nếu tìm thấy danh mục, trả về ID
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            long categoryId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CATEGORY_ID_COL));
            cursor.close();
            return (int) categoryId;
        }

        // Nếu không tìm thấy, trả về -1
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }



    /**
     * Update a category in the database.
     *
     * @param id   The ID of the category to update.
     * @param name The new name for the category.
     * @param icon The new icon for the category.
     * @param type The new type for the category (0 for income, 1 for expense).
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateCategory(long id, String name, String icon, int type) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME_COL, name);
        values.put(DatabaseHelper.CATEGORY_ICON_COL, icon);
        values.put(DatabaseHelper.CATEGORY_TYPE_COL, type); // Update category type

        return db.update(
                DatabaseHelper.CATEGORY_TABLE,
                values,
                DatabaseHelper.CATEGORY_ID_COL + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    /**
     * Delete a category from the database.
     *
     * @param id The ID of the category to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean deleteCategory(long id) {
        return db.delete(
                DatabaseHelper.CATEGORY_TABLE,
                DatabaseHelper.CATEGORY_ID_COL + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    private void open() {
        db = dbHelper.getWritableDatabase();
    }

    private void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}