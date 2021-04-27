package com.example.fitforlife.Model;

import java.util.Date;

public class Measurement {

    private String id;
    private String traineeId;
    private String dateString;
    private long date;
    private Double weight;
    private Double waist;
    private Double chest;
    private Double buttocks;
    private Double rightArm;
    private Double leftArm;
    private Double rightThigh;
    private Double leftThigh;

    public Measurement(String id, String traineeId, long date, Double weight, Double waist, Double chest, Double buttocks, Double rightArm, Double leftArm, Double rightThigh, Double leftThigh) {
        this.id = id;
        this.traineeId = traineeId;
        this.date = date;
        this.weight = weight;
        this.waist = waist;
        this.chest = chest;
        this.buttocks = buttocks;
        this.rightArm = rightArm;
        this.leftArm = leftArm;
        this.rightThigh = rightThigh;
        this.leftThigh = leftThigh;
    }

    public Measurement(String date, Double weight, Double waist, Double buttocks, Double chest, Double leftArm, Double rightArm, Double leftThigh, Double rightThigh) {
        this.dateString = date;
        this.weight = weight;
        this.waist = waist;
        this.buttocks = buttocks;
        this.chest = chest;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftThigh = leftThigh;
        this.rightThigh = rightThigh;
    }

    public Measurement() {

    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Measurement(String uniqueId, String traineeId, String date, Double weight, Double waist, Double buttocks, Double chest, Double leftArm, Double rightArm, Double leftThigh, Double rightThigh) {
        this.id = uniqueId;
        this.traineeId = traineeId;
        this.dateString = date;
        this.weight = weight;
        this.waist = waist;
        this.buttocks = buttocks;
        this.chest = chest;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftThigh = leftThigh;
        this.rightThigh = rightThigh;
    }


    public String getId() {
        return id;
    }

    public String getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(String traineeId) {
        this.traineeId = traineeId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    public Double getButtocks() {
        return buttocks;
    }

    public void setButtocks(Double buttocks) {
        this.buttocks = buttocks;
    }

    public Double getChest() {
        return chest;
    }

    public void setChest(Double chest) {
        this.chest = chest;
    }

    public Double getLeftArm() {
        return leftArm;
    }

    public void setLeftArm(Double leftArm) {
        this.leftArm = leftArm;
    }

    public Double getRightArm() {
        return rightArm;
    }

    public void setRightArm(Double rightArm) {
        this.rightArm = rightArm;
    }

    public Double getLeftThigh() {
        return leftThigh;
    }

    public void setLeftThigh(Double leftThigh) {
        this.leftThigh = leftThigh;
    }

    public Double getRightThigh() {
        return rightThigh;
    }

    public void setRightThigh(Double rightThigh) {
        this.rightThigh = rightThigh;
    }


    @Override
    public String toString() {
        return "Measurement{" +
                "id='" + id + '\'' +
                ", traineeId='" + traineeId + '\'' +
                ", date=" + date +
                ", weight=" + weight +
                ", waist=" + waist +
                ", chest=" + chest +
                ", buttocks=" + buttocks +
                ", rightArm=" + rightArm +
                ", leftArm=" + leftArm +
                ", rightThigh=" + rightThigh +
                ", leftThigh=" + leftThigh +
                '}';
    }
}
