package com.example.expensemanagementstudent;

import android.app.AlertDialog;
import android.content.SharedPreferences;
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

import com.example.expensemanagementstudent.adapter.TransactionAdapterOv;
import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.example.expensemanagementstudent.db.ExpenseDB;
import com.example.expensemanagementstudent.model.TransactionOverview;

import java.util.ArrayList;

public class TransactionHistoryActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private TransactionAdapterOv adapter;
    private ArrayList<TransactionOverview> transactionOverviews;
    private ExpenseDB expenseDB;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Retrieve the logged-in user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            // If no user is logged in, handle the error
            finish(); // Close the activity
            return;
        }

        // Initialize views and components
        initializeComponents();
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

        // Load transactionOverviews and set adapter
        transactionOverviews = loadTransactions();
        adapter = new TransactionAdapterOv(this, transactionOverviews);
        recyclerView.setAdapter(adapter);

        // Set filter button click listener
        filterButton.setOnClickListener(v -> openFilterDialog());
    }

    /**
     * Load transactionOverviews for the logged-in user from the database.
     */
    private ArrayList<TransactionOverview> loadTransactions() {
        ArrayList<TransactionOverview> transactionOverviews = new ArrayList<>();
        Cursor cursor = expenseDB.getTransactionsForUser(userId); // Fetch transactionOverviews for the specific user

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COL));
                String category = cursor.getString(cursor.getColumnIndex("category_name"));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.AMOUNT_COL));
                int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COL));

                // Format amount as currency
                String formattedAmount = String.format("%,.2f $", amount);

                // Add transaction to the list
                transactionOverviews.add(new TransactionOverview(date, category, formattedAmount, type));
            }
            cursor.close();
        }

        return transactionOverviews;
    }

    /**
     * Open the filter dialog to filter transactionOverviews.
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
            ArrayList<TransactionOverview> filteredTransactionOverviews = applyFilters(selectedType, selectedCategory);
            adapter.updateTransactions(filteredTransactionOverviews);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Load all categories for the logged-in user.
     */
    private ArrayList<String> loadCategories() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All"); // Add default "All" option
        Cursor cursor = expenseDB.getTransactionsForUser(userId); // Fetch user-specific categories

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
     * Apply filters and fetch the filtered transactionOverviews for the logged-in user.
     */
    private ArrayList<TransactionOverview> applyFilters(String type, String category) {
        ArrayList<TransactionOverview> filteredTransactionOverviews = new ArrayList<>();
        Cursor cursor;

        if (type.equals("All") && category.equals("All")) {
            cursor = expenseDB.getTransactionsForUser(userId);
        } else if (!type.equals("All") && category.equals("All")) {
            int transactionType = type.equals("Income") ? 0 : 1;
            cursor = expenseDB.getFilteredTransactionsForUser(userId, transactionType, -1);
        } else if (type.equals("All") && !category.equals("All")) {
            int categoryId = getCategoryId(category);
            cursor = expenseDB.getFilteredTransactionsForUser(userId, -1, categoryId);
        } else {
            int transactionType = type.equals("Income") ? 0 : 1;
            int categoryId = getCategoryId(category);
            cursor = expenseDB.getFilteredTransactionsForUser(userId, transactionType, categoryId);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COL));
                String transactionCategory = cursor.getString(cursor.getColumnIndex("category_name"));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.AMOUNT_COL));
                int transactionType = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COL));

                filteredTransactionOverviews.add(new TransactionOverview(date, transactionCategory, String.format("%,.2f $", amount), transactionType));
            }
            cursor.close();
        }

        return filteredTransactionOverviews;
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