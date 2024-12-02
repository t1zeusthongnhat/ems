package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class CategoryDB {
    private SQLiteDatabase db;

    public CategoryDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * Add a new category to the database.
     *
     * @param name The name of the category.
     * @param icon The icon associated with the category.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long addCategory(String name, String icon) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME_COL, name);
        values.put(DatabaseHelper.CATEGORY_ICON_COL, icon);

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
                new String[]{DatabaseHelper.CATEGORY_ID_COL, DatabaseHelper.CATEGORY_NAME_COL, DatabaseHelper.CATEGORY_ICON_COL},
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

    /**
     * Get a specific category by its ID.
     *
     * @param id The ID of the category.
     * @return A Cursor object pointing to the specific category row.
     */
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

    /**
     * Update a category in the database.
     *
     * @param id   The ID of the category to update.
     * @param name The new name for the category.
     * @param icon The new icon for the category.
     * @return True if the update was successful, false otherwise.
     */
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
}

