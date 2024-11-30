package com.example.expensemanagementstudent;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class TransactionActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        dbHelper = new DatabaseHelper(this);

        // Initialize the back button
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Set the back button's onClickListener
        btnBack.setOnClickListener(v -> {
            onBackPressed(); // Handle the back action
        });

        TextInputEditText inputAmount = findViewById(R.id.input_amount);
        // Initialize the views
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.transaction_type_toggle);
        Button btnSubmit = findViewById(R.id.btn_submit);

        // Set default selected to "Expense" and update the button text
        toggleGroup.check(R.id.btn_expense);
        btnSubmit.setText("Add Expense");

        // Add listener for toggle group to update button text
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_expense) {
                    btnSubmit.setText("Add Expense");
                } else if (checkedId == R.id.btn_income) {
                    btnSubmit.setText("Add Income");
                }
            }
        });
        // Add TextWatcher to append "$" at the end
        inputAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    inputAmount.removeTextChangedListener(this);

                    String input = s.toString().replace("$", "").trim(); // Remove "$" to avoid duplication
                    if (!input.isEmpty()) {
                        current = input + " $"; // Append "$" at the end
                        inputAmount.setText(current);
                        inputAmount.setSelection(input.length()); // Set cursor position before "$"
                    }

                    inputAmount.addTextChangedListener(this);
                }
            }
        });

        // Other initialization code
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        EditText inputDate = findViewById(R.id.input_date);
        EditText inputNotes = findViewById(R.id.input_notes);

        // Set default date to today
        Calendar calendar = Calendar.getInstance();
        inputDate.setText(formatDate(calendar));

        // Date picker for transaction date
        inputDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        inputDate.setText(formatDate(calendar));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        // Set default selected to "Expense"
        toggleGroup.check(R.id.btn_expense);

        // Apply the default styles for Expense (selected) and Income (unselected)
        MaterialButton btnExpense = findViewById(R.id.btn_expense);
        MaterialButton btnIncome = findViewById(R.id.btn_income);
        btnExpense.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.toggle_selected)));
        btnExpense.setTextColor(getResources().getColor(R.color.toggle_text_selected));
        btnIncome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.toggle_unselected)));
        btnIncome.setTextColor(getResources().getColor(R.color.toggle_text_unselected));

        // Handle toggle button state changes
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btn_expense && isChecked) {
                btnExpense.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.toggle_selected)));
                btnExpense.setTextColor(getResources().getColor(R.color.toggle_text_selected));

                btnIncome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.toggle_unselected)));
                btnIncome.setTextColor(getResources().getColor(R.color.toggle_text_unselected));
            } else if (checkedId == R.id.btn_income && isChecked) {
                btnIncome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.toggle_selected)));
                btnIncome.setTextColor(getResources().getColor(R.color.toggle_text_selected));

                btnExpense.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.toggle_unselected)));
                btnExpense.setTextColor(getResources().getColor(R.color.toggle_text_unselected));
            }
        });

        btnSubmit.setOnClickListener(v -> {
            // Validate amount input
            if (inputAmount.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(inputAmount.getText().toString());
            String category = categorySpinner.getSelectedItem().toString();
            String date = inputDate.getText().toString();
            String notes = inputNotes.getText().toString();

            // Determine if it's Income or Expense
            int type = (toggleGroup.getCheckedButtonId() == R.id.btn_income) ? 1 : 0;

            // Save to the database
            long transactionId = dbHelper.addTransaction(type, amount, notes, date, 1, getCategoryId(category)); // Assuming user ID is 1 for now
            if (transactionId != -1) {
                Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after saving
            } else {
                Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Check if the touch event is outside the currently focused view
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText || view instanceof TextInputEditText) {
                // Clear focus and hide keyboard
                view.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Helper method to format the date
    private String formatDate(Calendar calendar) {
        return android.text.format.DateFormat.format("yyyy-MM-dd", calendar).toString();
    }

    // Dummy method to get category ID from name
    // Ideally, you should query the database to fetch the category ID
    private int getCategoryId(String categoryName) {
        // Example mapping (you should implement proper logic here)
        switch (categoryName) {
            case "Food": return 1;
            case "Shopping": return 2;
            case "Entertainment": return 3;
            default: return 0; // Default or unknown category
        }
    }
}
