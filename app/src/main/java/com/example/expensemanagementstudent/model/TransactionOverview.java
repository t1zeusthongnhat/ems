package com.example.expensemanagementstudent.model;

public class TransactionOverview {
    private int id;
    private String date;
    private String category;
    private String formattedAmount;
    private String description; // Add description field
    private int type;

    public TransactionOverview(int id, String date, String category, String formattedAmount, String description, int type) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.formattedAmount = formattedAmount;
        this.description = description;
        this.type = type;
    }

    // Getter methods
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getFormattedAmount() { return formattedAmount; }
    public String getDescription() { return description; } // Getter for description
    public int getType() { return type; }
}
