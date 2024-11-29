package com.example.expensemanagementstudent;

import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.CategoryDB;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

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

        categoryDB = new CategoryDB(this);

        lvCategories.setOnItemLongClickListener((parent, view, position, id) -> {
            // Tạo View từ file custom_popup_menu
            View popupView = getLayoutInflater().inflate(R.layout.custom_popup_menu, null);

            // Tạo PopupWindow
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            // Hiển thị PopupWindow tại vị trí của View được nhấn giữ
            popupWindow.showAsDropDown(view);

            // Xử lý các hành động trong PopupWindow
            LinearLayout actionUpdate = popupView.findViewById(R.id.action_update);
            LinearLayout actionDelete = popupView.findViewById(R.id.action_delete);

            // Khi người dùng chọn Update
            actionUpdate.setOnClickListener(v -> {
                popupWindow.dismiss(); // Đóng popup
                showUpdateDialog(position); // Mở dialog update
            });

            // Khi người dùng chọn Delete
            actionDelete.setOnClickListener(v -> {
                popupWindow.dismiss(); // Đóng popup
                deleteCategory(position); // Gọi hàm xóa category
            });

            return true; // Xử lý long click
        });


        displayCategories();

        // Hide keyboard when touching outside
        setupTouchListener(findViewById(R.id.rootCate));

        // Populate GridView with icons
        setupIconGrid();

        // Handle add category button
        btnAddCategory.setOnClickListener(v -> addCategory());
    }

    @SuppressLint("Range")
    private void showUpdateDialog(int position) {
        // Lấy Cursor từ vị trí
        Cursor cursor = (Cursor) lvCategories.getItemAtPosition(position);

        // Kiểm tra con trỏ có hợp lệ hay không
        if (cursor == null || cursor.isClosed()) {
            Toast.makeText(this, "Unable to fetch category data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu từ Cursor
        String currentName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_NAME_COL));
        String currentIcon = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_ICON_COL));

        // Tạo Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_category, null);
        builder.setView(dialogView);

        // Ánh xạ các thành phần trong dialog
        EditText etUpdateName = dialogView.findViewById(R.id.etUpdateCategoryName);
        EditText etUpdateIcon = dialogView.findViewById(R.id.etUpdateCategoryIcon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) GridView gvIcons = dialogView.findViewById(R.id.gvIcons);

        // Gán dữ liệu hiện tại vào các EditText
        etUpdateName.setText(currentName);
        etUpdateIcon.setText(currentIcon);

        // Thiết lập GridView cho icon
        setupIconGridForDialog(gvIcons, etUpdateIcon);

        // Nút cập nhật
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = etUpdateName.getText().toString().trim();
            String newIcon = etUpdateIcon.getText().toString().trim();

            if (!newName.isEmpty() && !newIcon.isEmpty()) {
                categoryDB.updateCategory(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CATEGORY_ID_COL)),
                        newName,
                        newIcon
                );
                displayCategories(); // Refresh ListView
                Toast.makeText(this, "Category updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút hủy
        builder.setNegativeButton("Cancel", null);

        // Hiển thị dialog
        builder.create().show();
    }


    // Xử lý GridView icon trong dialog
    private void setupIconGridForDialog(GridView gvIcons, EditText etIcon) {
        // Tạo danh sách icon tương tự như thêm mới
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

        // Adapter cho GridView
        com.example.expensemanagementstudent.IconAdapter adapter = new com.example.expensemanagementstudent.IconAdapter(this, iconList);
        gvIcons.setAdapter(adapter);

        // Xử lý chọn icon
        gvIcons.setOnItemClickListener((parent, view, position, id) -> {
            IconItem selectedIcon = iconList.get(position);
            etIcon.setText(selectedIcon.getIconName());
        });
    }


    // Xử lý xóa category
    private void deleteCategory(int position) {
        Cursor cursor = (Cursor) lvCategories.getItemAtPosition(position);
        @SuppressLint("Range") String categoryId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_ID_COL));

        categoryDB.deleteCategory(Long.parseLong(categoryId));
        Toast.makeText(this, "Category deleted successfully!", Toast.LENGTH_SHORT).show();
        displayCategories();
    }
    private void setupIconGrid() {
        // Tạo danh sách icon (id drawable và tên tương ứng)
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

        // Adapter cho GridView
        com.example.expensemanagementstudent.IconAdapter adapter = new com.example.expensemanagementstudent.IconAdapter(this, iconList);
        gvIcons.setAdapter(adapter);

        // Khi người dùng chọn icon
        gvIcons.setOnItemClickListener((parent, view, position, id) -> {
            IconItem selectedIcon = iconList.get(position);
            etCategoryIcon.setText(selectedIcon.getIconName());
            gvIcons.setVisibility(View.GONE); // Ẩn GridView sau khi chọn
        });

        // Khi người dùng nhấn vào EditText
        etCategoryIcon.setOnClickListener(v -> {
            if (gvIcons.getVisibility() == View.GONE) {
                gvIcons.setVisibility(View.VISIBLE); // Hiển thị GridView
            } else {
                gvIcons.setVisibility(View.GONE); // Ẩn nếu đang hiển thị
            }
        });

        // Ẩn GridView khi người dùng nhấn ra ngoài
        findViewById(R.id.rootCate).setOnTouchListener((v, event) -> {
            if (gvIcons.getVisibility() == View.VISIBLE) {
                gvIcons.setVisibility(View.GONE); // Ẩn GridView
            }
            return false;
        });
    }



    private void addCategory() {
        String categoryName = etCategoryName.getText().toString().trim();
        String categoryIcon = etCategoryIcon.getText().toString().trim();

        if (categoryName.isEmpty() || categoryIcon.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            long result = categoryDB.addCategory(categoryName, categoryIcon);

            if (result != -1) {
                Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                etCategoryName.setText("");
                etCategoryIcon.setText("");
                displayCategories();
            } else {
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayCategories() {
        Cursor cursor = categoryDB.getAllCategories();

        if (cursor != null && cursor.getCount() > 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{DatabaseHelper.CATEGORY_NAME_COL, DatabaseHelper.CATEGORY_ICON_COL},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );
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
