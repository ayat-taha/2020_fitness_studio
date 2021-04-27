package com.example.fitforlife.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group implements Serializable {
    private String groupId;
    private String groupName;
    private String NumberOfTrainee;
    private String coachId;
    private List<Session> groupSessions;

    /**
     * full constructor
     * @param groupId
     * @param groupName
     * @param numberOfTrainee
     * @param coachId
     * @param groupSessions
     */
    public Group(String groupId, String groupName, String numberOfTrainee, String coachId, List<Session> groupSessions) {
        this.groupId = groupId;
        this.groupName = groupName;
        NumberOfTrainee = numberOfTrainee;
        this.coachId = coachId;
        this.groupSessions = groupSessions;
    }

    /**
     * without list sessions
     * @param groupId
     * @param groupName
     * @param numberOfTrainee
     * @param coachId
     */
    public Group(String groupId, String groupName, String numberOfTrainee, String coachId) {
        this.groupId = groupId;
        this.groupName = groupName;
        NumberOfTrainee = numberOfTrainee;
        this.coachId = coachId;
    }

    /**
     * Constructor without Group ID
     * @param groupName
     * @param numberOfTrainee
     * @param coachId
     * @param groupSessions
     */
    public Group(String groupName, String numberOfTrainee, String coachId, List<Session> groupSessions) {
        this.groupName = groupName;
        NumberOfTrainee = numberOfTrainee;
        this.coachId = coachId;
        this.groupSessions = groupSessions;
    }

    public Group() {

    }

    public Group(String groupId) {
        this.groupId=groupId;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNumberOfTrainee() {
        return NumberOfTrainee;
    }

    public void setNumberOfTrainee(String numberOfTrainee) {
        NumberOfTrainee = numberOfTrainee;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public List<Session> getGroupSessions() {
        return groupSessions;
    }

    public void setGroupSessions(ArrayList<Session> groupSessions) {
        this.groupSessions = groupSessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupId, group.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }

    /**
     *  DO NOT CHANGE THIS METHOD!!
     * @return
     */
    @Override
    public String toString() {
        return  groupName ;
    }
}
