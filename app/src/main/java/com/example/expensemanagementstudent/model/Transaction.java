package com.example.expensemanagementstudent.model;


public class Transaction {
    private String title;
    private String subTitle;
    private int amount;

    public Transaction(String title, String subTitle, int amount) {
        this.title = title;
        this.subTitle = subTitle;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getAmount() {
        return amount;
    }
}


