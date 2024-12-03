package com.example.expensemanagementstudent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanagementstudent.adapter.TransactionAdapter;
import com.example.expensemanagementstudent.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> {
            // Quay về màn hình Home
            Intent intent = new Intent(TransactionActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc màn hình hiện tại
        });
        // Dữ liệu giả lập
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("PJ Mobile Store", "Credit Card", -250));
        transactions.add(new Transaction("Apollo Pharmacy", "UPI", -350));

        // Áp dụng Adapter
        ListView listView = findViewById(R.id.lv_transactions);
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);
        listView.setAdapter(adapter);
    }
}
