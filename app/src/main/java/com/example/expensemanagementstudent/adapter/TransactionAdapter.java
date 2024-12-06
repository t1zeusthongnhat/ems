package com.example.expensemanagementstudent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
//sử dụng một TransactionAdapter để hiển thị dữ liệu giao dịch trong RecyclerView.
// TransactionAdapter sẽ nhận một danh sách các đối tượng Transaction và hiển thị thông tin của mỗi giao dịch.
    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        // Set dữ liệu vào các View
        holder.amountTextView.setText(transaction.getAmount());
        holder.amountTextView.setTextColor(transaction.getAmountColor());
        holder.dateTextView.setText(transaction.getDate());
        holder.descriptionTextView.setText(transaction.getDescription());
        holder.categoryTextView.setText(transaction.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView amountTextView, dateTextView, descriptionTextView, categoryTextView;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
        }
    }


}

