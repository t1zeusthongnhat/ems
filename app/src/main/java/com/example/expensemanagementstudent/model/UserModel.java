package com.example.expensemanagementstudent.model;

public class UserModel {
    public int id;
    public String username;
    public String password;
    public String email;
    public String gender;
    public String address;
    public String created_at;
    public String updated_at;
    // Constructor, getters, setters, và các phương thức khác

    public UserModel(String address, String created_at, String email, int id, String password, String gender, String username, String updated_at) {
        this.address = address;
        this.created_at = created_at;
        this.email = email;
        this.id = id;
        this.password = password;
        this.gender = gender;
        this.username = username;
        this.updated_at = updated_at;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
