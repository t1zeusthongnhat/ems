package com.example.expensemanagementstudent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.db.CategoryDB;
import com.example.expensemanagementstudent.db.DatabaseHelper;

public class CategoryActivity extends AppCompatActivity {

    private EditText etCategoryName, etCategoryIcon;
    private Button btnAddCategory;
    private ListView lvCategories;
    private CategoryDB categoryDB;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Kết nối các thành phần giao diện
        etCategoryName = findViewById(R.id.etCategoryName);
        etCategoryIcon = findViewById(R.id.etCategoryIcon);
        btnAddCategory = findViewById(R.id.btnAddCate);
        lvCategories = findViewById(R.id.lvCategories);

        // Khởi tạo cơ sở dữ liệu
        categoryDB = new CategoryDB(this);

        // Hiển thị dữ liệu từ bảng categories
        displayCategories();

        // Gọi hàm setupTouchListener để ẩn bàn phím khi nhấn ra ngoài
        setupTouchListener(findViewById(R.id.rootCate)); // rootLayout là layout chính của Activity

        // Xử lý khi nhấn nút "Add Category"
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = etCategoryName.getText().toString().trim();
                String categoryIcon = etCategoryIcon.getText().toString().trim();

                if (categoryName.isEmpty() || categoryIcon.isEmpty()) {
                    Toast.makeText(CategoryActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    long result = categoryDB.addCategory(categoryName, categoryIcon);

                    if (result != -1) {
                        Toast.makeText(CategoryActivity.this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                        etCategoryName.setText(""); // Reset trường nhập
                        etCategoryIcon.setText("");
                        displayCategories(); // Cập nhật danh sách sau khi thêm
                    } else {
                        Toast.makeText(CategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Hàm hiển thị danh mục
    private void displayCategories() {
        Cursor cursor = categoryDB.getAllCategories();

        if (cursor != null && cursor.getCount() > 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{DatabaseHelper.CATEGORY_NAME_COL, DatabaseHelper.CATEGORY_ICON_COL}, // Sửa ở đây
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );


            lvCategories.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No categories available", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm ẩn bàn phím
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    // Thiết lập touch listener để gọi hideKeyboard khi nhấn ra ngoài
    private void setupTouchListener(View view) {
        // Kiểm tra nếu View là EditText
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(); // Ẩn bàn phím khi nhấn vào view không phải EditText
                return false;
            });
        }

        // Nếu View là ViewGroup (ví dụ: LinearLayout, ConstraintLayout), lặp qua các con của nó
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupTouchListener(innerView);
            }
        }
    }
}
