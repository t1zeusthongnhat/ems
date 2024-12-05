package com.example.expensemanagementstudent.model;

public class Transaction {
    //Transaction để chứa các thông tin về giao dịch, ví dụ như id, amount, description, date, categoryId, type, v.v.
    private int id;
    private String amount;  // Định dạng tiền tệ với dấu + hoặc -
    private String description;
    private String date;
    private String categoryName;
    private int amountColor;  // Màu sắc của số tiền (green hoặc red)
    private int type;  // Loại giao dịch (0: income, 1: expense)

    // Constructor
    public Transaction(int id, String amount, String description, String date, String categoryName, int amountColor, int type) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryName = categoryName;
        this.amountColor = amountColor;
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }
    public String getFormattedAmount() {
        return amount; // Giá trị 'amount' đã được định dạng khi khởi tạo Transaction
    }
    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getAmountColor() {
        return amountColor;
    }

    public void setAmountColor(int amountColor) {
        this.amountColor = amountColor;
    }
// Getter and Setter methods...
}
