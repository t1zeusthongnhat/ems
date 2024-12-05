package com.example.expensemanagementstudent;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagementstudent.adapter.TransactionAdapter;
import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.example.expensemanagementstudent.db.ExpenseDB;
import com.example.expensemanagementstudent.model.Transaction;

import java.util.ArrayList;

public class TransactionHistoryActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private ArrayList<Transaction> transactions;
    private ExpenseDB expenseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        ImageButton btnBack = findViewById(R.id.btnBack);
        // Initialize views and components
        initializeComponents();
        btnBack.setOnClickListener(v -> {
            // Navigate back to the previous activity or close the current activity
            onBackPressed();
        });
    }

    /**
     * Initialize views and database components.
     */
    private void initializeComponents() {
        Button filterButton = findViewById(R.id.btn_transaction_history);
        recyclerView = findViewById(R.id.recycler_view_transactions);
        expenseDB = new ExpenseDB(this);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load transactions and set adapter
        transactions = loadTransactions();
        adapter = new TransactionAdapter(this, transactions);
        recyclerView.setAdapter(adapter);

        // Set filter button click listener
        filterButton.setOnClickListener(v -> openFilterDialog());
    }

    /**
     * Load transactions from the database.
     */
    private ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Cursor cursor = expenseDB.getAllTransactions();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COL));
                String category = cursor.getString(cursor.getColumnIndex("category_name"));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.AMOUNT_COL));
                int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COL));

                // Format amount as currency
                String formattedAmount = String.format("%,.2f $", amount);

                // Add transaction to the list
                transactions.add(new Transaction(date, category, formattedAmount, type));
            }
            cursor.close();
        }

        return transactions;
    }

    /**
     * Open the filter dialog to filter transactions.
     */
    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Transactions");

        // Inflate filter dialog layout
        View filterView = getLayoutInflater().inflate(R.layout.dialog_filter_transactions, null);
        builder.setView(filterView);

        // Initialize filter components
        Spinner typeSpinner = filterView.findViewById(R.id.filter_type_spinner);
        Spinner categorySpinner = filterView.findViewById(R.id.filter_category_spinner);

        // Populate type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"All", "Income", "Expense"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Populate category spinner
        ArrayList<String> categories = loadCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        builder.setPositiveButton("Apply Filters", (dialog, which) -> {
            String selectedType = typeSpinner.getSelectedItem().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            // Apply filters and update RecyclerView
            ArrayList<Transaction> filteredTransactions = applyFilters(selectedType, selectedCategory);
            adapter.updateTransactions(filteredTransactions);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Load all categories from the database.
     */
    private ArrayList<String> loadCategories() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All"); // Add default "All" option
        Cursor cursor = expenseDB.getAllTransactions(); // Replace with your category fetch logic

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String category = cursor.getString(cursor.getColumnIndex("category_name"));
                if (!categories.contains(category)) {
                    categories.add(category);
                }
            }
            cursor.close();
        }

        return categories;
    }

    /**
     * Apply filters and fetch the filtered transactions.
     */
    private ArrayList<Transaction> applyFilters(String type, String category) {
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();
        Cursor cursor;

        if (type.equals("All") && category.equals("All")) {
            cursor = expenseDB.getAllTransactions();
        } else if (!type.equals("All") && category.equals("All")) {
            int transactionType = type.equals("Income") ? 0 : 1;
            cursor = expenseDB.getTransactionsByType(transactionType);
        } else if (type.equals("All") && !category.equals("All")) {
            cursor = expenseDB.getTransactionsByCategory(category);
        } else {
            int transactionType = type.equals("Income") ? 0 : 1;
            int categoryId = getCategoryId(category); // Implement getCategoryId logic
            cursor = expenseDB.getFilteredTransactions(transactionType, categoryId);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COL));
                String transactionCategory = cursor.getString(cursor.getColumnIndex("category_name"));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.AMOUNT_COL));
                int transactionType = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COL));

                filteredTransactions.add(new Transaction(date, transactionCategory, String.format("%,.2f $", amount), transactionType));
            }
            cursor.close();
        }

        return filteredTransactions;
    }

    /**
     * Get the category ID from the category name.
     */
    private int getCategoryId(String categoryName) {
        Cursor cursor = expenseDB.getReadableDatabase().rawQuery(
                "SELECT " + DatabaseHelper.CATEGORY_ID_COL + " FROM " + DatabaseHelper.CATEGORY_TABLE +
                        " WHERE " + DatabaseHelper.CATEGORY_NAME_COL + " = ?",
                new String[]{categoryName}
        );
        int categoryId = -1;
        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(0);
        }
        cursor.close();
        return categoryId;
    }
}
