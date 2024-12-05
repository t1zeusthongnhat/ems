package com.example.expensemanagementstudent.Fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.expensemanagementstudent.CategoryActivity;
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

        // Add intent to navigate to CategoryActivity
        btnAddCategory.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CategoryActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Username");

        greetingText.setText("Hi " + username + ",");

        monthYearDisplay = rootView.findViewById(R.id.month_year_display);
        ImageView previousMonth = rootView.findViewById(R.id.previous_month);
        ImageView nextMonth = rootView.findViewById(R.id.next_month);

        calendar = Calendar.getInstance();
        updateMonthYearDisplay();

        previousMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYearDisplay();
        });
        nextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYearDisplay();
        });

        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Load transactions dynamically into the container
        loadTransactions();

        return rootView;
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        String formattedDate = dateFormat.format(calendar.getTime());
        monthYearDisplay.setText(formattedDate);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadTransactions(); // Reload the transactions when the fragment is resumed
    }

    private void loadTransactions() {
        Cursor cursor = expenseDB.getAllTransactions();
        if (cursor != null) {
            int count = 0; // Counter to limit transactions
            while (cursor.moveToNext() && count < 2) { // Limit to 2 transactions
                String category = cursor.getString(cursor.getColumnIndex("category_name"));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRIPTION_COL));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.AMOUNT_COL));
                int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COL));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COL)); // Fetch the date

                addTransactionItem(category, description, amount, type, date); // Pass the date

                count++; // Increment counter
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
        View transactionItem = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, transactionListContainer, false);

        TextView categoryView = transactionItem.findViewById(R.id.transaction_category);
        TextView descriptionView = transactionItem.findViewById(R.id.transaction_description);
        TextView amountView = transactionItem.findViewById(R.id.transaction_amount);
        TextView dateView = transactionItem.findViewById(R.id.transaction_date); // Ensure this ID exists

        categoryView.setText(category);
        descriptionView.setText(description);
        amountView.setText(String.format("%,.2f $", amount));
        dateView.setText(date); // Set the date text

        if (type == 1) { // Expense
            amountView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else { // Income
            amountView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        transactionListContainer.addView(transactionItem);
    }

}
