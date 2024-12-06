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
import com.example.expensemanagementstudent.model.TransactionOV;

import java.util.ArrayList;

public class TransactionAdapterOverview extends RecyclerView.Adapter<TransactionAdapterOverview.TransactionViewHolder> {
    private Context context;
    private ArrayList<TransactionOV> transactionOVS;

    public TransactionAdapterOverview(Context context, ArrayList<TransactionOV> transactionOVS) {
        this.context = context;
        this.transactionOVS = transactionOVS;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item_ov, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionOV transactionOV = transactionOVS.get(position);

        // Set transactionOV details
        holder.category.setText(transactionOV.getCategory());
        holder.date.setText(transactionOV.getDate());
        holder.amount.setText(transactionOV.getFormattedAmount());

        // Set the icon and text color based on the transactionOV type
        if (transactionOV.getType() == 0) { // Expense
            holder.icon.setImageResource(R.drawable.ic_minus); // Replace with your minus icon
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else { // Income
            holder.icon.setImageResource(R.drawable.ic_plus); // Replace with your plus icon
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
    }


    @Override
    public int getItemCount() {
        return transactionOVS.size();
    }

    /**
     * Updates the transactionOVS list and refreshes the RecyclerView.
     */
    public void updateTransactions(ArrayList<TransactionOV> newTransactionOVS) {
        this.transactionOVS = newTransactionOVS;
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
