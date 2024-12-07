package com.example.expensemanagementstudent;

import androidx.appcompat.widget.Toolbar;

import com.example.expensemanagementstudent.adapter.CategoryAdapter;
import com.example.expensemanagementstudent.adapter.IconAdapter;
import com.example.expensemanagementstudent.db.CategoryDB;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.example.expensemanagementstudent.model.IconItem;

import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private EditText etCategoryName, etCategoryIcon;
    private Button btnAddCategory;
    private GridView gvIcons;
    private ListView lvCategories;
    private CategoryDB categoryDB;
    Spinner spCategoryType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        categoryDB = new CategoryDB(this);
        spCategoryType = findViewById(R.id.spCategoryType);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // UI Elements
        etCategoryName = findViewById(R.id.etCategoryName);
        etCategoryIcon = findViewById(R.id.etCategoryIcon);
        btnAddCategory = findViewById(R.id.btnAddCate);
        gvIcons = findViewById(R.id.gvIcons);
        lvCategories = findViewById(R.id.lvCategories);

        lvCategories.setOnItemLongClickListener((parent, view, position, id) -> {
            // Create View from custom_popup_menu layout
            View popupView = getLayoutInflater().inflate(R.layout.custom_popup_menu, null);

            // Create PopupWindow
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            // Display PopupWindow either above or below the item, depending on available space
            int[] location = new int[2];
            view.getLocationOnScreen(location);

            popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = popupView.getMeasuredHeight();

            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            if (location[1] + view.getHeight() + popupHeight > screenHeight) {
                // Show above
                popupWindow.showAsDropDown(view, 0, -view.getHeight() - popupHeight);
            } else {
                // Show below
                popupWindow.showAsDropDown(view);
            }

            // Handle actions in PopupWindow
            LinearLayout actionUpdate = popupView.findViewById(R.id.action_update);
            LinearLayout actionDelete = popupView.findViewById(R.id.action_delete);

            // When user selects Update
            actionUpdate.setOnClickListener(v -> {
                popupWindow.dismiss(); // Close popup
                showUpdateDialog(position); // Open update dialog
            });

            // When user selects Delete
            actionDelete.setOnClickListener(v -> {
                popupWindow.dismiss(); // Close popup
                deleteCategory(position); // Call delete category function
            });

            return true; // Handle long click
        });

        displayCategories();

        // Hide keyboard when touching outside
        setupTouchListener(findViewById(R.id.rootCate));

        // Populate GridView with icons
        setupIconGrid();

        // Handle add category button
        btnAddCategory.setOnClickListener(v -> addCategory());
    }

    private void showUpdateDialog(int position) {
        try {
            Cursor cursor = (Cursor) lvCategories.getItemAtPosition(position);
            if (cursor == null || cursor.isClosed()) {
                Toast.makeText(this, "Unable to fetch category data!", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_NAME_COL));
            String currentIcon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ICON_COL));
            int currentType = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_TYPE_COL));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_category, null);
            builder.setView(dialogView);

            EditText etUpdateName = dialogView.findViewById(R.id.etUpdateCategoryName);
            EditText etUpdateIcon = dialogView.findViewById(R.id.etUpdateCategoryIcon);
            Spinner spUpdateCategoryType = dialogView.findViewById(R.id.spUpdateCategoryType);
            GridView gvUpdateIcons = dialogView.findViewById(R.id.gvIcons);

            etUpdateName.setText(currentName);
            etUpdateIcon.setText(currentIcon);
            spUpdateCategoryType.setSelection(currentType);

            // Set up GridView for selecting icon
            setupIconGridForDialog(gvUpdateIcons, etUpdateIcon);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newName = etUpdateName.getText().toString().trim();
                String newIcon = etUpdateIcon.getText().toString().trim();
                int newType = spUpdateCategoryType.getSelectedItemPosition();

                if (!newName.isEmpty() && !newIcon.isEmpty()) {
                    categoryDB.updateCategory(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ID_COL)),
                            newName,
                            newIcon,
                            newType
                    );
                    displayCategories();
                    Toast.makeText(this, "Category updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating category: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Handle GridView icons in dialog
    private void setupIconGridForDialog(GridView gvIcons, EditText etIcon) {
        // Create icon list similar to adding new
        List<IconItem> iconList = Arrays.asList(
                new IconItem(R.drawable.ic_company, "ic_company"),
                new IconItem(R.drawable.ic_shoppingg, "ic_shopping"),
                new IconItem(R.drawable.ic_foodd, "ic_food"),
                new IconItem(R.drawable.ic_transport, "ic_transport"),
                new IconItem(R.drawable.ic_health, "ic_health"),
                new IconItem(R.drawable.ic_travell, "ic_travel"),
                new IconItem(R.drawable.ic_entertainment, "ic_entertainment"),
                new IconItem(R.drawable.ic_saving, "ic_saving")
        );

        // Adapter for GridView
     IconAdapter adapter = new IconAdapter(this, iconList);
        gvIcons.setAdapter(adapter);

        // Handle icon selection
        gvIcons.setOnItemClickListener((parent, view, position, id) -> {
            IconItem selectedIcon = iconList.get(position);
            etIcon.setText(selectedIcon.getIconName());
        });
    }

    // Handle category deletion
    // Handle category deletion
    private void deleteCategory(int position) {
        Cursor cursor = (Cursor) lvCategories.getItemAtPosition(position);
        @SuppressLint("Range") long categoryId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CATEGORY_ID_COL));

        // Kiểm tra xem danh mục có đang được sử dụng trong bảng expense hay không
        if (categoryDB.isCategoryUsedInExpenses(categoryId)) {
            // Thông báo cho người dùng
            Toast.makeText(this, "This category is currently in use in expenses. Please delete the expenses first..", Toast.LENGTH_LONG).show();
        } else {
            // Tiến hành xóa khi không có khoản chi tiêu liên kết
            categoryDB.deleteCategory(categoryId);
            Toast.makeText(this, "Category deleted successfully!", Toast.LENGTH_SHORT).show();
            displayCategories();
        }
    }

    private void setupIconGrid() {
        List<IconItem> iconList = Arrays.asList(
                new IconItem(R.drawable.ic_company, "ic_company"),
                new IconItem(R.drawable.ic_shoppingg, "ic_shopping"),
                new IconItem(R.drawable.ic_foodd, "ic_food"),
                new IconItem(R.drawable.ic_transport, "ic_transport"),
                new IconItem(R.drawable.ic_health, "ic_health"),
                new IconItem(R.drawable.ic_travell, "ic_travel"),
                new IconItem(R.drawable.ic_entertainment, "ic_entertainment"),
                new IconItem(R.drawable.ic_saving, "ic_saving")
        );

        IconAdapter adapter = new IconAdapter(this, iconList);
        gvIcons.setAdapter(adapter);

        gvIcons.setOnItemClickListener((parent, view, position, id) -> {
            IconItem selectedIcon = iconList.get(position);
            etCategoryIcon.setText(selectedIcon.getIconName());
            gvIcons.setVisibility(View.GONE);
        });

        etCategoryIcon.setOnClickListener(v -> {
            if (gvIcons.getVisibility() == View.GONE) {
                gvIcons.setVisibility(View.VISIBLE);
            } else {
                gvIcons.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.rootCate).setOnTouchListener((v, event) -> {
            if (gvIcons.getVisibility() == View.VISIBLE) {
                gvIcons.setVisibility(View.GONE);
            }
            return false;
        });
    }


    private void addCategory() {
        String categoryName = etCategoryName.getText().toString().trim();
        String categoryIcon = etCategoryIcon.getText().toString().trim();
        int type = spCategoryType.getSelectedItemPosition(); // 0 = Income, 1 = Expense

        if (categoryName.isEmpty() || categoryIcon.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            long result = categoryDB.addCategory(categoryName, categoryIcon, type);

            if (result != -1) {
                Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                etCategoryName.setText("");
                etCategoryIcon.setText("");
                spCategoryType.setSelection(0); // Reset Spinner
                displayCategories();
            } else {
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayCategories() {
        Cursor cursor = categoryDB.getAllCategories();
        if (cursor != null && cursor.getCount() > 0) {
            CategoryAdapter adapter = new CategoryAdapter(this, cursor, 0);
            lvCategories.setAdapter(adapter);
        } else {
            lvCategories.setAdapter(null);
        }
    }


    private void setupTouchListener(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard();
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupTouchListener(innerView);
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
