package com.example.fitforlife.Model;

public class Notification {
    private String userid;
    private String text;
    private String text2;
    private String postid;
    private boolean ispost;
    private boolean isEventRes;
    private boolean isEventCanc;
    private boolean isEventNotGoing;
    private boolean isEventGoing;
    private boolean isProgress;
    private boolean isPayment;


    public Notification(String userid, String text, String text2, String postid, boolean ispost, boolean isEvent, boolean isEventCanc, boolean isEventNotGoing, boolean isEventGoing, boolean isProgress, boolean isPayment) {
        this.userid = userid;
        this.text = text;
        this.text2 = text2;
        this.postid = postid;
        this.ispost = ispost;
        this.isEventRes = isEvent;
        this.isEventCanc = isEventCanc;
        this.isEventNotGoing = isEventNotGoing;
        this.isEventGoing = isEventGoing;
        this.isProgress = isProgress;
        this.isPayment = isPayment;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

    public boolean isIsProgress() {
        return isProgress;
    }

    public void setIsProgress(boolean isProgress) {
        this.isProgress = isProgress;
    }

    public boolean isIsPayment() {
        return isPayment;
    }

    public void setIsPayment(boolean isPayment) {
        this.isPayment = isPayment;
    }

    public boolean isIsEventRes() {
        return isEventRes;
    }

    public void setIsEventRes(boolean eventRes) {
        isEventRes = eventRes;
    }

    public boolean isIsEventCanc() {
        return isEventCanc;
    }

    public void setIsEventCanc(boolean eventCanc) {
        isEventCanc = eventCanc;
    }

    public boolean isIsEventNotGoing() {
        return isEventNotGoing;
    }

    public void setIsEventNotGoing(boolean isEventNotGoing) {
        this.isEventNotGoing = isEventNotGoing;
    }

    public boolean isIsEventGoing() {
        return isEventGoing;
    }

    public void setIsEventGoing(boolean isEventGoing) {
        this.isEventGoing = isEventGoing;
    }


    @Override
    public String toString() {
        return "Notification{" +
                "userid='" + userid + '\'' +
                ", text='" + text + '\'' +
                ", postid='" + postid + '\'' +
                ", ispost=" + ispost +
                ", isEventRes=" + isEventRes +
                ", isEventCanc=" + isEventCanc +
                '}';
    }
}
