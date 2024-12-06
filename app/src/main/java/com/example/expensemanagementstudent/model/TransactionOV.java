package com.example.expensemanagementstudent.model;

public class TransactionOV {
    private String date;
    private String category;
    private String formattedAmount;
    private int type; // 1 for expense, 0 for income

    // Constructor
    public TransactionOV(String date, String category, String formattedAmount, int type) {
        this.date = date;
        this.category = category;
        this.formattedAmount = formattedAmount;
        this.type = type;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

    public int getType() {
        return type;
    }
}
