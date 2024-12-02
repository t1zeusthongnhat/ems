package com.example.expensemanagementstudent;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.CategoryDB;
import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
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
        TextView amountErrorMessage = findViewById(R.id.amount_error_message);

        // Add TextWatcher to validate the amount
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

                    String input = s.toString().replace(".", "").replace("$", "").trim(); // Remove formatting
                    if (!input.isEmpty()) {
                        try {
                            long amount = Long.parseLong(input);
                            if (amount > 1_000_000_000) {
                                // Show error
                                amountErrorMessage.setText("Maximum amount is 999.999.999.");
                                amountErrorMessage.setVisibility(View.VISIBLE);

                                /*// Set the text color and underline the input layout
                                ((TextInputLayout) inputAmount.getParent().getParent()).setErrorEnabled(true);
                                ((TextInputLayout) inputAmount.getParent().getParent()).setError("Invalid Amount");*/

                            } else {
                                // Hide error if amount is valid
                                amountErrorMessage.setVisibility(View.GONE);
                                ((TextInputLayout) inputAmount.getParent().getParent()).setErrorEnabled(false);
                            }
                            current = formatAmount(amount) + " $"; // Format the input
                            inputAmount.setText(current);
                            inputAmount.setSelection(current.length() - 2); // Set cursor before "$"
                        } catch (NumberFormatException e) {
                            amountErrorMessage.setVisibility(View.GONE);
                        }
                    } else {
                        amountErrorMessage.setVisibility(View.GONE);
                    }

                    inputAmount.addTextChangedListener(this);
                }
            }

            // Helper method to format the amount
            private String formatAmount(long amount) {
                return String.format("%,d", amount).replace(",", ".");
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
                        current = input + "$"; // Append "$" at the end
                        inputAmount.setText(current);
                        inputAmount.setSelection(input.length()); // Set cursor position before "$"
                    }

                    inputAmount.addTextChangedListener(this);
                }
            }

        });

        /**
         * Get category from database in add transaction.
         */
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        CategoryDB categoryDB = new CategoryDB(this);

        // Fetch category names from the database
        ArrayList<String> categoryNames = categoryDB.getCategoryNames();
        // Set up the ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, // Layout for the dropdown items
                categoryNames
        );




        // Set the dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach the adapter to the Spinner
        categorySpinner.setAdapter(adapter);


        // Set the dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach the adapter to the Spinner
        categorySpinner.setAdapter(adapter);

        // Other initialization code
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
        inputAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    inputAmount.removeTextChangedListener(this);

                    try {
                        String input = s.toString().replaceAll("[,.\\s$]", ""); // Remove existing formatting
                        if (!input.isEmpty()) {
                            long parsedAmount = Long.parseLong(input); // Parse the number

                            if (parsedAmount > 1_000_000_000) { // Check for max limit
                                amountErrorMessage.setText("Maximum amount is 999,999,999.99");
                                amountErrorMessage.setVisibility(View.VISIBLE);
                            } else {
                                amountErrorMessage.setVisibility(View.GONE);
                            }

                            current = String.format("%,d", parsedAmount).replace(",", ".") + " $"; // Add formatting
                            inputAmount.setText(current);
                            inputAmount.setSelection(current.length() - 2); // Move cursor before "$"
                        } else {
                            amountErrorMessage.setVisibility(View.GONE);
                        }
                    } catch (NumberFormatException e) {
                        amountErrorMessage.setVisibility(View.GONE);
                    }

                    inputAmount.addTextChangedListener(this);
                }
            }
        });
        btnSubmit.setOnClickListener(v -> {
            String amountText = inputAmount.getText().toString().replaceAll("[,.\\s$]", "").trim(); // Remove formatting
            if (amountText.isEmpty()) {
                amountErrorMessage.setText("Amount is required");
                amountErrorMessage.setVisibility(View.VISIBLE);
                return;
            }

            try {
                long amount = Long.parseLong(amountText);
                if (amount > 1_000_000_000) {
                    amountErrorMessage.setText("Maximum amount is 999,999,999.99");
                    amountErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                amountErrorMessage.setVisibility(View.GONE); // Clear error if valid

                String category = categorySpinner.getSelectedItem().toString();
                String date = inputDate.getText().toString();
                String notes = inputNotes.getText().toString();

                int type = (toggleGroup.getCheckedButtonId() == R.id.btn_income) ? 1 : 0;

                long transactionId = dbHelper.addTransaction(type, amount, notes, date, 1, getCategoryId(category));
                if (transactionId != -1) {
                    Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                amountErrorMessage.setText("Invalid amount entered");
                amountErrorMessage.setVisibility(View.VISIBLE);
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
        return android.text.format.DateFormat.format("dd-MM-yyyy", calendar).toString();
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