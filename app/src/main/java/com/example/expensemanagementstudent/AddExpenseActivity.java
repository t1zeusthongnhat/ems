package com.example.expensemanagementstudent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private EditText edtAmount;
    private EditText edtNotes;
    private Button btnSave;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initViews();
        setupListeners();
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        edtAmount = findViewById(R.id.edtAmount);
        edtNotes = findViewById(R.id.edtNotes);
        btnSave = findViewById(R.id.btnSave);

        // Set default date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date(calendarView.getDate()));
    }

    private void setupListeners() {
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Calendar selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveExpense();
            }
        });
    }

    private boolean validateInput() {
        if (edtAmount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveExpense() {
        // TODO: Implement saving expense to database
        Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();
        finish();
    }
} 