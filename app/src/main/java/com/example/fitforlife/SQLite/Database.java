package com.example.fitforlife.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.Payment;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.firestore.v1.StructuredQuery.CompositeFilter.Operator.AND;


public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FitForLife_Database";
    Context DBcontext;

    private SQLiteDatabase db = null;

    // Coaches Table
    private static final String TABLE_COACH_NAME = "coaches";  //table name
    private static final String COACH_COLUMN_ID = "id";
    private static final String COACH_COLUMN_EMAIL = "email";
    private static final String COACH_COLUMN_FULLNAME = "fullName";
    private static final String COACH_COLUMN_AGE = "age";
    private static final String COACH_COLUMN_PHONE = "phone";
    private static final String COACH_COLUMN_PASSWORD = "password";
    private static final String COACH_COLUMN_TYPE = "type";
    private static final String COACH_COLUMN_STUDIO = "studio";

    private static final String[] TABLE_COACH_COLUMNS = {COACH_COLUMN_ID, COACH_COLUMN_EMAIL, COACH_COLUMN_FULLNAME, COACH_COLUMN_AGE, COACH_COLUMN_PHONE, COACH_COLUMN_PASSWORD, COACH_COLUMN_TYPE, COACH_COLUMN_STUDIO};  //order of fields in user table

    // USER table
    private static final String TABLE_USER_NAME = "users";  //table name
    private static final String USER_COLUMN_ID = "id";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_FULLNAME = "fullName";
    private static final String USER_COLUMN_AGE = "age";
    private static final String USER_COLUMN_PHONE = "phone";
    private static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_COLUMN_DATEADDED = "dateAdded";
    private static final String USER_COLUMN_TYPE = "type";
    private static final String USER_COLUMN_STUDIO = "studio";
    private static final String USER_COLUMN_GROUPID = "groupId";
    private static final String USER_COLUMN_WEIGHTGOAL = "weightGoal";
    private static final String USER_COLUMN_WEEKLYGOAL = "weeklyGoal";

    private static final String[] TABLE_USER_COLUMNS = {USER_COLUMN_ID, USER_COLUMN_EMAIL,
            USER_COLUMN_FULLNAME, USER_COLUMN_AGE, USER_COLUMN_PHONE, USER_COLUMN_PASSWORD, USER_COLUMN_DATEADDED, USER_COLUMN_TYPE, USER_COLUMN_STUDIO,
            USER_COLUMN_GROUPID, USER_COLUMN_WEIGHTGOAL, USER_COLUMN_WEEKLYGOAL};  //order of fields in user table

    // GROUP table
    private static final String TABLE_GROUP_NAME = "groups";  //table name
    private static final String GROUP_COLUMN_ID = "groupId";
    private static final String GROUP_COLUMN_NAME = "groupName";
    private static final String GROUP_COLUMN_NUMBEROFTRAINEE = "numberOfTrainee";
    private static final String GROUP_COLUMN_COACHID = "coachId";
    private static final String[] TABLE_GROUP_COLUMNS = {GROUP_COLUMN_ID, GROUP_COLUMN_NAME, GROUP_COLUMN_NUMBEROFTRAINEE, GROUP_COLUMN_COACHID};

    // Session table SESSION
    private static final String TABLE_SESSION_NAME = "sessions";  //table name
    private static final String SESSION_COLUMN_ID = "sessionId";  //table name
    private static final String SESSION_COLUMN_GROUPID = "groupId";
    private static final String SESSION_COLUMN_NUMBER = "sessionNumber";
    private static final String SESSION_COLUMN_DAY = "day";
    private static final String SESSION_COLUMN_HOUR = "hour";
    private static final String SESSION_COLUMN_MINUTE = "minute";
    private static final String SESSION_COLUMN_DURATION = "duration";
    private static final String[] TABLE_SESSION_COLUMNS = {SESSION_COLUMN_ID, SESSION_COLUMN_GROUPID, SESSION_COLUMN_NUMBER, SESSION_COLUMN_DAY, SESSION_COLUMN_HOUR, SESSION_COLUMN_MINUTE, SESSION_COLUMN_DURATION};


    // payment table
    private static final String TABLE_PAYMENT_NAME = "payments";  //table name
    private static final String PAYMENT_COLUMN_ID = "paymentId";
    private static final String PAYMENT_COLUMN_USERID = "userId";
    private static final String PAYMENT_COLUMN_MONTH = "month";
    private static final String PAYMENT_COLUMN_YEAR = "year";
    private static final String PAYMENT_COLUMN_AMOUNT = "amount";
    private static final String PAYMENT_COLUMN_METHOD = "method";
    private static final String[] TABLE_PAYMENT_COLUMNS = {PAYMENT_COLUMN_ID, PAYMENT_COLUMN_USERID, PAYMENT_COLUMN_MONTH, PAYMENT_COLUMN_YEAR, PAYMENT_COLUMN_AMOUNT, PAYMENT_COLUMN_METHOD};


    // CANCELED EVENT table
    private static final String TABLE_CANCELED_NAME = "canceledSessions";  //table name
    private static final String CANCELED_COLUMN_ID = "canceledId";  //table name
    private static final String CANCELED_COLUMN_COLOR = "canceledColor";  //table name
    private static final String CANCELED_COLUMN_TIMEINMILLIS = "canceledTimeInMillis";  //table name
    private static final String CANCELED_COLUMN_GROUPID = "canceledGroupId";
    private static final String CANCELED_COLUMN_NUMBER = "canceledSessionNumber";
    private static final String CANCELED_COLUMN_DAY = "canceledDay";
    private static final String CANCELED_COLUMN_HOUR = "canceledHour";
    private static final String CANCELED_COLUMN_MINUTE = "canceleedMinute";
    private static final String CANCELED_COLUMN_DURATION = "canceledDuration";
    private static final String[] TABLE_CANCELED_COLUMNS = {CANCELED_COLUMN_ID, CANCELED_COLUMN_COLOR, CANCELED_COLUMN_TIMEINMILLIS, CANCELED_COLUMN_GROUPID,
            CANCELED_COLUMN_NUMBER, CANCELED_COLUMN_DAY, CANCELED_COLUMN_HOUR, CANCELED_COLUMN_MINUTE, CANCELED_COLUMN_DURATION};
    // Reschedule EVENT table
    private static final String TABLE_RESCHEDULE_NAME = "rescheduleSessions";  //table name
    private static final String RESCHEDULE_COLUMN_ID = "rescheduleId";  //table name
    private static final String RESCHEDULE_COLUMN_COLOR = "rescheduleColor";  //table name
    private static final String RESCHEDULE_COLUMN_TIMEINMILLIS = "rescheduleTimeInMillis";  //table name
    private static final String RESCHEDULE_COLUMN_GROUPID = "rescheduleGroupId";
    private static final String RESCHEDULE_COLUMN_NUMBER = "rescheduleSessionNumber";
    private static final String RESCHEDULE_COLUMN_DAY = "rescheduleDay";
    private static final String RESCHEDULE_COLUMN_HOUR = "rescheduleHour";
    private static final String RESCHEDULE_COLUMN_MINUTE = "rescheduleMinute";
    private static final String RESCHEDULE_COLUMN_DURATION = "rescheduleDuration";
    private static final String[] TABLE_RESCHEDULE_COLUMNS = {RESCHEDULE_COLUMN_ID, RESCHEDULE_COLUMN_COLOR, RESCHEDULE_COLUMN_TIMEINMILLIS
            , RESCHEDULE_COLUMN_GROUPID, RESCHEDULE_COLUMN_NUMBER,
            RESCHEDULE_COLUMN_DAY, RESCHEDULE_COLUMN_HOUR, RESCHEDULE_COLUMN_MINUTE, RESCHEDULE_COLUMN_DURATION};

    // NOT GOING  EVENT table
    private static final String TABLE_NOTGOING_NAME = "notGoingSessions";  //table name
    private static final String NOTGOING_COLUMN_ID = "notGoingId";  //table name
    private static final String NOTGOING_COLUMN_COLOR = "notGoingColor";  //table name
    private static final String NOTGOING_COLUMN_TIMEINMILLIS = "notGoingTimeInMillis";  //table name
    private static final String NOTGOING_COLUMN_GROUPID = "notGoingGroupId";
    private static final String NOTGOING_COLUMN_NUMBER = "notGoingSessionNumber";
    private static final String NOTGOING_COLUMN_DAY = "notGoingDay";
    private static final String NOTGOING_COLUMN_HOUR = "notGoingHour";
    private static final String NOTGOING_COLUMN_MINUTE = "notGoingMinute";
    private static final String NOTGOING_COLUMN_DURATION = "notGoingDuration";
    private static final String NOTGOING_COLUMN_USER1 = "notGoingUser1";
    private static final String NOTGOING_COLUMN_USER2 = "notGoingUser2";
    private static final String[] TABLE_NOTGOING_COLUMNS = {NOTGOING_COLUMN_ID, NOTGOING_COLUMN_COLOR, NOTGOING_COLUMN_TIMEINMILLIS, NOTGOING_COLUMN_GROUPID,
            NOTGOING_COLUMN_NUMBER, NOTGOING_COLUMN_DAY, NOTGOING_COLUMN_HOUR, NOTGOING_COLUMN_MINUTE, NOTGOING_COLUMN_DURATION, NOTGOING_COLUMN_USER1,
            NOTGOING_COLUMN_USER2};

    // measurements table
    private static final String TABLE_MEASUREMENT_NAME = "measurements";  //table name
    private static final String MEASUREMENT_COLUMN_ID = "id";  //table name
    private static final String MEASUREMENT_COLUMN_TRAINEEID = "traineeId";  //table name
    private static final String MEASUREMENT_COLUMN_DATE = "date";  //table name
    private static final String MEASUREMENT_COLUMN_WEIGHT = "weight";  //table name
    private static final String MEASUREMENT_COLUMN_WAIST = "waist";  //table name
    private static final String MEASUREMENT_COLUMN_CHEST = "chest";  //table name
    private static final String MEASUREMENT_COLUMN_BUTTOCKS = "buttocks";  //table name
    private static final String MEASUREMENT_COLUMN_RIGHTARM = "rightArm";  //table name
    private static final String MEASUREMENT_COLUMN_LEFTARM = "leftArm";  //table name
    private static final String MEASUREMENT_COLUMN_RIGHTHIGH = "rightThigh";  //table name
    private static final String MEASUREMENT_COLUMN_LEFTHIGH = "leftThigh";  //table name

    private static final String[] TABLE_MEASUREMENT_COLUMNS = {MEASUREMENT_COLUMN_ID, MEASUREMENT_COLUMN_TRAINEEID, MEASUREMENT_COLUMN_DATE, MEASUREMENT_COLUMN_WEIGHT,
            MEASUREMENT_COLUMN_WAIST, MEASUREMENT_COLUMN_CHEST, MEASUREMENT_COLUMN_BUTTOCKS, MEASUREMENT_COLUMN_RIGHTARM, MEASUREMENT_COLUMN_LEFTARM,
            MEASUREMENT_COLUMN_RIGHTHIGH, MEASUREMENT_COLUMN_LEFTHIGH};


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DBcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // SQL statement to create COACH table
            String CREATE_COACH_TABLE = "create table if not exists " + TABLE_COACH_NAME + " ( "
                    + COACH_COLUMN_ID + " TEXT, "
                    + COACH_COLUMN_EMAIL + " TEXT PRIMARY KEY , "
                    + COACH_COLUMN_FULLNAME + " TEXT, "
                    + COACH_COLUMN_AGE + " TEXT, "
                    + COACH_COLUMN_PHONE + " TEXT, "
                    + COACH_COLUMN_PASSWORD + " TEXT, "
                    + COACH_COLUMN_TYPE + " TEXT, "
                    + COACH_COLUMN_STUDIO + " TEXT)";
            db.execSQL(CREATE_COACH_TABLE);

            // SQL statement to create uSER table
            String CREATE_USER_TABLE = "create table if not exists " + TABLE_USER_NAME + " ( "
                    + USER_COLUMN_ID + " TEXT, "
                    + USER_COLUMN_EMAIL + " TEXT PRIMARY KEY , "
                    + USER_COLUMN_FULLNAME + " TEXT, "
                    + USER_COLUMN_AGE + " TEXT, "
                    + USER_COLUMN_PHONE + " TEXT, "
                    + USER_COLUMN_PASSWORD + " TEXT, "
                    + USER_COLUMN_DATEADDED + " INTEGER , "
                    + USER_COLUMN_TYPE + " TEXT, "
                    + USER_COLUMN_STUDIO + " TEXT, "
                    + USER_COLUMN_GROUPID + " TEXT, "
                    + USER_COLUMN_WEIGHTGOAL + " REAL, "
                    + USER_COLUMN_WEEKLYGOAL + " TEXT)";
            db.execSQL(CREATE_USER_TABLE);

            // SQL statement to create Group table
            String CREATE_GROUP_TABLE = "create table if not exists " + TABLE_GROUP_NAME + " ( "
                    + GROUP_COLUMN_ID + " TEXT PRIMARY KEY , "
                    + GROUP_COLUMN_NAME + " TEXT, "
                    + GROUP_COLUMN_NUMBEROFTRAINEE + " TEXT, "
                    + GROUP_COLUMN_COACHID + " TEXT)";
            db.execSQL(CREATE_GROUP_TABLE);

            // SQL statement to create Session table
            String CREATE_SESSION_TABLE = "create table if not exists " + TABLE_SESSION_NAME + " ( "
                    + SESSION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SESSION_COLUMN_GROUPID + " TEXT , "
                    + SESSION_COLUMN_NUMBER + " TEXT, "
                    + SESSION_COLUMN_DAY + " TEXT, "
                    + SESSION_COLUMN_HOUR + " INTEGER, "
                    + SESSION_COLUMN_MINUTE + " INTEGER, "
                    + SESSION_COLUMN_DURATION + " INTEGER)";
            db.execSQL(CREATE_SESSION_TABLE);

            // SQL statement to create payment table
            String CREATE_PAYMENT_TABLE = "create table if not exists " + TABLE_PAYMENT_NAME + " ( "
                    + PAYMENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PAYMENT_COLUMN_USERID + " TEXT , "
                    + PAYMENT_COLUMN_MONTH + " INTEGER, "
                    + PAYMENT_COLUMN_YEAR + " INTEGER, "
                    + PAYMENT_COLUMN_AMOUNT + " INTEGER, "
                    + PAYMENT_COLUMN_METHOD + " TEXT)";
            db.execSQL(CREATE_PAYMENT_TABLE);


            // SQL statement to create Session table
            String CREATE_CANCELED_TABLE = "create table if not exists " + TABLE_CANCELED_NAME + " ( "
                    + CANCELED_COLUMN_ID + " TEXT PRIMARY KEY , "
                    + CANCELED_COLUMN_COLOR + " INTEGER , "
                    + CANCELED_COLUMN_TIMEINMILLIS + " INTEGER , "
                    + CANCELED_COLUMN_GROUPID + " TEXT , "
                    + CANCELED_COLUMN_NUMBER + " TEXT, "
                    + CANCELED_COLUMN_DAY + " TEXT, "
                    + CANCELED_COLUMN_HOUR + " INTEGER, "
                    + CANCELED_COLUMN_MINUTE + " INTEGER, "
                    + CANCELED_COLUMN_DURATION + " INTEGER)";

            db.execSQL(CREATE_CANCELED_TABLE);
            // SQL statement to create Session table
            String CREATE_RESCHEDULE_TABLE = "create table if not exists " + TABLE_RESCHEDULE_NAME + " ( "
                    + RESCHEDULE_COLUMN_ID + " TEXT PRIMARY KEY , "
                    + RESCHEDULE_COLUMN_COLOR + " INTEGER , "
                    + RESCHEDULE_COLUMN_TIMEINMILLIS + " INTEGER , "
                    + RESCHEDULE_COLUMN_GROUPID + " TEXT , "
                    + RESCHEDULE_COLUMN_NUMBER + " TEXT, "
                    + RESCHEDULE_COLUMN_DAY + " TEXT, "
                    + RESCHEDULE_COLUMN_HOUR + " INTEGER, "
                    + RESCHEDULE_COLUMN_MINUTE + " INTEGER, "
                    + RESCHEDULE_COLUMN_DURATION + " INTEGER)";
            db.execSQL(CREATE_RESCHEDULE_TABLE);

            // SQL statement to create not going table
            String CREATE_NOTGOING_TABLE = "create table if not exists " + TABLE_NOTGOING_NAME + " ( "
                    + NOTGOING_COLUMN_ID + " TEXT PRIMARY KEY , "
                    + NOTGOING_COLUMN_COLOR + " INTEGER , "
                    + NOTGOING_COLUMN_TIMEINMILLIS + " INTEGER , "
                    + NOTGOING_COLUMN_GROUPID + " TEXT , "
                    + NOTGOING_COLUMN_NUMBER + " TEXT, "
                    + NOTGOING_COLUMN_DAY + " TEXT, "
                    + NOTGOING_COLUMN_HOUR + " INTEGER, "
                    + NOTGOING_COLUMN_MINUTE + " INTEGER, "
                    + NOTGOING_COLUMN_DURATION + " INTEGER, "
                    + NOTGOING_COLUMN_USER1 + " TEXT, "
                    + NOTGOING_COLUMN_USER2 + " TEXT)";
            db.execSQL(CREATE_NOTGOING_TABLE);

            // SQL statement to create measurement table
            String CREATE_MEASUREMENT_TABLE = "create table if not exists " + TABLE_MEASUREMENT_NAME + " ( "
                    + MEASUREMENT_COLUMN_ID + " TEXT PRIMARY KEY , "
                    + MEASUREMENT_COLUMN_TRAINEEID + " TEXT , "
                    + MEASUREMENT_COLUMN_DATE + " INTEGER , "
                    + MEASUREMENT_COLUMN_WEIGHT + " REAL , "
                    + MEASUREMENT_COLUMN_WAIST + " REAL , "
                    + MEASUREMENT_COLUMN_CHEST + " REAL, "
                    + MEASUREMENT_COLUMN_BUTTOCKS + " REAL, "
                    + MEASUREMENT_COLUMN_RIGHTARM + " REAL, "
                    + MEASUREMENT_COLUMN_LEFTARM + " REAL, "
                    + MEASUREMENT_COLUMN_RIGHTHIGH + " REAL, "
                    + MEASUREMENT_COLUMN_LEFTHIGH + " REAL)";
            db.execSQL(CREATE_MEASUREMENT_TABLE);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void open() {
        if (db == null) {
            try {
                db = getWritableDatabase();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            db.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    /**
     * METHOD TO INSERT VALUES TO COACH TABLE
     * adD NEW COACH TO SQL
     *
     * @param newCoach
     */
    public void createCoach(CoachInfo newCoach) {
        boolean empty = true;
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM coaches WHERE email= ? ", new String[]{newCoach.getEmail()});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();
                values.put(COACH_COLUMN_ID, newCoach.getId());
                values.put(COACH_COLUMN_EMAIL, newCoach.getEmail());
                values.put(COACH_COLUMN_FULLNAME, newCoach.getFullName());
                values.put(COACH_COLUMN_AGE, newCoach.getAge());
                values.put(COACH_COLUMN_PHONE, newCoach.getPhone());
                values.put(COACH_COLUMN_PASSWORD, newCoach.getPassword());
                values.put(COACH_COLUMN_TYPE, newCoach.getType());
                values.put(COACH_COLUMN_STUDIO, newCoach.getStudio());

                // insert COACH TO COACH TABLE
                db.insert(TABLE_COACH_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    /**
     * ADD NEW USER TO USER TABLE IN SQL
     *
     * @param newUser
     */
    public void createUser(UserInfo newUser) {
        Cursor cursor = null;
//        cursor = db
//                .query(TABLE_USER_NAME, // a. table
//                        TABLE_USER_COLUMNS, USER_COLUMN_EMAIL + " = ?",
//                        new String[]{newUser.getEmail()}, null, null,
//                        null, null);

        boolean empty = true;
//        Cursor cursor = db.rawQuery(
//                "select * from "+TABLE_USER_NAME +" where email = ?", new String[]{email, password});
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM users WHERE email= ? ", new String[]{newUser.getEmail()});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();
                values.put(USER_COLUMN_ID, newUser.getId());
                values.put(USER_COLUMN_EMAIL, newUser.getEmail());
                values.put(USER_COLUMN_FULLNAME, newUser.getFullName());
                values.put(USER_COLUMN_AGE, newUser.getAge());
                values.put(USER_COLUMN_PHONE, newUser.getPhone());
                values.put(USER_COLUMN_PASSWORD, newUser.getPassword());
                values.put(USER_COLUMN_DATEADDED, newUser.getDateAdded());
                values.put(USER_COLUMN_TYPE, newUser.getType());
                values.put(USER_COLUMN_STUDIO, newUser.getStudio());
                if (newUser.getGroupId() != null)
                    values.put(USER_COLUMN_GROUPID, newUser.getGroupId());

                if (newUser.getWeeklyGoal() != null && newUser.getWeightGoal() != null) {
                    values.put(USER_COLUMN_WEIGHTGOAL, newUser.getWeightGoal());
                    values.put(USER_COLUMN_WEEKLYGOAL, newUser.getWeeklyGoal());
                }
                // insert USER TO USER TABLE
                db.insert(TABLE_USER_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    public void createGroup(Group newGroup) {

        boolean empty = true;
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM groups WHERE groupId= ? ", new String[]{newGroup.getGroupId()});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();
                values.put(GROUP_COLUMN_ID, newGroup.getGroupId());
                values.put(GROUP_COLUMN_NAME, newGroup.getGroupName());
                values.put(GROUP_COLUMN_NUMBEROFTRAINEE, newGroup.getNumberOfTrainee());
                values.put(GROUP_COLUMN_COACHID, newGroup.getCoachId());

                // insert group TO group TABLE
                db.insert(TABLE_GROUP_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    public void createSession(Session newSession) {
        boolean empty = true;
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM sessions WHERE groupId= ? AND sessionNumber= ? ", new String[]{newSession.getGroupId(), String.valueOf(newSession.getSessionNumber())});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();
                values.put(SESSION_COLUMN_GROUPID, newSession.getGroupId());
                values.put(SESSION_COLUMN_NUMBER, newSession.getSessionNumber());
                values.put(SESSION_COLUMN_DAY, newSession.getDay());
                values.put(SESSION_COLUMN_HOUR, newSession.getHour());
                values.put(SESSION_COLUMN_MINUTE, newSession.getMinute());
                values.put(SESSION_COLUMN_DURATION, newSession.getDuration());

                // insert session TO session TABLE
                db.insert(TABLE_SESSION_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * CREATE cancel session
     *
     * @param canceledEvent
     */
    public void createCanceledSession(Event canceledEvent, String fStoreId) {
        boolean empty = true;
        Session newSess = ((Session) canceledEvent.getData());
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM canceledSessions WHERE canceledId= ?", new String[]{fStoreId});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();

                values.put(CANCELED_COLUMN_ID, fStoreId);
                values.put(CANCELED_COLUMN_COLOR, canceledEvent.getColor());
                values.put(CANCELED_COLUMN_TIMEINMILLIS, canceledEvent.getTimeInMillis());
                values.put(CANCELED_COLUMN_GROUPID, ((Session) canceledEvent.getData()).getGroupId());
                values.put(CANCELED_COLUMN_NUMBER, ((Session) canceledEvent.getData()).getSessionNumber());
                values.put(CANCELED_COLUMN_DAY, ((Session) canceledEvent.getData()).getDay());
                values.put(CANCELED_COLUMN_HOUR, ((Session) canceledEvent.getData()).getHour());
                values.put(CANCELED_COLUMN_MINUTE, ((Session) canceledEvent.getData()).getMinute());
                values.put(CANCELED_COLUMN_DURATION, ((Session) canceledEvent.getData()).getDuration());

                // insert USER TO session TABLE
                db.insert(TABLE_CANCELED_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }


    public void createRescheduledSession(Event canceledEvent, String fStoreId) {

        boolean empty = true;
        Session newSess = ((Session) canceledEvent.getData());
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM rescheduleSessions WHERE rescheduleId= ?", new String[]{fStoreId});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();


        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();

                values.put(RESCHEDULE_COLUMN_ID, fStoreId);
                values.put(RESCHEDULE_COLUMN_COLOR, canceledEvent.getColor());
                values.put(RESCHEDULE_COLUMN_TIMEINMILLIS, canceledEvent.getTimeInMillis());
                values.put(RESCHEDULE_COLUMN_GROUPID, ((Session) canceledEvent.getData()).getGroupId());
                values.put(RESCHEDULE_COLUMN_NUMBER, ((Session) canceledEvent.getData()).getSessionNumber());
                values.put(RESCHEDULE_COLUMN_DAY, ((Session) canceledEvent.getData()).getDay());
                values.put(RESCHEDULE_COLUMN_HOUR, ((Session) canceledEvent.getData()).getHour());
                values.put(RESCHEDULE_COLUMN_MINUTE, ((Session) canceledEvent.getData()).getMinute());
                values.put(RESCHEDULE_COLUMN_DURATION, ((Session) canceledEvent.getData()).getDuration());

                // insert USER TO session TABLE
                db.insert(TABLE_RESCHEDULE_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void createNotGoingSession(Event notGoingEvent, String fStoreId, String user1) {
        boolean empty = true;
//        Cursor cursor = db.rawQuery(
//                "select * from "+TABLE_USER_NAME +" where email = ?", new String[]{email, password});
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM notGoingSessions WHERE notGoingId= ? ", new String[]{fStoreId});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();

                values.put(NOTGOING_COLUMN_ID, fStoreId);
                values.put(NOTGOING_COLUMN_COLOR, notGoingEvent.getColor());
                values.put(NOTGOING_COLUMN_TIMEINMILLIS, notGoingEvent.getTimeInMillis());
                values.put(NOTGOING_COLUMN_GROUPID, ((Session) notGoingEvent.getData()).getGroupId());
                values.put(NOTGOING_COLUMN_NUMBER, ((Session) notGoingEvent.getData()).getSessionNumber());
                values.put(NOTGOING_COLUMN_DAY, ((Session) notGoingEvent.getData()).getDay());
                values.put(NOTGOING_COLUMN_HOUR, ((Session) notGoingEvent.getData()).getHour());
                values.put(NOTGOING_COLUMN_MINUTE, ((Session) notGoingEvent.getData()).getMinute());
                values.put(NOTGOING_COLUMN_DURATION, ((Session) notGoingEvent.getData()).getDuration());
                values.put(NOTGOING_COLUMN_USER1, user1);

                // insert USER TO session TABLE
                db.insert(TABLE_NOTGOING_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    public void createNotGoingSession(Event notGoingEvent, String fStoreId, String user1, String user2) {
        boolean empty = true;
//        Cursor cursor = db.rawQuery(
//                "select * from "+TABLE_USER_NAME +" where email = ?", new String[]{email, password});
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM notGoingSessions WHERE  notGoingId= ? ", new String[]{fStoreId});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();

                values.put(NOTGOING_COLUMN_ID, fStoreId);
                values.put(NOTGOING_COLUMN_COLOR, notGoingEvent.getColor());
                values.put(NOTGOING_COLUMN_TIMEINMILLIS, notGoingEvent.getTimeInMillis());
                values.put(NOTGOING_COLUMN_GROUPID, ((Session) notGoingEvent.getData()).getGroupId());
                values.put(NOTGOING_COLUMN_NUMBER, ((Session) notGoingEvent.getData()).getSessionNumber());
                values.put(NOTGOING_COLUMN_DAY, ((Session) notGoingEvent.getData()).getDay());
                values.put(NOTGOING_COLUMN_HOUR, ((Session) notGoingEvent.getData()).getHour());
                values.put(NOTGOING_COLUMN_MINUTE, ((Session) notGoingEvent.getData()).getMinute());
                values.put(NOTGOING_COLUMN_DURATION, ((Session) notGoingEvent.getData()).getDuration());
                values.put(NOTGOING_COLUMN_USER1, user1);
                values.put(NOTGOING_COLUMN_USER2, user2);

                // insert USER TO session TABLE
                db.insert(TABLE_NOTGOING_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    //create payment
    public void createPayment(Payment newPayment) {

        try {
            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(PAYMENT_COLUMN_USERID, newPayment.getUserId());
            values.put(PAYMENT_COLUMN_MONTH, newPayment.getMonth());
            values.put(PAYMENT_COLUMN_YEAR, newPayment.getYear());
            values.put(PAYMENT_COLUMN_AMOUNT, newPayment.getAmount());
            values.put(PAYMENT_COLUMN_METHOD, newPayment.getMethod());

            // insert payment TO payment TABLE
            db.insert(TABLE_PAYMENT_NAME, null, values);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    //create payment
    public void createMeasurement(Measurement newMeasurement) {
        boolean empty = true;
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM measurements WHERE id= ? ", new String[]{newMeasurement.getId()});
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) == 0);
        }
        cur.close();

        if (empty) {
            try {
                // make values to be inserted
                ContentValues values = new ContentValues();
                values.put(MEASUREMENT_COLUMN_ID, newMeasurement.getId());
                values.put(MEASUREMENT_COLUMN_TRAINEEID, newMeasurement.getTraineeId());
                values.put(MEASUREMENT_COLUMN_DATE, newMeasurement.getDate());
                values.put(MEASUREMENT_COLUMN_WEIGHT, newMeasurement.getWeight());
                values.put(MEASUREMENT_COLUMN_WAIST, newMeasurement.getWaist());
                values.put(MEASUREMENT_COLUMN_CHEST, newMeasurement.getChest());
                values.put(MEASUREMENT_COLUMN_BUTTOCKS, newMeasurement.getButtocks());
                values.put(MEASUREMENT_COLUMN_RIGHTARM, newMeasurement.getRightArm());
                values.put(MEASUREMENT_COLUMN_LEFTARM, newMeasurement.getLeftArm());
                values.put(MEASUREMENT_COLUMN_RIGHTHIGH, newMeasurement.getRightThigh());
                values.put(MEASUREMENT_COLUMN_LEFTHIGH, newMeasurement.getLeftThigh());

                // insert payment TO payment TABLE
                db.insert(TABLE_MEASUREMENT_NAME, null, values);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    /**
     * get current user
     *
     * @return
     */
    public List<CoachInfo> getCoach() {
        List<CoachInfo> result = new ArrayList<CoachInfo>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_COACH_NAME, TABLE_COACH_COLUMNS, null, null,
                    null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CoachInfo user = cursorToCoach(cursor);
                result.add(user);
                cursor.moveToNext();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    // cursor for coach
    private CoachInfo cursorToCoach(Cursor cursor) {
        CoachInfo result = new CoachInfo();
        try {
            //result.setCommentId(Integer.parseInt(cursor.getString(0)));
            result.setId(cursor.getString(0));
            result.setEmail(cursor.getString(1));
            result.setFullName(cursor.getString(2));
            result.setAge(cursor.getString(3));
            result.setPhone(cursor.getString(4));
            result.setPassword(cursor.getString(5));
            result.setType(cursor.getString(6));
            result.setStudio(cursor.getString(7));

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    /**
     * Get All specific Coach Trainee
     *
     * @param currentCoach - current coach
     * @return - list of coach Trainee ..
     */
    public List<UserInfo> getAllCoachTrainee(CoachInfo currentCoach) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM users WHERE groupId IN  ( SELECT  groupId FROM groups WHERE coachId= ?)", new String[]{String.valueOf(currentCoach.getEmail())});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Get All specific Coach Trainee by age
     *
     * @param currentCoach - current coach
     * @return - list of coach Trainee ..
     */
    public List<UserInfo> getAllTraineeByAgeAndCoach(CoachInfo currentCoach, String ageRange) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM users WHERE age=? AND groupId IN  ( SELECT  groupId FROM groups WHERE coachId= ?)", new String[]{ageRange, String.valueOf(currentCoach.getEmail())});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Query for age report
     *
     * @param currentManager - current manager to get this studio trainees
     * @return trainee list
     */
    public List<UserInfo> getAllManagerTrainee(CoachInfo currentManager) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM users WHERE studio = ?  AND type = ?", new String[]{String.valueOf(currentManager.getStudio()), "user"});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Query for age report
     *
     * @param currentManager - current manager to get this studio trainees
     * @param ageRange       - selected range
     * @return trainee list
     */
    public List<UserInfo> getAllTraineeByAgeAndManager(CoachInfo currentManager, String ageRange) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM users WHERE studio = ?  AND type = ? AND age=?", new String[]{String.valueOf(currentManager.getStudio()), "user", ageRange});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<CoachInfo> getAllManagerCoaches(CoachInfo currentManager) {
        List<CoachInfo> result = new ArrayList<CoachInfo>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM coaches WHERE studio = ?  AND type = ?", new String[]{String.valueOf(currentManager.getStudio()), "coach"});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CoachInfo user = cursorToCoach(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<Group> getAllManagerGroups(CoachInfo currentManager) {
        List<Group> result = new ArrayList<Group>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM groups WHERE coachId IN ( SELECT coachId FROM coaches WHERE studio= ?)", new String[]{currentManager.getStudio()});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Group group = cursorToGroup(cursor);
                result.add(group);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<Session> getAllManagerSessions(CoachInfo currentManager) {
        List<Session> result = new ArrayList<Session>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM sessions WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId IN (SELECT coachId FROM coaches WHERE studio= ? ))", new String[]{currentManager.getStudio()});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Session sess = cursorToSession(cursor);
                result.add(sess);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<Event> getAllManagerReschedule(CoachInfo currentManager) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM rescheduleSessions  WHERE rescheduleGroupId IN ( SELECT groupId FROM groups WHERE coachId IN (SELECT coachId FROM coaches WHERE studio= ? ))", new String[]{currentManager.getStudio()});


            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event resEvent = cursorToEvent(cursor);
                result.add(resEvent);
                cursor.moveToNext();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;

    }

    public List<Event> getAllManagerCanceled(CoachInfo currentManager) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM canceledSessions  WHERE canceledGroupId IN ( SELECT groupId FROM groups WHERE coachId IN (SELECT coachId FROM coaches WHERE studio= ? ))", new String[]{currentManager.getStudio()});


            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event resEvent = cursorToEvent(cursor);
                result.add(resEvent);
                cursor.moveToNext();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;

    }

    public List<Event> getAllManagerCanceledToReschedule(CoachInfo currentManager) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM canceledSessions  WHERE canceledId NOT IN (SELECT rescheduleId FROM rescheduleSessions) AND canceledGroupId IN ( SELECT groupId FROM groups WHERE coachId IN (SELECT coachId FROM coaches WHERE studio= ? ))", new String[]{currentManager.getStudio()});


            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event resEvent = cursorToEvent(cursor);
                result.add(resEvent);
                cursor.moveToNext();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;

    }

    // cursor for user
    private UserInfo cursorToUser(Cursor cursor) {
        UserInfo result = new UserInfo();
        try {
            //result.setCommentId(Integer.parseInt(cursor.getString(0)));
            result.setId(cursor.getString(0));
            result.setEmail(cursor.getString(1));
            result.setFullName(cursor.getString(2));
            result.setAge(cursor.getString(3));
            result.setPhone(cursor.getString(4));
            result.setPassword(cursor.getString(5));
            result.setDateAdded(cursor.getLong(6));
            result.setType(cursor.getString(7));
            result.setStudio(cursor.getString(8));
            result.setGroupId(cursor.getString(9));
            if (cursor.getString(10) != null && cursor.getString(11) != null) {
                result.setWeightGoal(cursor.getDouble(10));
                result.setWeeklyGoal(cursor.getString(11));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private Integer cursorToDate(Cursor cursor) {
        Date date = new Date();
        try {
            date = new Date(cursor.getLong(0));

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return date.getYear() + 1900;
    }

    // cursor for user & amount
    private Map<UserInfo, Integer> cursorToUserAmount(Cursor cursor) {
        Map<UserInfo, Integer> result = new LinkedHashMap<>();
        UserInfo user = new UserInfo();
        try {
            //result.setCommentId(Integer.parseInt(cursor.getString(0)));
            user.setId(cursor.getString(0));
            user.setEmail(cursor.getString(1));
            user.setFullName(cursor.getString(2));
            result.put(user, cursor.getInt(3));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    /**
     * Update user Info
     *
     * @param user
     * @return
     */
    public int updateUser(UserInfo user) {
        int i = 0;
        try {

            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(USER_COLUMN_FULLNAME, user.getFullName());
            values.put(USER_COLUMN_AGE, user.getAge());
            values.put(USER_COLUMN_PHONE, user.getPhone());
            values.put(USER_COLUMN_GROUPID, user.getGroupId());
            values.put(USER_COLUMN_PASSWORD, user.getPassword());
            values.put(USER_COLUMN_STUDIO, user.getStudio());


            // update
            i = db.update(TABLE_USER_NAME, values, USER_COLUMN_EMAIL + " = ?",
                    new String[]{String.valueOf(user.getEmail())});
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return i;
    }

    /**
     * delte user
     *
     * @param user delete user
     * @return
     */
    public int deleteUser(UserInfo user) {
        int i = 0;
        try {

//            // make values to be inserted
//            ContentValues values = new ContentValues();
//            values.put(PAYMENT_COLUMN_AMOUNT, payment.getAmount());
//            values.put(PAYMENT_COLUMN_METHOD, payment.getMethod());

            // delete
            i = db.delete(TABLE_USER_NAME, USER_COLUMN_EMAIL + " = ? ",
                    new String[]{String.valueOf(user.getEmail())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }


    /**
     * Update user Info
     *
     * @param coach
     * @return
     */
    public int updateCoach(CoachInfo coach) {
        int i = 0;
        try {

            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(COACH_COLUMN_FULLNAME, coach.getFullName());
            values.put(COACH_COLUMN_AGE, coach.getAge());
            values.put(COACH_COLUMN_PHONE, coach.getPhone());
            values.put(COACH_COLUMN_PASSWORD, coach.getPassword());

            // update
            i = db.update(TABLE_COACH_NAME, values, COACH_COLUMN_EMAIL + " = ?",
                    new String[]{String.valueOf(coach.getEmail())});
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return i;
    }

    public int deleteCoach(CoachInfo coach) {
        int i = 0;
        try {

//            // make values to be inserted
//            ContentValues values = new ContentValues();
//            values.put(PAYMENT_COLUMN_AMOUNT, payment.getAmount());
//            values.put(PAYMENT_COLUMN_METHOD, payment.getMethod());

            // delete
            i = db.delete(TABLE_COACH_NAME, COACH_COLUMN_EMAIL + " = ? ",
                    new String[]{String.valueOf(coach.getEmail())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }

    /**
     * Update user goals
     *
     * @param user
     * @return
     */
    public int updateUserGoal(UserInfo user) {
        int i = 0;
        try {
            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(USER_COLUMN_WEIGHTGOAL, user.getWeightGoal());
            values.put(USER_COLUMN_WEEKLYGOAL, user.getWeeklyGoal());

            // update
            i = db.update(TABLE_USER_NAME, values, USER_COLUMN_EMAIL + " = ?",
                    new String[]{String.valueOf(user.getEmail())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }

    /**
     * Update user payment
     *
     * @param payment
     * @return
     */
    public int updatePayment(Payment payment) {
        int i = 0;
        try {

            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(PAYMENT_COLUMN_AMOUNT, payment.getAmount());
            values.put(PAYMENT_COLUMN_METHOD, payment.getMethod());

            // update
            i = db.update(TABLE_PAYMENT_NAME, values, PAYMENT_COLUMN_USERID + " = ? AND " + PAYMENT_COLUMN_MONTH + " = ? AND " + PAYMENT_COLUMN_YEAR + " = ?",
                    new String[]{String.valueOf(payment.getUserId()), String.valueOf(payment.getMonth()), String.valueOf(payment.getYear())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }

    /**
     * delete user payment
     *
     * @param payment
     * @return
     */
    public int deletePayment(Payment payment) {
        int i = 0;
        try {

//            // make values to be inserted
//            ContentValues values = new ContentValues();
//            values.put(PAYMENT_COLUMN_AMOUNT, payment.getAmount());
//            values.put(PAYMENT_COLUMN_METHOD, payment.getMethod());

            // delete
            i = db.delete(TABLE_PAYMENT_NAME, PAYMENT_COLUMN_USERID + " = ? AND " + PAYMENT_COLUMN_MONTH + " = ? AND " + PAYMENT_COLUMN_YEAR + " = ?",
                    new String[]{String.valueOf(payment.getUserId()), String.valueOf(payment.getMonth()), String.valueOf(payment.getYear())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }

    /**
     * Update Group Info
     *
     * @param group
     * @return
     */
    public int updateGroup(Group group) {
        int i = 0;
        try {

            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(GROUP_COLUMN_NAME, group.getGroupName());
            values.put(GROUP_COLUMN_NUMBEROFTRAINEE, group.getNumberOfTrainee());
            values.put(GROUP_COLUMN_COACHID, group.getCoachId());

            // update
            i = db.update(TABLE_GROUP_NAME, values, GROUP_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(group.getGroupId())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }


    public int updateNotGoingUser2(String user2Id, String eventId) {
        int i = 0;
        try {

            // make values to be inserted
            ContentValues values = new ContentValues();
            values.put(NOTGOING_COLUMN_USER2, user2Id);
            values.put(NOTGOING_COLUMN_COLOR, Color.GREEN);

            //also change event color so it shows Green not yellow

            // update
            i = db.update(TABLE_NOTGOING_NAME, values, NOTGOING_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(eventId)});
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return i;
    }


    public void deleteGroup(Group group) {
        boolean succeded = false;
        try {

            // delete folder
            int rowAffected = db.delete(TABLE_GROUP_NAME, GROUP_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(group.getGroupId())});
            if (rowAffected > 0) {
                succeded = true;
            }

        } catch (Throwable t) {
            succeded = false;
            t.printStackTrace();
        } finally {
            if (succeded) {
                deleteGroupSessions(group);
            }
        }
    }


    public void deleteGroupSessions(Group group) {

        try {

            // delete items
            db.delete(TABLE_SESSION_NAME, SESSION_COLUMN_GROUPID + " = ?",
                    new String[]{String.valueOf(group.getGroupId())});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void deleteCanceled(String canceledId) {

        try {

            // delete items
            db.delete(TABLE_CANCELED_NAME, CANCELED_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(canceledId)});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public List<Group> getAllGroups() {
        List<Group> result = new ArrayList<Group>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_GROUP_NAME, TABLE_GROUP_COLUMNS, null, null,
                    null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Group group = cursorToGroup(cursor);
                result.add(group);
                cursor.moveToNext();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    // cursor for group
    private Group cursorToGroup(Cursor cursor) {
        Group result = new Group();
        try {
            //result.setId(Integer.parseInt(cursor.getString(0)));
            result.setGroupId(cursor.getString(0));
            result.setGroupName(cursor.getString(1));
            result.setNumberOfTrainee(cursor.getString(2));
            result.setCoachId(cursor.getString(3));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public List<Event> getAllCanceledSessions() {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CANCELED_NAME, TABLE_CANCELED_COLUMNS, null, null,
                    null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event canceledEvent = cursorToEvent(cursor);
                result.add(canceledEvent);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<Event> getAllGroupCanceledSessions(Group group) {


        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM canceledSessions WHERE canceledGroupId= ?", new String[]{group.getGroupId()});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event canceledEvent = cursorToEvent(cursor);
                result.add(canceledEvent);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<Event> getAllGroupRescheduleSessions(Group group) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RESCHEDULE_NAME, TABLE_RESCHEDULE_COLUMNS, RESCHEDULE_COLUMN_GROUPID + " = ?",
                    new String[]{String.valueOf(group.getGroupId())}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Event event = cursorToEvent(cursor);
                    result.add(event);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;

    }

    public List<Event> getAllRescheduledSessions() {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RESCHEDULE_NAME, TABLE_RESCHEDULE_COLUMNS, null, null,
                    null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event resEvent = cursorToEvent(cursor);
                result.add(resEvent);
                cursor.moveToNext();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private Event cursorToEvent(Cursor cursor) {
        Session session = new Session(cursor.getString(3), cursor.getString(4), cursor.getString(5),
                cursor.getInt(6), cursor.getInt(7), cursor.getInt(8));
        Event result = new Event(cursor.getInt(1), cursor.getLong(2), session);
        try {
            //result.setId(Integer.parseInt(cursor.getString(0)));

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public List<Event> getAllCanceledByGroupId(String groupId) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CANCELED_NAME, TABLE_CANCELED_COLUMNS, CANCELED_COLUMN_GROUPID + " = ?",
                    new String[]{String.valueOf(groupId)}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Event event = cursorToEvent(cursor);
                    result.add(event);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public Group getGroup(String id) {
        Group group = null;
        Cursor cursor = null;
        try {
            // get reference of the folderDB database

            // get  query
            cursor = db
                    .query(TABLE_GROUP_NAME, // a. table
                            TABLE_GROUP_COLUMNS, GROUP_COLUMN_ID + " = ?",
                            new String[]{id}, null, null,
                            null, null);

            // if results !=null, parse the first one
            if (cursor != null)
                cursor.moveToFirst();

            group = new Group();
            group.setGroupId(cursor.getString(0));
            //folder.setId(Integer.parseInt(cursor.getString(0)));
            group.setGroupName(cursor.getString(1));
            group.setNumberOfTrainee(cursor.getString(2));
            group.setCoachId(cursor.getString(3));

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return group;
    }

    public CoachInfo getCoach(String id) {
        CoachInfo coach = null;
        Cursor cursor = null;
        try {
            // get reference of the folderDB database

            // get  query
            cursor = db
                    .query(TABLE_COACH_NAME, // a. table
                            TABLE_COACH_COLUMNS, COACH_COLUMN_EMAIL + " = ?",
                            new String[]{id}, null, null,
                            null, null);

            // if results !=null, parse the first one
            if (cursor != null)
                cursor.moveToFirst();
            coach = new CoachInfo();
            coach.setId(cursor.getString(0));
            //folder.setId(Integer.parseInt(cursor.getString(0)));
            coach.setEmail(cursor.getString(1));
            coach.setFullName(cursor.getString(2));
            coach.setAge(cursor.getString(3));
            coach.setPhone(cursor.getString(4));
            coach.setPassword(cursor.getString(5));
            coach.setType(cursor.getString(6));
            coach.setStudio(cursor.getString(7));

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return coach;
    }

    public List<Group> getAllCoachGroup(String coachId) {
        List<Group> result = new ArrayList<Group>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_GROUP_NAME, TABLE_GROUP_COLUMNS, GROUP_COLUMN_COACHID + " = ?",
                    new String[]{String.valueOf(coachId)}, null, null,
                    GROUP_COLUMN_NAME, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Group group = cursorToGroup(cursor);
                    result.add(group);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<Session> getAllGroupSessions(Group group) {
        List<Session> result = new ArrayList<Session>();
        Cursor cursor = null;
        try {
            String groupId = group.getGroupId();
            cursor = db.query(TABLE_SESSION_NAME, TABLE_SESSION_COLUMNS, SESSION_COLUMN_GROUPID + " = ?",
                    new String[]{String.valueOf(groupId)}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Session session = cursorToSession(cursor);
                    result.add(session);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }


    //get paid payments by user and year
    public List<Integer> getAllPaidPayments(UserInfo user, int year) {
        List<Integer> result = new ArrayList<Integer>();
        Cursor cursor = null;
        try {
            String userID = user.getEmail();
            cursor = db.query(TABLE_PAYMENT_NAME, TABLE_PAYMENT_COLUMNS, PAYMENT_COLUMN_USERID + "= ? AND " + PAYMENT_COLUMN_YEAR + "= ?",
                    new String[]{String.valueOf(userID), String.valueOf(year)}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Payment payment = cursorToPayment(cursor);
                    result.add(payment.getMonth());
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    //get paid years of payments by user
    public List<Integer> getAllPaymentsYears(UserInfo user) {
        List<Integer> result = new ArrayList<Integer>();
        Cursor cursor = null;
        try {
            String userID = user.getEmail();
            cursor = db.query(true, TABLE_PAYMENT_NAME, TABLE_PAYMENT_COLUMNS, PAYMENT_COLUMN_USERID + "= ?",
                    new String[]{String.valueOf(userID)}, PAYMENT_COLUMN_YEAR, null,
                    PAYMENT_COLUMN_YEAR, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Payment payment = cursorToPayment(cursor);
                    result.add(payment.getYear());
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<Integer> getAllJoinedYears() {
        List<Integer> result = new ArrayList<Integer>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT dateAdded FROM users ", null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Integer year = cursorToDate(cursor);
                    if (!result.contains(year))
                        result.add(year);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    //get paid years of payments
    public List<Integer> getAllPaymentsYears() {
        List<Integer> result = new ArrayList<Integer>();
        Cursor cursor = null;
        try {
            cursor = db.query(true, TABLE_PAYMENT_NAME, TABLE_PAYMENT_COLUMNS, null,
                    null, PAYMENT_COLUMN_YEAR, null,
                    PAYMENT_COLUMN_YEAR, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Payment payment = cursorToPayment(cursor);
                    result.add(payment.getYear());
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }


    //get paid payment by user, month and year
    public Payment getPayment(UserInfo user, int month, int year) {
        Payment result = new Payment();
        Cursor cursor = null;
        try {
            String userID = user.getEmail();
            cursor = db.query(TABLE_PAYMENT_NAME, TABLE_PAYMENT_COLUMNS, PAYMENT_COLUMN_USERID + "= ? AND " + PAYMENT_COLUMN_YEAR + "= ? AND " + PAYMENT_COLUMN_MONTH + "= ?",
                    new String[]{String.valueOf(userID), String.valueOf(year), String.valueOf(month)}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Payment payment = cursorToPayment(cursor);
                    result = payment;
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    //get all users paid payment in month and year
    public List<UserInfo> getAllNotPaidReport(int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE email NOT IN(SELECT userId FROM payments WHERE month=? AND year=? )", new String[]{String.valueOf(month), String.valueOf(year)});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    //get all users paid payments by group, month and year
    public List<UserInfo> getGroupPaidReport(Group group, int month, int year) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE groupId = ? AND email NOT IN(SELECT userId FROM payments WHERE month=? AND year=? )", new String[]{String.valueOf(group.getGroupId()), String.valueOf(month), String.valueOf(year)});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    /**
     * get all trainee of studio that join in specfiv month
     *
     * @param manager - current manager
     * @param month   - choosen month
     * @param year    - choose year
     * @return - list of trainee
     */
    public List<UserInfo> getAllJoinedManager(CoachInfo manager, int month, int year) {
//Date added
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
        firstDayOfMonth.set(Calendar.MINUTE, 00);
        firstDayOfMonth.set(Calendar.SECOND, 00);
        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
        firstDayOfMonth.set(Calendar.YEAR, year);
        firstDayOfMonth.set(Calendar.MONTH, month - 1);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);
        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
        lastDayOfMonth.set(Calendar.YEAR, year);
        lastDayOfMonth.set(Calendar.MONTH, month - 1);
        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, res);
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE studio = ?  AND type = ? and dateAdded>= ? and dateAdded<= ? ORDER BY dateAdded ASC", new String[]{String.valueOf(manager.getStudio()), "user", String.valueOf(firstDayOfMonth.getTimeInMillis()), String.valueOf(lastDayOfMonth.getTimeInMillis())});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllJoinedThisYearManager(CoachInfo manager, int year) {
//Date added
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
        firstDayOfMonth.set(Calendar.MINUTE, 00);
        firstDayOfMonth.set(Calendar.SECOND, 00);
        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
        firstDayOfMonth.set(Calendar.YEAR, year);
        firstDayOfMonth.set(Calendar.MONTH, 0);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);
        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
        lastDayOfMonth.set(Calendar.YEAR, year);
        lastDayOfMonth.set(Calendar.MONTH, 11);
        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, res);
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE studio = ?  AND type = ? and dateAdded>= ? and dateAdded<= ? ORDER BY dateAdded ASC", new String[]{String.valueOf(manager.getStudio()), "user", String.valueOf(firstDayOfMonth.getTimeInMillis()), String.valueOf(lastDayOfMonth.getTimeInMillis())});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
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
//Date added
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
        firstDayOfMonth.set(Calendar.MINUTE, 00);
        firstDayOfMonth.set(Calendar.SECOND, 00);
        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
        firstDayOfMonth.set(Calendar.YEAR, year);
        firstDayOfMonth.set(Calendar.MONTH, month - 1);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);
        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
        lastDayOfMonth.set(Calendar.YEAR, year);
        lastDayOfMonth.set(Calendar.MONTH, month - 1);
        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, res);
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE dateAdded>= ? AND dateAdded<= ? AND groupId IN  ( SELECT  groupId FROM groups WHERE coachId= ? ) ORDER BY dateAdded ASC", new String[]{String.valueOf(firstDayOfMonth.getTimeInMillis()), String.valueOf(lastDayOfMonth.getTimeInMillis()), String.valueOf(coach.getEmail())});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllJoinedThisYearCoach(CoachInfo coach, int year) {
//Date added
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
        firstDayOfMonth.set(Calendar.MINUTE, 00);
        firstDayOfMonth.set(Calendar.SECOND, 00);
        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
        firstDayOfMonth.set(Calendar.YEAR, year);
        firstDayOfMonth.set(Calendar.MONTH, 0);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);
        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
        lastDayOfMonth.set(Calendar.YEAR, year);
        lastDayOfMonth.set(Calendar.MONTH, 11);
        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, res);
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE dateAdded>= ? AND dateAdded<= ? AND groupId IN  ( SELECT  groupId FROM groups WHERE coachId= ? ) ORDER BY dateAdded ASC", new String[]{String.valueOf(firstDayOfMonth.getTimeInMillis()), String.valueOf(lastDayOfMonth.getTimeInMillis()), String.valueOf(coach.getEmail())});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
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
//Date added
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
        firstDayOfMonth.set(Calendar.MINUTE, 00);
        firstDayOfMonth.set(Calendar.SECOND, 00);
        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
        firstDayOfMonth.set(Calendar.YEAR, year);
        firstDayOfMonth.set(Calendar.MONTH, month - 1);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);
        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
        lastDayOfMonth.set(Calendar.YEAR, year);
        lastDayOfMonth.set(Calendar.MONTH, month - 1);
        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, res);
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE dateAdded>= ? AND dateAdded<= ? AND groupId = ?  ORDER BY dateAdded ASC", new String[]{String.valueOf(firstDayOfMonth.getTimeInMillis()), String.valueOf(lastDayOfMonth.getTimeInMillis()), String.valueOf(group.getGroupId())});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllJoinedByGroupAllYear(Group group, int year) {
//Date added
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
        firstDayOfMonth.set(Calendar.MINUTE, 00);
        firstDayOfMonth.set(Calendar.SECOND, 00);
        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
        firstDayOfMonth.set(Calendar.YEAR, year);
        firstDayOfMonth.set(Calendar.MONTH, 0);
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfMonth.set(Calendar.MINUTE, 59);
        lastDayOfMonth.set(Calendar.SECOND, 59);
        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
        lastDayOfMonth.set(Calendar.YEAR, year);
        lastDayOfMonth.set(Calendar.MONTH, 11);
        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
        lastDayOfMonth.set(Calendar.DAY_OF_MONTH, res);
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE dateAdded>= ? AND dateAdded<= ? AND groupId = ?  ORDER BY dateAdded ASC", new String[]{String.valueOf(firstDayOfMonth.getTimeInMillis()), String.valueOf(lastDayOfMonth.getTimeInMillis()), String.valueOf(group.getGroupId())});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    UserInfo user = cursorToUser(cursor);
                    result.add(user);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    //get group by name
    public Group getGroupByName(String groupName) {
        Group result = new Group();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM groups WHERE groupName=? ", new String[]{String.valueOf(groupName)});

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Group group = cursorToGroup(cursor);
                    result = group;
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    // all users payments
    public List<Payment> getAllUserPayments(UserInfo user) {
        List<Payment> result = new ArrayList<Payment>();
        Cursor cursor = null;
        try {
            String userId = user.getEmail();
            cursor = db.query(TABLE_PAYMENT_NAME, TABLE_PAYMENT_COLUMNS, PAYMENT_COLUMN_USERID + " = ?",
                    new String[]{userId}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Payment payment = cursorToPayment(cursor);
                    result.add(payment);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<Measurement> getTraineeMeasurement(String traineeId) {
        List<Measurement> result = new ArrayList<Measurement>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MEASUREMENT_NAME, TABLE_MEASUREMENT_COLUMNS, MEASUREMENT_COLUMN_TRAINEEID + " = ?",
                    new String[]{String.valueOf(traineeId)}, null, null,
                    MEASUREMENT_COLUMN_DATE + " ASC", null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Measurement mes = cursorToMeasurement(cursor);
                    result.add(mes);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public Double getLatestWeightPrgress(UserInfo currentItem) {


        return new Double(5);
    }

    // cursor for group
    private Measurement cursorToMeasurement(Cursor cursor) {
        Measurement result = new Measurement();
        try {
            //result.setId(Integer.parseInt(cursor.getString(0)));
            result.setId(cursor.getString(0));
            result.setTraineeId(cursor.getString(1));
            result.setDate(cursor.getLong(2));
            result.setWeight(cursor.getDouble(3));
            result.setWaist(cursor.getDouble(4));
            result.setChest(cursor.getDouble(5));
            result.setButtocks(cursor.getDouble(6));
            result.setRightArm(cursor.getDouble(7));
            result.setLeftArm(cursor.getDouble(8));
            result.setRightThigh(cursor.getDouble(9));
            result.setLeftThigh(cursor.getDouble(10));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    // cursor for session
    private Session cursorToSession(Cursor cursor) {
        Session result = new Session();
        try {
            //result.setId(Integer.parseInt(cursor.getString(0)));
            result.setGroupId(cursor.getString(1));
            result.setSessionNumber(cursor.getString(2));
            result.setDay(cursor.getString(3));
            result.setHour(cursor.getInt(4));
            result.setMinute(cursor.getInt(5));
            result.setDuration(cursor.getInt(6));

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    // cursor for payment
    private Payment cursorToPayment(Cursor cursor) {
        Payment result = new Payment();
        try {
            result.setNumber(Integer.parseInt(cursor.getString(0)));
            result.setUserId(cursor.getString(1));
            result.setMonth(cursor.getInt(2));
            result.setYear(cursor.getInt(3));
            result.setAmount(cursor.getInt(4));
            result.setMethod(cursor.getString(5));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public String getCanceledId(Event event) {
        String group = null;
        Cursor cursor = null;
        Session session = ((Session) event.getData());
        try {
            // get reference of the folderDB database
            cursor = db.query(TABLE_CANCELED_NAME, TABLE_CANCELED_COLUMNS, CANCELED_COLUMN_COLOR + " = ? AND " + CANCELED_COLUMN_TIMEINMILLIS + " = ? AND " + CANCELED_COLUMN_GROUPID + " = ? AND " + CANCELED_COLUMN_NUMBER + " = ? AND " +
                            CANCELED_COLUMN_DAY + " = ? AND " + CANCELED_COLUMN_HOUR + " = ? AND " + CANCELED_COLUMN_MINUTE + " = ? AND " + CANCELED_COLUMN_DURATION + " = ? ",
                    new String[]{String.valueOf(event.getColor()), String.valueOf(event.getTimeInMillis()), session.getGroupId(), session.getSessionNumber(),
                            session.getDay(), String.valueOf(session.getHour()), String.valueOf(session.getMinute()), String.valueOf(session.getDuration())}, null, null,
                    null, null);

            // if results !=null, parse the first one
            if (cursor != null)
                cursor.moveToFirst();

            group = (cursor.getString(0));

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return group;
    }

    public String getNotGoingId(Event event) {
        String getNotGoingId = null;
        Cursor cursor = null;
        Session session = ((Session) event.getData());
        try {
            // get reference of the folderDB database
            cursor = db.query(TABLE_NOTGOING_NAME, TABLE_NOTGOING_COLUMNS, NOTGOING_COLUMN_COLOR + " = ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " = ? AND " + NOTGOING_COLUMN_GROUPID + " = ? AND " + NOTGOING_COLUMN_NUMBER + " = ? AND " +
                            NOTGOING_COLUMN_DAY + " = ? AND " + NOTGOING_COLUMN_HOUR + " = ? AND " + NOTGOING_COLUMN_MINUTE + " = ? AND " + NOTGOING_COLUMN_DURATION + " = ? ",
                    new String[]{String.valueOf(event.getColor()), String.valueOf(event.getTimeInMillis()), session.getGroupId(), session.getSessionNumber(),
                            session.getDay(), String.valueOf(session.getHour()), String.valueOf(session.getMinute()), String.valueOf(session.getDuration())}, null, null,
                    null, null);

            // if results !=null, parse the first one
            if (cursor != null)
                cursor.moveToFirst();

            getNotGoingId = (cursor.getString(0));

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return getNotGoingId;
    }

    public List<Event> getAllNotGoingForUser(String userId) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NOTGOING_NAME, TABLE_NOTGOING_COLUMNS, NOTGOING_COLUMN_USER1 + " = ?",
                    new String[]{String.valueOf(userId)}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Event event = cursorToEvent(cursor);
                    result.add(event);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }


    public int getNotGoingCountThisMonth(long firstDay, long lastday, String userId) {
        Cursor cursor = null;
        long i = 0;
        int count = 0;
        try {
            // get reference of the folderDB database
            cursor = db.query(TABLE_NOTGOING_NAME, TABLE_NOTGOING_COLUMNS, NOTGOING_COLUMN_USER1 + " = ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " > ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " < ? ",
                    new String[]{String.valueOf(userId), String.valueOf(firstDay), String.valueOf(lastday)}, null, null,
                    null, null);

//            i= DatabaseUtils.queryNumEntries(db, "notGoingSessions",
//                    "notGoingUser1=? AND notGoingTimeInMillies>? AND notGoingTimeInMillies<?", new String[] {String.valueOf(userId), String.valueOf(firstDay), String.valueOf(lastday)});
            count = cursor.getCount();

            // if results !=null, parse the first one
            if (cursor != null)
                cursor.moveToFirst();
            cursor.getCount();

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public List<Event> getNotGoingCountThisMonthList(long firstDay, long lastday, String userId) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            // get reference of the folderDB database
            cursor = db.query(TABLE_NOTGOING_NAME, TABLE_NOTGOING_COLUMNS, NOTGOING_COLUMN_USER1 + " = ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " > ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " < ? ",
                    new String[]{String.valueOf(userId), String.valueOf(firstDay), String.valueOf(lastday)}, null, null,
                    null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Event event = cursorToEvent(cursor);
                    result.add(event);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<Event> getAvailableThisMonthList(long firstDay, long lastday, String userId) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            // get reference of the folderDB database
//            cursor = db.query(TABLE_NOTGOING_NAME, TABLE_NOTGOING_COLUMNS, NOTGOING_COLUMN_USER2 + " = ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " > ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " < ? " ,
//                    new String[]{"", String.valueOf(firstDay), String.valueOf(lastday)}, null, null,
//                    null, null);

            cursor = db.rawQuery("SELECT * FROM notGoingSessions WHERE notGoingUser2 IS NULL AND  notGoingTimeInMillis >" + firstDay + " AND notGoingTimeInMillis < " + lastday, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Event event = cursorToEvent(cursor);
                    result.add(event);
                    cursor.moveToNext();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }


    public List<Event> User2isCurrentUser(long firstDay, long lastday, String userId) {
        List<Event> result = new ArrayList<Event>();
        Cursor cursor = null;
        try {
            // get reference of the folderDB database
            cursor = db.query(TABLE_NOTGOING_NAME, TABLE_NOTGOING_COLUMNS, NOTGOING_COLUMN_USER2 + " = ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " > ? AND " + NOTGOING_COLUMN_TIMEINMILLIS + " < ? ",
                    new String[]{userId, String.valueOf(firstDay), String.valueOf(lastday)}, null, null,
                    null, null);

//            cursor=db.rawQuery("SELECT * FROM notGoingSessions WHERE notGoingUser2 IS NOT NULL AND notGoingUser2=" + userId + " AND notGoingTimeInMillis >" + firstDay + " AND notGoingTimeInMillis < " + lastday, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Event event = cursorToEvent(cursor);
                    result.add(event);
                    cursor.moveToNext();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllGroupTrainee(CoachInfo currentCoach, String groupName) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?)", new String[]{String.valueOf(currentCoach.getEmail()), groupName});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }


    public List<UserInfo> getAllTrainee(String groupName) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE groupId IN ( SELECT groupId FROM groups WHERE groupName=?)", new String[]{groupName});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllGroupTrainee(Group group) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE  groupId=?", new String[]{String.valueOf(group.getGroupId())});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllAgeGroupTrainee(Group group, String age) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            if (age.equals(DBcontext.getResources().getString(R.string.allAges)) && group.getGroupName().equals(DBcontext.getResources().getString(R.string.allTrainee))) {
                cursor = db.query(TABLE_USER_NAME, TABLE_USER_COLUMNS, null, null,
                        null, null, null);
            } else if (age.equals(DBcontext.getResources().getString(R.string.allAges))) {
                cursor = db.rawQuery("SELECT * FROM users WHERE  groupId=?", new String[]{String.valueOf(group.getGroupId())});
            } else if (group.getGroupName().equals(DBcontext.getResources().getString(R.string.allTrainee))) {
                cursor = db.rawQuery("SELECT * FROM users WHERE  age=?", new String[]{age});
            } else
                cursor = db.rawQuery("SELECT * FROM users WHERE  groupId=? AND age=?", new String[]{String.valueOf(group.getGroupId()), age});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<UserInfo> getAllAgeCoachTrainee(Group group, String age, CoachInfo coachInfo) {
        List<UserInfo> result = new ArrayList<UserInfo>();
        Cursor cursor = null;
        try {
            if (age.equals(DBcontext.getResources().getString(R.string.allAges)) && group.getGroupName().equals(DBcontext.getResources().getString(R.string.allTrainee))) {
                cursor = db.rawQuery("SELECT * FROM users WHERE  groupId IN (SELECT groupId from groups Where coachId=?)", new String[]{String.valueOf(coachInfo.getEmail())});

            } else if (age.equals(DBcontext.getResources().getString(R.string.allAges))) {
                cursor = db.rawQuery("SELECT * FROM users WHERE  groupId=?", new String[]{String.valueOf(group.getGroupId())});
            } else if (group.getGroupName().equals(DBcontext.getResources().getString(R.string.allTrainee))) {
                cursor = db.rawQuery("SELECT * FROM users WHERE  groupId IN (SELECT groupId from groups Where coachId=?) AND age=?", new String[]{String.valueOf(coachInfo.getEmail()), age});
            } else
                cursor = db.rawQuery("SELECT * FROM users WHERE  groupId=? AND age=?", new String[]{String.valueOf(group.getGroupId()), age});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
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
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE groupId IN ( SELECT groupId FROM groups WHERE groupName= ?)", new String[]{groupName});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserInfo user = cursorToUser(cursor);
                result.add(user);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public CoachInfo getGroupCoach(Group group) {
        CoachInfo result = new CoachInfo();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM coaches WHERE email = ?", new String[]{String.valueOf(group.getCoachId())});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CoachInfo user = cursorToCoach(cursor);
                result = user;
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public Map<UserInfo, Integer> getMonthlyUserReport(int month, int year, String groupName, String method, String CoachId) {
        Map<UserInfo, Integer> result = new LinkedHashMap<>();
        Cursor cursor = null;
        try {
            if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups)) && method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments ON userId = email Where  month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ?))", new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(CoachId)});
            } else if (method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments ON userId = email Where year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            } else if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups))) {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments ON userId = email Where method=? AND month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? ))", new String[]{method, String.valueOf(month), String.valueOf(year), String.valueOf(CoachId)});
            } else {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments on userId = email Where method=? AND month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{method, String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Map<UserInfo, Integer> userAndAmount = cursorToUserAmount(cursor);
                if (userAndAmount != null) {
                    result.putAll(userAndAmount);
                }
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public Map<UserInfo, Integer> getManagerMonthlyUserReport(int month, int year, String groupName, String method, String CoachId) {
        Map<UserInfo, Integer> result = new LinkedHashMap<>();
        Cursor cursor = null;
        try {
            if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups)) && method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments ON userId = email Where  month=? AND year=?", new String[]{String.valueOf(month), String.valueOf(year)});
            } else if (method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments ON userId = email Where year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            } else if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups))) {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments ON userId = email Where method=? AND month=? AND year=? ", new String[]{method, String.valueOf(month), String.valueOf(year)});
            } else {
                cursor = db.rawQuery("SELECT id,email,fullName,amount FROM users Join payments on userId = email Where method=? AND month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{method, String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Map<UserInfo, Integer> userAndAmount = cursorToUserAmount(cursor);
                if (userAndAmount != null) {
                    result.putAll(userAndAmount);
                }
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public int getMonthlySumReport(int month, int year, String groupName, String method, String CoachId) {
        int result = 0;
        Cursor cursor = null;
        try {
            if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups)) && method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments ON userId = email Where  month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ?))", new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(CoachId)});
            } else if (method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments ON userId = email Where year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            } else if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups))) {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments ON userId = email Where method=? AND month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? ))", new String[]{method, String.valueOf(month), String.valueOf(year), String.valueOf(CoachId)});
            } else {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments on userId = email Where method=? AND month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{method, String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                result = cursor.getInt(0);

                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public int getManagerMonthlySumReport(int month, int year, String groupName, String method, String CoachId) {
        int result = 0;
        Cursor cursor = null;
        try {
            if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups)) && method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments ON userId = email Where  month=? AND year=? ", new String[]{String.valueOf(month), String.valueOf(year)});
            } else if (method.equals(DBcontext.getResources().getString(R.string.allMethods))) {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments ON userId = email Where year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            } else if (groupName.equals(DBcontext.getResources().getString(R.string.allGroups))) {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments ON userId = email Where method=? AND month=? AND year=?", new String[]{method, String.valueOf(month), String.valueOf(year)});
            } else {
                cursor = db.rawQuery("SELECT sum(amount) FROM users Join payments on userId = email Where method=? AND month=? AND year=? AND userId IN (SELECT email From users WHERE groupId IN ( SELECT groupId FROM groups WHERE coachId= ? AND groupName=?))", new String[]{method, String.valueOf(month), String.valueOf(year), String.valueOf(CoachId), groupName});
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                result = cursor.getInt(0);

                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<Session> sessionsOverlap(Session newSession) {
        List<Session> result = new ArrayList<Session>();
        Cursor cursor = null;
        try {

            cursor = db.rawQuery("SELECT * FROM sessions WHERE (day=?) AND( (?*60+?+? between (hour*60+minute) and (hour*60+minute+duration))  OR ((?*60+?) between (hour*60+minute) and (hour*60+minute+duration)) )  ", new String[]{newSession.getDay(), String.valueOf(newSession.getHour()), String.valueOf(newSession.getMinute()), String.valueOf(newSession.getDuration()), String.valueOf(newSession.getHour()), String.valueOf(newSession.getMinute())});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Session sess = cursorToSession(cursor);
                result.add(sess);
                cursor.moveToNext();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // make sure to close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

}
