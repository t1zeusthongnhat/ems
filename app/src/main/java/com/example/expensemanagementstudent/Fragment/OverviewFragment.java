package com.example.expensemanagementstudent.Fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.expensemanagementstudent.TransactionHistoryActivity;
import com.example.expensemanagementstudent.CategoryActivity; // Import CategoryActivity
import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.TransactionHistoryActivity;
import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.example.expensemanagementstudent.db.ExpenseDB;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OverviewFragment extends Fragment {

    private TextView monthYearDisplay;
    private Calendar calendar;
    private LinearLayout notificationLayout;
    private LinearLayout transactionListContainer;
    private ExpenseDB expenseDB;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        TextView greetingText = rootView.findViewById(R.id.greetingText);
        TextView seeAllButton = rootView.findViewById(R.id.see_all_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout btnAddCategory = rootView.findViewById(R.id.btnAddCategory);
        // Initialize the database
        expenseDB = new ExpenseDB(requireContext());

        // Initialize the transaction list container
        transactionListContainer = rootView.findViewById(R.id.transaction_list_container);
        // Thêm Intent chuyển đến CategoryActivity
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến CategoryActivity
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Username");

        greetingText.setText("Hi " + username + ",");

        monthYearDisplay = rootView.findViewById(R.id.month_year_display);
        ImageView previousMonth = rootView.findViewById(R.id.previous_month);
        ImageView nextMonth = rootView.findViewById(R.id.next_month);

        calendar = Calendar.getInstance();
        updateMonthYearDisplay();

        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                updateMonthYearDisplay();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                updateMonthYearDisplay();
            }
        });

        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        // Load transactions dynamically into the container
        loadTransactions();

        return rootView;
    }

    private void updateMonthYearDisplay() {
        // Force the locale to English
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        String formattedDate = dateFormat.format(calendar.getTime());
        monthYearDisplay.setText(formattedDate);
    }
    private void toggleNotification() {
        if (notificationLayout.getVisibility() == View.GONE) {
            notificationLayout.setVisibility(View.VISIBLE);
            notificationLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        } else {
            notificationLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
            notificationLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        loadTransactions(); // Reload the transactions when the fragment is resumed
    }

    private void loadTransactions() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        transactionListContainer.removeAllViews(); // Clear previous transactions
        Cursor cursor = expenseDB.getTransactionsByUserId(userId);
        int limit = 2; // Show only two newest transactions
        int count = 0;

        if (cursor != null) {
            while (cursor.moveToNext() && count < limit) {
                String category = cursor.getString(cursor.getColumnIndex("category_name"));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRIPTION_COL));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.AMOUNT_COL));
                int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COL));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COL));

                addTransactionItem(category, description, amount, type, date);
                count++;
            }
            cursor.close();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadTransactions(); // Reload transactions after adding a new one
        }
    }

    private void addTransactionItem(String category, String description, double amount, int type, String date) {
        View transactionItem = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item_overview, transactionListContainer, false);

        TextView categoryView = transactionItem.findViewById(R.id.transaction_category);
        TextView descriptionView = transactionItem.findViewById(R.id.transaction_description);
        TextView amountView = transactionItem.findViewById(R.id.transaction_amount);
        TextView dateView = transactionItem.findViewById(R.id.transaction_date);

        // Find the Edit and Delete buttons
        View editButton = transactionItem.findViewById(R.id.btn_edit_transaction);
        View deleteButton = transactionItem.findViewById(R.id.btn_delete_transaction);

        // Hide the buttons
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        categoryView.setText(category);
        descriptionView.setText(description);
        dateView.setText(date);

        // Format the amount with a "+" or "-" and apply color based on type
        if (type == 1) { // Income
            amountView.setText(String.format("+ %, .2f $", amount));
            amountView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else { // Expense
            amountView.setText(String.format("- %, .2f $", amount));
            amountView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        transactionListContainer.addView(transactionItem);
    }


}