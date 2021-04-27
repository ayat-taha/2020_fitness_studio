package com.example.fitforlife.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserInfo extends CoachInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String groupId;
    private Double weightGoal;
    private String weeklyGoal;
    private long dateAdded;
    private List<Payment> userPayments;

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, String type, String studio) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.userPayments = new ArrayList<>();
    }

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, String type, String studio, String groupId) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.groupId = groupId;
        this.userPayments = new ArrayList<Payment>();
    }



    public UserInfo(String id, String fullName, String age, String phone, String email, String password, String type, String studio, String groupId, double parseDouble, String weeklyGoal) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.groupId = groupId;
        this.userPayments = new ArrayList<Payment>();
        this.weightGoal = parseDouble;
        this.weeklyGoal = weeklyGoal;
    }

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, String type, String studio, double parseDouble, String weeklyGoal) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.userPayments = new ArrayList<Payment>();
        this.weightGoal = parseDouble;
        this.weeklyGoal = weeklyGoal;
    }

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, long dateAdded, String type, String studio) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.dateAdded = dateAdded;
        this.userPayments = new ArrayList<>();
    }

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, long dateAdded, String type, String studio, String groupId) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.groupId = groupId;
        this.dateAdded = dateAdded;

        this.userPayments = new ArrayList<Payment>();
    }


    public UserInfo() {
        this.userPayments = new ArrayList<Payment>();
    }

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, long dateAdded, String type, String studio, String groupId, double parseDouble, String weeklyGoal) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.groupId = groupId;
        this.userPayments = new ArrayList<Payment>();
        this.weightGoal = parseDouble;
        this.weeklyGoal = weeklyGoal;
        this.dateAdded = dateAdded;

    }

    public UserInfo(String id, String fullName, String age, String phone, String email, String password, long dateAdded, String type, String studio, double parseDouble, String weeklyGoal) {
        super(id, fullName, age, phone, email, password, type, studio);
        this.userPayments = new ArrayList<Payment>();
        this.weightGoal = parseDouble;
        this.weeklyGoal = weeklyGoal;
        this.dateAdded = dateAdded;

    }

    /**
     * setters and getters
     */
    public String getGroupId() {
        return groupId;
    }

    public Double getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(Double weightGoal) {
        this.weightGoal = weightGoal;
    }

    public String getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(String weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<Payment> getUserPayments() {
        return userPayments;
    }

    public void setUserPayments(List<Payment> userPayments) {
        if (userPayments != null)
            this.userPayments = userPayments;
        else this.userPayments = new ArrayList<>();
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String toString() {
        return getFullName();
    }
}
