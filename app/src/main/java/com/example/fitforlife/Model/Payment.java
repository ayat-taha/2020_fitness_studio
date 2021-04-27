package com.example.fitforlife.Model;

import java.util.Objects;

public class Payment {

    private String userId;
    private int number;
    private int month;
    private int year;
    private int amount;
    private String method;


    public Payment(Payment payment) {
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.method = method;
    }


    public Payment(String userId,int number, int month, int year, int amount, String method) {
        this.userId = userId;
        this.number = number;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.method = method;
    }

    //constructor without number
    public Payment(String userId, int month, int year, int amount, String method) {
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.method = method;
    }
    public Payment() {
    }

    //        setters & getters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMethod() { return method; }

    public void setMethod(String method) { this.method = method; }

    public int getNumber() { return number; }

    public void setNumber(int number) { this.number = number; }

    @Override
    public String toString() {
        return "Payment{" +
                "userId='" + userId + '\'' +
                ", number=" + number +
                ", month=" + month +
                ", year=" + year +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return month == payment.month &&
                year == payment.year &&
                userId.equals(payment.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, month, year);
    }
}
