package com.example.fitforlife.Model;

public class User {

    private String id;
    private String fullName;
    private String imageUrl;
    private String type;
    private String status;
    private String search;

    public User(String id, String fullName, String imageUrl, String type) {
        this.id = id;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String userType) {
        this.type = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
