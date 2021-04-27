package com.example.fitforlife.Model;

import java.util.Objects;

public class Session {

    private String groupId;
    private String sessionNumber;
    private String day;
    private int hour;
    private int minute;
    private int duration;



    public Session(String groupId, String sessionNumber) {
        this.groupId = groupId;
        this.sessionNumber = sessionNumber;
    }

    public Session(String groupId, String sessionNumber, String day, int hour, int minute, int duration) {
        this.groupId = groupId;
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
    }

    public Session(String groupId, String sessionNumber, String day, int hour, int minute) {
        this.groupId=groupId;
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public Session(String sessionNumber, String day, int hour, int minute) {
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public Session(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public Session(String sessionNumber, String day, int hour, int minute, int duration) {
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
    }

    public Session() {

    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return hour == session.hour &&
                minute == session.minute &&
                duration == session.duration &&
                groupId.equals(session.groupId) &&
                sessionNumber.equals(session.sessionNumber) &&
                day.equals(session.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, sessionNumber, day, hour, minute, duration);
    }

    @Override
    public String toString() {
        return "Session{" +
                "groupId='" + groupId + '\'' +
                ", sessionNumber='" + sessionNumber + '\'' +
                ", day='" + day + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                ", duration=" + duration +
                '}';
    }
}