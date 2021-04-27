package com.example.fitforlife.SQLite;

import android.content.Context;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.Payment;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.Model.UserInfo;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FitForLifeDataManager {
    private static FitForLifeDataManager instance = null;
    private Context context = null;
    private Database db = null;
    private CoachInfo currentCoach = null;
    private UserInfo currentUser = null;
    private CoachInfo userCoach = null;
    private CoachInfo userManager = null;
    private UserInfo currentUserProgress = null;
    // private Comment selectedComment = null;
    private Event currentCanceledEvent = null;
    private CoachInfo currentManager = null;

    public static FitForLifeDataManager getInstance() {
        if (instance == null) {
            instance = new FitForLifeDataManager();
        }
        return instance;
    }


    public static void releaseInstance() {
        if (instance != null) {
            instance.clean();
            instance = null;
        }
    }

    private void clean() {
    }


    public Context getContext() {
        return context;
    }


    public void openDataBase(Context context) {
        this.context = context;

        if (context != null) {
            db = new Database(context);
            db.open();
        }
    }

    public void closeDataBase() {
        if (db != null) {
            db.close();
        }
    }


    /**
     * METHOD TO ADD NEW COACH TO DATABASE
     *
     * @param newCoach
     */

    public void createCoach(CoachInfo newCoach) {
        if (db != null) {
            db.createCoach(newCoach);
        }
    }

    /**
     * CReate New USER
     *
     * @param newUser
     */
    public void createUser(UserInfo newUser) {
        if (db != null) {
            db.createUser(newUser);
        }
    }

    /**
     * create new group to add t osql
     *
     * @param newGroup
     */
    public void createGroup(Group newGroup) {
        if (db != null) {
            db.createGroup(newGroup);
        }

    }

    /**
     * create new session
     *
     * @param newSession
     */
    public void createSession(Session newSession) {
        if (db != null) {
            db.createSession(newSession);
        }
    }

    /**
     * create new payment
     *
     * @param newPayment
     */
    public void createPayment(Payment newPayment) {
        if (db != null) {
            db.createPayment(newPayment);
        }
    }

    /**
     * Create new meas
     *
     * @param newMeasurement -for user
     */
    public void createMeasurement(Measurement newMeasurement) {
        if (db != null) {
            db.createMeasurement(newMeasurement);
        }
    }

    /**
     * Update payment
     *
     * @params payment
     */

    public void updatePayment(Payment payment) {
        if (db != null && payment != null) {
            db.updatePayment(payment);
        }
    }

    /**
     * delete payment
     *
     * @params payment
     */

    public void deletePayment(Payment payment) {
        if (db != null && payment != null) {
            db.deletePayment(payment);
        }
    }


    /**
     * @param canceledEvent
     * @param fStoreId      id generated from firestore
     */
    public void createCanceledSession(Event canceledEvent, String fStoreId) {

        if (db != null) {
            db.createCanceledSession(canceledEvent, fStoreId);
        }
    }


    public void createRescheduledSession(Event canceledEvent, String fStoreId) {

        if (db != null) {
            db.createRescheduledSession(canceledEvent, fStoreId);
        }
    }

    public void createNotGoingSession(Event notGoingEvent, String fStoreId, String user1) {

        if (db != null) {
            db.createNotGoingSession(notGoingEvent, fStoreId, user1);
        }
    }

    public void createNotGoingSession(Event notGoingEvent, String fStoreId, String user1, String user2) {

        if (db != null) {
            db.createNotGoingSession(notGoingEvent, fStoreId, user1, user2);
        }
    }


    /**
     * Update user
     *
     * @param user
     */

    public void updateUser(UserInfo user) {
        if (db != null && user != null) {
            db.updateUser(user);
        }
    }

    public void deleteUser(UserInfo user) {
        if (db != null && user != null) {
            db.deleteUser(user);
        }
    }

    /**
     * UPDATE COACH
     *
     * @param coach - BEFORE UPDATE
     */
    public void updateCoach(CoachInfo coach) {
        if (db != null && coach != null) {
            db.updateCoach(coach);
        }
    }

    public void deleteCoach(CoachInfo coach) {
        if (db != null && coach != null) {
            db.deleteCoach(coach);
        }
    }

    /**
     * Update Group
     *
     * @param group
     */

    public void updateGroup(Group group) {
        if (db != null && group != null) {
            db.updateGroup(group);
        }
    }

    public void updateUserGoal(UserInfo userInfo) {
        if (db != null && userInfo != null) {
            db.updateUserGoal(userInfo);
        }
    }

    public void updateNotGoingUser2(String user2Id, String eventId) {
        if (db != null && user2Id != null && eventId != null) {
            db.updateNotGoingUser2(user2Id, eventId);
        }
    }

    /**
     * delete group and its session
     *
     * @param group
     */
    public void deleteGroup(Group group) {
        if (db != null) {
            db.deleteGroup(group);
        }
    }

    /**
     * delete group and its session
     *
     * @param group
     */
    public void deleteGroupSessions(Group group) {
        if (db != null) {
            db.deleteGroupSessions(group);
        }
    }

    public void deleteCanceled(String canceledId) {
        if (db != null) {
            db.deleteCanceled(canceledId);
        }
    }

    public Group getGroup(String id) {
        Group result = null;
        if (db != null) {
            result = db.getGroup(id);
        }
        return result;
    }

    public CoachInfo getCoach(String id) {
        CoachInfo result = null;
        if (db != null) {
            result = db.getCoach(id);
        }
        return result;
    }

    public Group getGroupByName(String groupName) {
        Group result = null;
        if (db != null) {
            result = db.getGroupByName(groupName);
        }
        return result;
    }

    public String getCanceledId(Event event) {
        String result = null;
        if (db != null) {
            result = db.getCanceledId(event);
        }
        return result;
    }

    public String getNotGoingId(Event event) {
        String result = null;
        if (db != null) {
            result = db.getNotGoingId(event);
        }
        return result;
    }

    public List<Group> getAllCoachGroup(String id) {
        List<Group> result = null;
        if (db != null) {
            result = db.getAllCoachGroup(id);
        }
        return result;
    }

    /**
     * get all Groups inSQl
     *
     * @return
     */
    public List<Group> getAllGroups() {
        List<Group> result = new ArrayList<Group>();
        if (db != null) {
            result = db.getAllGroups();
        }
        return result;
    }


    /**
     * Get all Canceled sessions
     *
     * @return
     */
    public List<Event> getAllCanceledSessions() {
        List<Event> result = new ArrayList<Event>();
        if (db != null) {
            result = db.getAllCanceledSessions();
        }
        return result;
    }

    /**
     * Get all Canceled sessions
     *
     * @return
     */
    public List<Event> getAllGroupCanceledSessions(Group group) {
        List<Event> result = new ArrayList<Event>();
        if (db != null && group != null) {
            result = db.getAllGroupCanceledSessions(group);
        }
        return result;
    }

    /**
     * Get all Canceled sessions
     *
     * @return
     */
    public List<Event> getAllGroupRescheduleSessions(Group group) {
        List<Event> result = new ArrayList<Event>();
        if (db != null && group != null) {
            result = db.getAllGroupRescheduleSessions(group);
        }
        return result;
    }

    public List<Event> getAllRescheduledSessions() {
        List<Event> result = new ArrayList<Event>();
        if (db != null) {
            result = db.getAllRescheduledSessions();
        }
        return result;
    }


    /**
     * Get all Canceled sessions
     *
     * @return
     */
    public List<Event> getAllCanceledByGroupId(String groupId) {
        List<Event> result = new ArrayList<Event>();
        if (db != null) {
            result = db.getAllCanceledByGroupId(groupId);
        }
        return result;
    }

    public List<Event> getAllNotGoingForUser(String userId) {
        List<Event> result = new ArrayList<Event>();
        if (db != null) {
            result = db.getAllNotGoingForUser(userId);
        }
        return result;
    }

    /**
     * for spinner ( filter in trainee framgent ) - used when current logged in is ***coach*****
     *
     * @param currentCoach - current coach
     * @param groupName    - group name to get its trainee
     * @return - list of trainee for this grop
     */
    public List<UserInfo> getAllGroupTrainee(CoachInfo currentCoach, String groupName) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && currentCoach != null && groupName != null) {
            result = db.getAllGroupTrainee(currentCoach, groupName);
        }
        return result;
    }

    /**
     * for spinner ( filter in trainee framgent ) - used when current logged in is ***coach*****
     *
     * @param group group  to get its trainee
     * @return - list of trainee for this grop
     */
    public List<UserInfo> getAllGroupTrainee(Group group) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && group != null) {
            result = db.getAllGroupTrainee(group);
        }
        return result;
    }

    public List<UserInfo> getAllAgeGroupTrainee(Group group, String age) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && group != null) {
            result = db.getAllAgeGroupTrainee(group, age);
        }
        return result;
    }

    public List<UserInfo> getAllAgeCoachTrainee(Group group, String age, CoachInfo coach) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && group != null) {
            result = db.getAllAgeCoachTrainee(group, age, coach);
        }
        return result;
    }


    /**
     * for spinner ( filter in trainee framgent ) - used when current logged in is **MANAGER**
     *
     * @param groupName - group name to get its trainee
     * @return - list of trainee for this groOUP
     */
    public List<UserInfo> getAllGroupTraineeByGroupName(String groupName) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && groupName != null) {
            result = db.getAllGroupTraineeByGroupName(groupName);
        }
        return result;
    }

    public List<UserInfo> getAllTrainee(String groupName) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && groupName != null) {
            result = db.getAllTrainee(groupName);
        }
        return result;
    }

    public List<UserInfo> getAllCoachTrainee(CoachInfo currentCoach) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null) {
            result = db.getAllCoachTrainee(currentCoach);
        }
        return result;
    }

    public List<UserInfo> getAllTraineeByAgeAndCoach(CoachInfo currentCoach, String ageRange) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null) {
            result = db.getAllTraineeByAgeAndCoach(currentCoach, ageRange);
        }
        return result;
    }

    public List<UserInfo> getAllManagerTrainee(CoachInfo currentManager) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerTrainee(currentManager);
        }
        return result;
    }

    public List<UserInfo> getAllTraineeByAgeAndManager(CoachInfo currentManager, String ageRange) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && currentManager != null) {
            result = db.getAllTraineeByAgeAndManager(currentManager, ageRange);
        }
        return result;
    }

    public List<CoachInfo> getAllManagerCoaches(CoachInfo currentManager) {
        List<CoachInfo> result = new ArrayList<CoachInfo>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerCoaches(currentManager);
        }
        return result;
    }

    public List<Group> getAllManagerGroups(CoachInfo currentManager) {
        List<Group> result = new ArrayList<Group>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerGroups(currentManager);
        }
        return result;
    }

    public List<Session> getAllManagerSessions(CoachInfo currentManager) {
        List<Session> result = new ArrayList<Session>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerSessions(currentManager);
        }
        return result;
    }

    public List<Event> getAllManagerReschedule(CoachInfo currentManager) {
        List<Event> result = new ArrayList<>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerReschedule(currentManager);
        }
        return result;
    }

    public List<Event> getAllManagerCanceled(CoachInfo currentManager) {
        List<Event> result = new ArrayList<>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerCanceled(currentManager);
        }
        return result;
    }

    public List<Event> getAllManagerCanceledToReschedule(CoachInfo currentManager) {
        List<Event> result = new ArrayList<>();
        if (db != null && currentManager != null) {
            result = db.getAllManagerCanceledToReschedule(currentManager);
        }
        return result;
    }

    /**
     * get all session for group
     *
     * @param group
     * @return
     */
    public List<Session> getAllGroupSessions(Group group) {
        List<Session> result = new ArrayList<Session>();
        if (db != null && group != null) {
            result = db.getAllGroupSessions(group);
        }
        return result;
    }

    public List<Measurement> getTraineeMeasurement(String traineeId) {
        List<Measurement> result = new ArrayList<Measurement>();
        if (db != null && traineeId != null) {
            result = db.getTraineeMeasurement(traineeId);
        }
        return result;
    }

    public Double getLatestWeightPrgress(UserInfo currentItem) {
        Double result = null;
        if (db != null && currentItem != null) {
            result = db.getLatestWeightPrgress(currentItem);
        }
        return result;

    }

    public List<Payment> getAllUserPayments(UserInfo user) {
        List<Payment> result = new ArrayList<Payment>();
        if (db != null && user != null) {
            result = db.getAllUserPayments(user);
        }
        return result;
    }

    public List<Integer> getAllPaidPayments(UserInfo user, int year) {
        List<Integer> result = new ArrayList<Integer>();
        if (db != null && user != null) {
            result = db.getAllPaidPayments(user, year);
        }
        return result;
    }

    public List<Integer> getAllPaymentsYears(UserInfo user) {
        List<Integer> result = new ArrayList<Integer>();
        if (db != null && user != null) {
            result = db.getAllPaymentsYears(user);
        }
        return result;
    }

    public List<Integer> getAllJoinedYears() {
        List<Integer> result = new ArrayList<Integer>();
        if (db != null) {
            result = db.getAllJoinedYears();
        }
        return result;
    }

    public List<Integer> getAllPaymentsYears() {
        List<Integer> result = new ArrayList<Integer>();
        if (db != null) {
            result = db.getAllPaymentsYears();
        }
        return result;
    }


    public Payment getPayment(UserInfo user, int month, int year) {
        Payment result = new Payment();
        if (db != null && user != null) {
            result = db.getPayment(user, month, year);
        }
        return result;
    }

    public List<UserInfo> getAllNotPaidReport(int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null) {
            result = db.getAllNotPaidReport(month, year);
        }
        return result;
    }

    public List<UserInfo> getGroupPaidReport(Group group, int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && group != null) {
            result = db.getGroupPaidReport(group, month, year);
        }
        return result;
    }

    public List<UserInfo> getAllJoinedManager(CoachInfo manager, int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && manager != null) {
            result = db.getAllJoinedManager(manager, month, year);
        }
        return result;
    }

    public List<UserInfo> getAllJoinedThisYearManager(CoachInfo manager, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && manager != null) {
            result = db.getAllJoinedThisYearManager(manager, year);
        }
        return result;
    }
    /**
     * get all trainee of studio that join in specfiv month
     *
     * @param coach - current coach
     * @param month - choosen month
     * @param year  - choose year
     * @return - list of trainee
     */
    public List<UserInfo> getAllJoinedCoach(CoachInfo coach, int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && coach != null) {
            result = db.getAllJoinedCoach(coach, month, year);
        }
        return result;
    }

    public List<UserInfo> getAllJoinedThisYearCoach(CoachInfo coach, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && coach != null) {
            result = db.getAllJoinedThisYearCoach(coach, year);
        }
        return result;
    }

    /**
     * get all trainee who joined a specif group in specif date
     *
     * @param group - specific group coach
     * @param month - choosen month
     * @param year  - choose year
     * @return - list of trainee
     */
    public List<UserInfo> getAllJoinedByGroup(Group group, int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && group != null) {
            result = db.getAllJoinedByGroup(group, month, year);
        }
        return result;
    }

    public List<UserInfo> getAllJoinedByGroupAllYear(Group group, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        if (db != null && group != null) {
            result = db.getAllJoinedByGroupAllYear(group, year);
        }
        return result;
    }

    /**
     * TEST
     *
     * @param firstDay
     * @param lastday
     * @param userId
     * @return
     */
    public List<Event> getNotGoingCountThisMonthList(long firstDay, long lastday, String userId) {
        List<Event> result = new ArrayList<Event>();
        if (db != null && userId != null) {
            result = db.getNotGoingCountThisMonthList(firstDay, lastday, userId);
        }
        return result;
    }

    public List<Event> getAvailableThisMonthList(long firstDay, long lastDay, String userId) {
        List<Event> result = new ArrayList<Event>();
        if (db != null && userId != null) {
            result = db.getAvailableThisMonthList(firstDay, lastDay, userId);
        }
        return result;
    }

    public List<Event> User2isCurrentUser(long firstDay, long lastDay, String userId) {
        List<Event> result = new ArrayList<Event>();
        if (db != null && userId != null) {
            result = db.User2isCurrentUser(firstDay, lastDay, userId);
        }
        return result;
    }

    public int getNotGoingCountThisMonth(long firstDay, long lastday, String userId) {
        int count = 0;
        if (db != null && userId != null) {
            count = db.getNotGoingCountThisMonth(firstDay, lastday, userId);
        }
        return count;
    }

    public CoachInfo getGroupCoach(Group group) {
        CoachInfo result = new CoachInfo();
        if (db != null && group != null) {
            result = db.getGroupCoach(group);
        }
        return result;
    }

    public Map<UserInfo, Integer> getMonthlyUserReport(int month, int year, String groupName, String method, String CoachId) {
        Map<UserInfo, Integer> result = new LinkedHashMap<>();
        if (db != null && groupName != null) {
            result = db.getMonthlyUserReport(month, year, groupName, method, CoachId);
        }
        return result;
    }

    public Map<UserInfo, Integer> getManagerMonthlyUserReport(int month, int year, String groupName, String method, String CoachId) {
        Map<UserInfo, Integer> result = new LinkedHashMap<>();
        if (db != null && groupName != null) {
            result = db.getManagerMonthlyUserReport(month, year, groupName, method, CoachId);
        }
        return result;
    }

    public int getMonthlySumReport(int month, int year, String groupName, String method, String CoachId) {
        int result = 0;
        if (db != null && groupName != null) {
            result = db.getMonthlySumReport(month, year, groupName, method, CoachId);
        }
        return result;
    }

    public int getManagerMonthlySumReport(int month, int year, String groupName, String method, String CoachId) {
        int result = 0;
        if (db != null && groupName != null) {
            result = db.getManagerMonthlySumReport(month, year, groupName, method, CoachId);
        }
        return result;
    }

    public List<Session> sessionsOverlap(Session newSession) {
        List<Session> result = new ArrayList<Session>();
        if (db != null && newSession != null) {
            result = db.sessionsOverlap(newSession);
        }
        return result;
    }

    public CoachInfo getCurrentManager() {
        return currentManager;
    }

    public CoachInfo getCurrentCoach() {
        return currentCoach;
    }

    public CoachInfo getUserCoach() {
        return userCoach;
    }

    public CoachInfo getUserManager() {
        return userManager;
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public UserInfo getCurrentUserProgress() {
        return currentUserProgress;
    }

    public Event getCurrentCanceledEvent() {
        return currentCanceledEvent;
    }

    public void setCurrentManager(CoachInfo selected) {
        this.currentManager = selected;
    }

    public void setUserManager(CoachInfo selected) {
        this.userManager = selected;
    }

    public void setUserCoach(CoachInfo selected) {
        this.userCoach = selected;
    }

    public void setCurrentCoach(CoachInfo selected) {
        this.currentCoach = selected;
    }

    public void setCurrentUser(UserInfo selected) {
        this.currentUser = selected;
    }

    public void setCurrentUserProgress(UserInfo selected) {
        this.currentUserProgress = selected;
    }

    public void setCurrentCanceledEvent(Event currentCanceledEvent) {
        this.currentCanceledEvent = currentCanceledEvent;
    }
}