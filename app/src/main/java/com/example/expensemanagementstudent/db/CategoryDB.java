package com.example.expensemanagementstudent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensemanagementstudent.db.DatabaseHelper;

public class CategoryDB {
    private SQLiteDatabase db;

    public CategoryDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public long addCategory(String name, String icon) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME_COL, name);
        values.put(DatabaseHelper.CATEGORY_ICON_COL, icon);

        return db.insert(DatabaseHelper.CATEGORY_TABLE, null, values);
    }

    public Cursor getAllCategories() {
        return db.query(DatabaseHelper.CATEGORY_TABLE, null, null, null, null, null, null);
    }
}
