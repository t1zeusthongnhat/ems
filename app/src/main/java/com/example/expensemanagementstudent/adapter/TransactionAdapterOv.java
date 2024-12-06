package com.example.expensemanagementstudent.adapter;

import static com.example.expensemanagementstudent.TransactionHistoryActivity.EDIT_TRANSACTION_REQUEST_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagementstudent.EditTransactionActivity;
import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.db.ExpenseDB;
import com.example.expensemanagementstudent.model.TransactionOverview;

import java.util.ArrayList;

public class TransactionAdapterOv extends RecyclerView.Adapter<TransactionAdapterOv.TransactionViewHolder> {
    private Context context;
    private ArrayList<TransactionOverview> transactionOverviews;
    private ExpenseDB expenseDB; // Add this field

    public TransactionAdapterOv(Context context, ArrayList<TransactionOverview> transactionOverviews) {
        this.context = context;
        this.transactionOverviews = transactionOverviews;
        this.expenseDB = new ExpenseDB(context); // Initialize here

    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item_overview, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionOverview transactionOverview = transactionOverviews.get(position);

        holder.category.setText(transactionOverview.getCategory());
        holder.date.setText(transactionOverview.getDate());
        holder.amount.setText(transactionOverview.getFormattedAmount());
        holder.description.setText(transactionOverview.getDescription()); // Set description text

        if (transactionOverview.getType() == 0) { // Expense
            holder.icon.setImageResource(R.drawable.ic_minus);
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else { // Income
            holder.icon.setImageResource(R.drawable.ic_plus);
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }

        // Set the click listener for the Edit button
        holder.btnEditTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTransactionActivity.class);
            intent.putExtra("transactionId", transactionOverview.getId());
            intent.putExtra("transactionType", transactionOverview.getType());

            // Start activity for result
            ((Activity) context).startActivityForResult(intent, EDIT_TRANSACTION_REQUEST_CODE);
        });

        // Set the click listener for the Delete button
        holder.btnDeleteTransaction.setOnClickListener(v -> {
            // Confirm deletion with a dialog
            new AlertDialog.Builder(context)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean isDeleted = expenseDB.deleteTransaction(transactionOverview.getId());
                        if (isDeleted) {
                            transactionOverviews.remove(position); // Remove the item from the list
                            notifyItemRemoved(position); // Notify RecyclerView about the deletion
                            notifyItemRangeChanged(position, transactionOverviews.size()); // Update the range
                            Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return transactionOverviews.size();
    }

    /**
     * Updates the transactionOverviews list and refreshes the RecyclerView.
     */
    public void updateTransactions(ArrayList<TransactionOverview> newTransactionOverviews) {
        this.transactionOverviews = newTransactionOverviews;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView date, category, amount, description; // Add description
        ImageView icon;
        Button btnEditTransaction, btnDeleteTransaction; // Buttons for edit and delete

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.transaction_date);
            category = itemView.findViewById(R.id.transaction_category);
            amount = itemView.findViewById(R.id.transaction_amount);
            description = itemView.findViewById(R.id.transaction_description); // Initialize description
            icon = itemView.findViewById(R.id.transaction_icon);
            btnEditTransaction = itemView.findViewById(R.id.btn_edit_transaction);
            btnDeleteTransaction = itemView.findViewById(R.id.btn_delete_transaction);
        }
    }

}

