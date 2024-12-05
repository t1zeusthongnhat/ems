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


}
