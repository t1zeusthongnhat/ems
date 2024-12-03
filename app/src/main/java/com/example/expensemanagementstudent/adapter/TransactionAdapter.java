package com.example.expensemanagementstudent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.model.Transaction;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    public TransactionAdapter(@NonNull Context context, @NonNull List<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_transaction, parent, false);
        }

        Transaction transaction = getItem(position);

        // Gán dữ liệu vào View
        ImageView icon = convertView.findViewById(R.id.imageViewIcon);
        TextView title = convertView.findViewById(R.id.textViewTitle);
        TextView subTitle = convertView.findViewById(R.id.textViewSubTitle);
        TextView amount = convertView.findViewById(R.id.textViewAmount);

        // Set giá trị
        title.setText(transaction.getTitle());
        subTitle.setText(transaction.getSubTitle());
        amount.setText(String.format("₹%s", transaction.getAmount()));

        // Thay đổi màu sắc dựa trên giá trị
        int color = transaction.getAmount() < 0 ? 0xFFFF0000 : 0xFF00FF00; // Đỏ nếu âm, xanh nếu dương
        amount.setTextColor(color);

        return convertView;
    }
}
