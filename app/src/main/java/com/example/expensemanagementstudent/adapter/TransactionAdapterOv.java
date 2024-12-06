package com.example.expensemanagementstudent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.model.TransactionOverview;

import java.util.ArrayList;

public class TransactionAdapterOv extends RecyclerView.Adapter<TransactionAdapterOv.TransactionViewHolder> {
    private Context context;
    private ArrayList<TransactionOverview> transactionOverviews;

    public TransactionAdapterOv(Context context, ArrayList<TransactionOverview> transactionOverviews) {
        this.context = context;
        this.transactionOverviews = transactionOverviews;
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

        // Set transactionOverview details
        holder.category.setText(transactionOverview.getCategory());
        holder.date.setText(transactionOverview.getDate());
        holder.amount.setText(transactionOverview.getFormattedAmount());

        // Set the icon and text color based on the transactionOverview type
        if (transactionOverview.getType() == 0) { // Expense
            holder.icon.setImageResource(R.drawable.ic_minus); // Replace with your minus icon
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else { // Income
            holder.icon.setImageResource(R.drawable.ic_plus); // Replace with your plus icon
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
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
        TextView date, category, amount;
        ImageView icon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.transaction_date);
            category = itemView.findViewById(R.id.transaction_category);
            amount = itemView.findViewById(R.id.transaction_amount);
            icon = itemView.findViewById(R.id.transaction_icon); // Initialize the ImageView
        }
    }

}
