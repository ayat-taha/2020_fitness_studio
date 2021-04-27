package com.example.fitforlife.Model;

import java.io.Serializable;
import java.util.Objects;

public class CoachInfo implements Serializable {
    private String id;
    private String fullName;
    private String age;
    private String phone;
    private String email;
    private String password;
    private String type;
    private String studio;

    public CoachInfo() {
    }

    public CoachInfo(String id, String fullName, String age, String phone, String email, String password, String type, String studio) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.type = type;
        this.studio = studio;
    }

    public CoachInfo(String all_groups) {
        this.fullName = all_groups;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachInfo coachInfo = (CoachInfo) o;
        return Objects.equals(id, coachInfo.id) &&
                Objects.equals(fullName, coachInfo.fullName) &&
                Objects.equals(age, coachInfo.age) &&
                Objects.equals(phone, coachInfo.phone) &&
                Objects.equals(email, coachInfo.email) &&
                Objects.equals(password, coachInfo.password) &&
                Objects.equals(type, coachInfo.type) &&
                Objects.equals(studio, coachInfo.studio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, age, phone, email, password, type, studio);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}

