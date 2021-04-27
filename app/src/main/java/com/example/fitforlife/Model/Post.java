package com.example.fitforlife.Model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private String studio;
    private String date;



    public Post(String postid, String postimage, String description, String publisher, String studio, String date) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        this.studio = studio;
        this.date = date;
    }

    public Post() {
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
