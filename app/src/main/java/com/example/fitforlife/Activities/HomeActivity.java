package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.fitforlife.BackgroundSoundService;
import com.example.fitforlife.Fragments.Home_Fragment;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.Payment;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.R;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    boolean isCoach;
    boolean isManager;
    private CoachInfo currentCoach = null;
    private UserInfo currentUser = null;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //   check if current user is coach or trainee
        final FirebaseUser user = fAuth.getCurrentUser();

        DocumentReference docRef = fStore.collection("users").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Add &&& and studio not null !
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        // current signed in is *******MANAGER********
                        if (document.getData().get("type").toString().equals("manager")) {
                            currentCoach = new CoachInfo(user.getUid(), document.getData().get("fullName").toString(),
                                    document.getData().get("age").toString(), document.getData().get("phone").toString(), user.getEmail()
                                    , document.getData().get("password").toString(), "manager", document.getData().get("studio").toString());
                            FitForLifeDataManager.getInstance().setCurrentCoach(null);
                            FitForLifeDataManager.getInstance().setCurrentUser(null);
                            FitForLifeDataManager.getInstance().setCurrentManager(currentCoach);
                            isCoach = false;
                            isManager = true;

                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_add);
                            bottomNavigationView.getMenu().getItem(2).setTitle(R.string.add_post);
                            bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);
                            getManagerData();


                        }


                        // current signed in is *******COACH********
                        if (document.getData().get("type").toString().equals("coach")) {
                            currentCoach = new CoachInfo(user.getUid(), document.getData().get("fullName").toString(),
                                    document.getData().get("age").toString(), document.getData().get("phone").toString(), user.getEmail()
                                    , document.getData().get("password").toString(), "coach", document.getData().get("studio").toString());
                            FitForLifeDataManager.getInstance().setCurrentCoach(currentCoach);
                            FitForLifeDataManager.getInstance().setCurrentUser(null);
                            FitForLifeDataManager.getInstance().setCurrentManager(null);
                            isCoach = true;
                            isManager = false;
                            getCoachGroups(currentCoach);
                            getCoachTrainee(currentCoach);
                            getUserManager(currentCoach);
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_add);
                            bottomNavigationView.getMenu().getItem(2).setTitle(R.string.add_post);
                            bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);

                        }


                        // current signed in is *******User********
                        if (document.getData().get("type").toString().equals("user")) {
                            isCoach = false;
                            isManager = false;
                            FitForLifeDataManager.getInstance().setCurrentCoach(null);
                            FitForLifeDataManager.getInstance().setCurrentManager(null);
                            currentUser = new UserInfo(user.getUid(), document.getData().get("fullName").toString(),
                                    document.getData().get("age").toString(), document.getData().get("phone").toString(), user.getEmail(),
                                    document.getData().get("password").toString(), "user", document.getData().get("studio").toString());
                            getUserProgress(currentUser);
                            getUserPayments(currentUser);
                            getUserNotGoing(currentUser);
                            getUserManager(currentUser);
                            if (document.get("dateAdded") != null)
                                currentUser.setDateAdded(((Long) document.get("dateAdded")));

                            if (document.getData().get("groupId") != null) {
                                currentUser.setGroupId(document.getData().get("groupId").toString());
                                getUserGroup(currentUser);//&coach
                                if (document.getData().get("weeklyGoal") != null && document.getData().get("weightGoal") != null) {
                                    currentUser.setWeightGoal(document.getDouble("weightGoal"));
                                    currentUser.setWeeklyGoal(document.getData().get("weeklyGoal").toString());
                                    FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                                } else {
                                    FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                                }
                            } else {
                                if (document.contains("weeklyGoal") && document.contains("weightGoal")) {
                                    currentUser.setWeightGoal(document.getDouble("weightGoal"));
                                    currentUser.setWeeklyGoal(document.getData().get("weeklyGoal").toString());
                                    FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                                } else {
                                    FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                                }
                            }

                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

//        if (!isManager && !isCoach)
//            getDataFromFirebase();


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Home_Fragment()).commit();

        // Bottom Navigation Bar - action Listener
        bottomNavigationView.setSelectedItemId(R.id.home);
        setNav();

    }


    /**
     * when Current user is the MANAGER get all their studio trainee+
     * groups+coaches..
     */
    private void getManagerData() {
        // ***** Add coaches ********

        fStore.collection("users")
                .whereEqualTo("studio", currentCoach.getStudio())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.get("type").equals("coach")) {
                                    CoachInfo newCoach = new CoachInfo(document.getData().get("id").toString(), document.getData().get("fullName").toString(),
                                            document.getData().get("age").toString(), document.getData().get("phone").toString(), document.getData().get("email").toString()
                                            , document.getData().get("password").toString(), "coach", document.getData().get("studio").toString());

                                    FitForLifeDataManager.getInstance().createCoach(newCoach);
                                    getCoachGroups(newCoach); //GET THIS COACH GROUPS SESSION + RES + CANCELED
                                    getCoachTrainee(newCoach);  //GET THIS COACH TRAINEE
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // end add manager coaches


    }


    /**
     * called when manger/coach is logged in get all groups and its sessions
     *
     * @param newCoach
     */
    private void getCoachGroups(CoachInfo newCoach) {
        fStore.collection("groups")
                .whereEqualTo("coachId", newCoach.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                final Group group = new Group(document.getId(), document.getData().get("groupName").toString(), document.getData().get("NumberOfTrainee").toString()
                                        , document.getData().get("coachId").toString());
                                FitForLifeDataManager.getInstance().createGroup(group);
                                getCanceledAndRes(group);
                                //get group session
                                fStore.collection("groups").document(group.getGroupId()).collection("sessions")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, "tajrbee " + document.getId() + " => " + document.getData());
                                                        Long hour = (Long) document.get("hour");
                                                        int y1 = hour.intValue();
                                                        Long min = ((Long) document.get("minute"));
                                                        int y2 = min.intValue();
                                                        Long duration = (Long) document.get("duration");
                                                        int y3 = duration.intValue();
                                                        Session newSession = new Session(group.getGroupId(), document.getId(), document.getData().get("day").toString(), y1
                                                                , y2, y3);
                                                        FitForLifeDataManager.getInstance().createSession(newSession);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                                //end GETTING group sessions
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }); // end get groups


    }

    /**
     * when current user is coach , got their trainee and
     * their oayments & progress
     *
     * @param currentCoach - current logged coach
     */
    private void getCoachTrainee(final CoachInfo currentCoach) {

        List<UserInfo> coachTrainee = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoach);
        if (coachTrainee.isEmpty()) {
            final List<Group> groups = new ArrayList<>();

            fStore.collection("groups")
                    .whereEqualTo("coachId", currentCoach.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                    Group group = new Group(document.getId(), document.getData().get("groupName").toString(), document.getData().get("NumberOfTrainee").toString()
                                            , document.getData().get("coachId").toString());
                                    groups.add(group);
                                    Log.d(TAG, "id :" + document.getId() + " => " + groups);
                                }

                                for (Group tmpGroups : groups) {
                                    fStore.collection("users")
                                            .whereEqualTo("groupId", tmpGroups.getGroupId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            UserInfo currentUser = new UserInfo(document.getData().get("id").toString(), document.getData().get("fullName").toString(),
                                                                    document.getData().get("age").toString(), document.getData().get("phone").toString(), document.getId()
                                                                    , document.getData().get("password").toString(), "user", document.getData().get("studio").toString());
                                                            if (document.get("dateAdded") != null)
                                                                currentUser.setDateAdded(((Long) document.get("dateAdded")));

                                                            if (document.getData().get("groupId") != null) {
                                                                currentUser.setGroupId(document.getData().get("groupId").toString());
                                                                if (document.getData().get("weeklyGoal") != null && document.getData().get("weightGoal") != null) {
                                                                    currentUser.setWeightGoal(document.getDouble("weightGoal"));
                                                                    currentUser.setWeeklyGoal(document.getData().get("weeklyGoal").toString());
                                                                }
                                                            } else {
                                                                if (document.contains("weeklyGoal") && document.contains("weightGoal")) {
                                                                    currentUser.setWeightGoal(document.getDouble("weightGoal"));
                                                                    currentUser.setWeeklyGoal(document.getData().get("weeklyGoal").toString());
                                                                }
                                                            }
                                                            FitForLifeDataManager.getInstance().createUser(currentUser);
                                                            // get payments
                                                            fStore.collection("users").document(currentUser.getEmail()).collection("payments")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                    Log.d(TAG, "payment from data base " + document.getId() + " => " + document.getData());
                                                                                    Long Lnumber = (Long) document.get("number");
                                                                                    int number = Lnumber.intValue();
                                                                                    Long Lmonth = (Long) document.get("month");
                                                                                    int month = Lmonth.intValue();
                                                                                    Long Lyear = ((Long) document.get("year"));
                                                                                    int year = Lyear.intValue();
                                                                                    Long Lamount = (Long) document.get("amount");
                                                                                    int amount = Lamount.intValue();
                                                                                    String method = (String) document.get("method");
                                                                                    String userId = (String) document.get("userId");
                                                                                    Payment newPayment = new Payment(userId, number, month, year, amount, method);
                                                                                    FitForLifeDataManager.getInstance().createPayment(newPayment);
                                                                                    Log.d(TAG, "added user payments: ");
                                                                                }
                                                                            } else {
                                                                                Log.d(TAG, "Error getting payments: ", task.getException());
                                                                            }
                                                                        }
                                                                    });
                                                            //end payments
                                                            // get progress
                                                            fStore.collection("users").document(currentUser.getEmail()).collection("progress")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                    Measurement newMeasurement = new Measurement(document.getId(), document.get("traineeId").toString(),
                                                                                            document.getLong("date"), document.getDouble("weight"),
                                                                                            document.getDouble("waist"), document.getDouble("chest"), document.getDouble("buttocks")
                                                                                            , document.getDouble("rightArm"), document.getDouble("leftArm"),
                                                                                            document.getDouble("rightThigh"), document.getDouble("leftThigh"));
                                                                                    FitForLifeDataManager.getInstance().createMeasurement(newMeasurement);
                                                                                    Log.d(TAG, "Mcreated " + newMeasurement);
                                                                                }
                                                                            } else {
                                                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                                                            }
                                                                        }
                                                                    });
                                                            //end progress
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting documents Coach Trainee: ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "Error getting documents Coach Groups: ", task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * get all  canceled and ress for this group
     * called in getCoachGroups when current user is manager
     *
     * @param group
     */
    private void getCanceledAndRes(Group group) {

//get all canceled
        fStore.collection("canceledSessions")
                .whereEqualTo("groupId", group.getGroupId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Session newSession = new Session(document.getData().get("groupId").toString()
                                        , document.getData().get("sessionNumber").toString(),
                                        document.getData().get("day").toString(), Integer.valueOf(document.get("hour").toString())
                                        , Integer.valueOf(document.get("minute").toString()), Integer.valueOf(document.get("duration").toString()));

                                Long color = (Long) document.get("color");
                                int co = color.intValue();
                                Event event = new Event(co, (Long) document.get("timeInMillis"), newSession);
                                FitForLifeDataManager.getInstance().createCanceledSession(event, document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }); // end get canceled


        // get all res
        if (FitForLifeDataManager.getInstance().getAllRescheduledSessions().isEmpty()) {
            fStore.collection("rescheduledSessions")
                    .whereEqualTo("groupId", group.getGroupId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Session newSession = new Session(document.getData().get("groupId").toString()
                                            , document.getData().get("sessionNumber").toString(),
                                            document.getData().get("day").toString(), Integer.valueOf(document.get("hour").toString())
                                            , Integer.valueOf(document.get("minute").toString()), Integer.valueOf(document.get("duration").toString()));

                                    Long color = (Long) document.get("color");
                                    int co = color.intValue();
                                    Event event = new Event(co, (Long) document.get("timeInMillis"), newSession);
                                    FitForLifeDataManager.getInstance().createRescheduledSession(event, document.getId());

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }  //end get res

    }

    /**
     * when current user is TRainee , got their progress and add into sqlite
     *
     * @param currentUser - trainee
     */
    private void getUserProgress(UserInfo currentUser) {
        List<Measurement> measurements = FitForLifeDataManager.getInstance().getTraineeMeasurement(currentUser.getEmail());
        if (measurements.isEmpty()) {
            // get payments
            fStore.collection("users").document(currentUser.getEmail()).collection("progress")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Measurement newMeasurement = new Measurement(document.getId(), document.get("traineeId").toString(),
                                            document.getLong("date"), document.getDouble("weight"),
                                            document.getDouble("waist"), document.getDouble("chest"), document.getDouble("buttocks")
                                            , document.getDouble("rightArm"), document.getDouble("leftArm"),
                                            document.getDouble("rightThigh"), document.getDouble("leftThigh"));
                                    FitForLifeDataManager.getInstance().createMeasurement(newMeasurement);
                                    Log.d(TAG, "Mcreated " + newMeasurement);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            //end payments
        }
    }

    private void getUserGroup(UserInfo currentUser) {
        Log.d("karla", "getgroup : " + FitForLifeDataManager.getInstance().getGroup(currentUser.getGroupId()));
        DocumentReference docRef = fStore.collection("groups").document(currentUser.getGroupId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Group group = new Group(document.getId(), document.getData().get("groupName").toString(), document.getData().get("NumberOfTrainee").toString()
                                , document.getData().get("coachId").toString());
                        FitForLifeDataManager.getInstance().createGroup(group);
                        getGroupSessions(group);
                        getCanceledAndRes(group);
                        getUserCoach(group);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getGroupSessions(final Group group) {
        List<Session> measurements = FitForLifeDataManager.getInstance().getAllGroupSessions(group);
        if (measurements.isEmpty()) {
            // get payments
            fStore.collection("groups").document(group.getGroupId()).collection("sessions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "tajrbee " + document.getId() + " => " + document.getData());
                                    Long hour = (Long) document.get("hour");
                                    int y1 = hour.intValue();
                                    Long min = ((Long) document.get("minute"));
                                    int y2 = min.intValue();
                                    Long duration = (Long) document.get("duration");
                                    int y3 = duration.intValue();
                                    Session newSession = new Session(group.getGroupId(), document.getId(), document.getData().get("day").toString(), y1
                                            , y2, y3);
                                    FitForLifeDataManager.getInstance().createSession(newSession);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            //end payments
        }
    }
    private void getUserCoach(Group group) {
        Log.d("karla", "getgroup : " + FitForLifeDataManager.getInstance().getGroup(currentUser.getGroupId()));
        DocumentReference docRef = fStore.collection("users").document(group.getCoachId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        CoachInfo userCoach = new CoachInfo(document.getData().get("id").toString(), document.getData().get("fullName").toString(),
                                document.getData().get("age").toString(), document.getData().get("phone").toString(), document.getData().get("email").toString()
                                , document.getData().get("password").toString(), "coach", document.getData().get("studio").toString());
                        FitForLifeDataManager.getInstance().setUserCoach(userCoach);
                        FitForLifeDataManager.getInstance().createCoach(userCoach);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getUserManager(CoachInfo currentUser) {

        fStore.collection("users")
                .whereEqualTo("studio", currentUser.getStudio())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.get("type").equals("manager")) {
                                    CoachInfo userManager = new CoachInfo(document.getData().get("id").toString() ,document.getData().get("fullName").toString(),
                                            document.getData().get("age").toString(), document.getData().get("phone").toString(), document.getData().get("email").toString()
                                            , document.getData().get("password").toString(), "manager", document.getData().get("studio").toString());
                                    FitForLifeDataManager.getInstance().setUserManager(userManager);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getUserNotGoing(UserInfo currentUser) {
        getDataFromFirebase();
        if (FitForLifeDataManager.getInstance().getAllNotGoingForUser(currentUser.getEmail()).isEmpty()) {
            fStore.collection("notGoing")
                    .whereEqualTo("user1", currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    Session newSession = new Session(document.getData().get("groupId").toString()
                                            , document.getData().get("sessionNumber").toString(),
                                            document.getData().get("day").toString(), Integer.valueOf(document.get("hour").toString())
                                            , Integer.valueOf(document.get("minute").toString()), Integer.valueOf(document.get("duration").toString()));

                                    Long color = (Long) document.get("color");
                                    int co = color.intValue();
                                    Event event = new Event(co, (Long) document.get("timeInMillis"), newSession);
                                    if (document.get("user2") != null)
                                        FitForLifeDataManager.getInstance().createNotGoingSession(event, document.getId(), document.getData().get("user1").toString(), document.getData().get("user2").toString());
                                    else
                                        FitForLifeDataManager.getInstance().createNotGoingSession(event, document.getId(), document.getData().get("user1").toString());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
    }


    private void getDataFromFirebase() {

        //AllGroups an its sessions  from firebase to SqlLite;
//        if (FitForLifeDataManager.getInstance().getAllGroups().isEmpty()) {
//            fStore.collection("groups")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    final Group group = new Group(document.getId(), document.getData().get("groupName").toString(), document.getData().get("NumberOfTrainee").toString()
//                                            , document.getData().get("coachId").toString());
//                                    FitForLifeDataManager.getInstance().createGroup(group);
//                                    fStore.collection("groups").document(group.getGroupId()).collection("sessions")
//                                            .get()
//                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                    if (task.isSuccessful()) {
//                                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                                            Log.d(TAG, "tajrbee " + document.getId() + " => " + document.getData());
//                                                            Long hour = (Long) document.get("hour");
//                                                            int y1 = hour.intValue();
//                                                            Long min = ((Long) document.get("minute"));
//                                                            int y2 = min.intValue();
//                                                            Long duration = (Long) document.get("duration");
//                                                            int y3 = duration.intValue();
//                                                            Session newSession = new Session(group.getGroupId(), document.getId(), document.getData().get("day").toString(), y1
//                                                                    , y2, y3);
//                                                            FitForLifeDataManager.getInstance().createSession(newSession);
//                                                        }
//                                                    } else {
//                                                        Log.d(TAG, "Error getting documents: ", task.getException());
//                                                    }
//                                                }
//                                            });
//                                }
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//        }


        //GET CANCELED SESSION
//        if (FitForLifeDataManager.getInstance().getAllCanceledSessions().isEmpty()) {
//            fStore.collection("canceledSessions")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                    Session newSession = new Session(document.getData().get("groupId").toString()
//                                            , document.getData().get("sessionNumber").toString(),
//                                            document.getData().get("day").toString(), Integer.valueOf(document.get("hour").toString())
//                                            , Integer.valueOf(document.get("minute").toString()), Integer.valueOf(document.get("duration").toString()));
//
//                                    Long color = (Long) document.get("color");
//                                    int co = color.intValue();
//                                    Event event = new Event(co, (Long) document.get("timeInMillis"), newSession);
//                                    FitForLifeDataManager.getInstance().createCanceledSession(event, document.getId());
//                                }
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//        }
        //GET res SESSION
//        if (FitForLifeDataManager.getInstance().getAllRescheduledSessions().isEmpty()) {
//            fStore.collection("rescheduledSessions")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                    Session newSession = new Session(document.getData().get("groupId").toString()
//                                            , document.getData().get("sessionNumber").toString(),
//                                            document.getData().get("day").toString(), Integer.valueOf(document.get("hour").toString())
//                                            , Integer.valueOf(document.get("minute").toString()), Integer.valueOf(document.get("duration").toString()));
//
//                                    Long color = (Long) document.get("color");
//                                    int co = color.intValue();
//                                    Event event = new Event(co, (Long) document.get("timeInMillis"), newSession);
//                                    FitForLifeDataManager.getInstance().createRescheduledSession(event, document.getId());
//                                }
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//        }
        //GET res SESSION
            fStore.collection("notGoing")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Session newSession = new Session(document.getData().get("groupId").toString()
                                            , document.getData().get("sessionNumber").toString(),
                                            document.getData().get("day").toString(), Integer.valueOf(document.get("hour").toString())
                                            , Integer.valueOf(document.get("minute").toString()), Integer.valueOf(document.get("duration").toString()));

                                    Long color = (Long) document.get("color");
                                    int co = color.intValue();
                                    Event event = new Event(co, (Long) document.get("timeInMillis"), newSession);
                                    if (document.get("user2") != null)
                                        FitForLifeDataManager.getInstance().createNotGoingSession(event, document.getId(), document.getData().get("user1").toString(), document.getData().get("user2").toString());
                                    else
                                        FitForLifeDataManager.getInstance().createNotGoingSession(event, document.getId(), document.getData().get("user1").toString());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
    }

    private void getUserPayments(UserInfo currentUser) {
        if (!isCoach && this.currentUser != null) {
            if (FitForLifeDataManager.getInstance().getAllUserPayments(this.currentUser).isEmpty()) {
                fStore.collection("users").document(currentUser.getEmail()).collection("payments")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("hi", "tajrbee " + document.getId() + " => " + document.getData());
                                        Long Lnumber = (Long) document.get("number");
                                        int number = Lnumber.intValue();
                                        Long Lmonth = (Long) document.get("month");
                                        int month = Lmonth.intValue();
                                        Long Lyear = ((Long) document.get("year"));
                                        int year = Lyear.intValue();
                                        Long Lamount = (Long) document.get("amount");
                                        int amount = Lamount.intValue();
                                        String method = (String) document.get("method");
                                        Payment newPayment = new Payment(fAuth.getCurrentUser().getEmail(), number, month, year, amount, method);
                                        HomeActivity.this.currentUser.getUserPayments().add(newPayment);
                                        FitForLifeDataManager.getInstance().createPayment(newPayment);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }
    }


    private void setNav() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        if (isManager) {
                            Intent calendarIntent = new Intent(HomeActivity.this, ManagerCalendarAcitvity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent calendarIntent = new Intent(HomeActivity.this, CalendarActivity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.progress:
                        if (isCoach || isManager) {
                            Intent Progress = new Intent(HomeActivity.this, TraineeProgressActivity.class);
                            startActivity(Progress);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent ProgressIntent = new Intent(HomeActivity.this, ProgressActivity.class);
                            startActivity(ProgressIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.home:
                        if (isCoach || isManager) {
                            Intent postIntent = new Intent(HomeActivity.this, PostActivity.class);
                            startActivity(postIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.payments:
                        Intent paymentsIntent;
                        if (isCoach || isManager) {
                            paymentsIntent = new Intent(HomeActivity.this, TraineePaymentsActivity.class);
                        } else {
                            paymentsIntent = new Intent(HomeActivity.this, PaymentsActivity.class);
                        }
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (isManager) {
                            Intent groupsIntent = new Intent(HomeActivity.this, ManagerCoachTraineeGroupsActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                        if (isCoach) {
                            Intent groupsIntent = new Intent(HomeActivity.this, GroupsTraineeActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                }
                return false;
            }
        });
        // END NAVIGATION BAR
    }

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(HomeActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }


    public void PlayBackgroundSound(View view) {

        Intent intent = new Intent(this, BackgroundSoundService.class);
        startService(intent);
    }

    private void changeMenuItemCheckedStateColor(BottomNavigationView bottomNavigationView, String checkedColorHex, String uncheckedColorHex) {
        int checkedColor = Color.parseColor(checkedColorHex);
        int uncheckedColor = Color.parseColor(uncheckedColorHex);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}, // checked

        };

        int[] colors = new int[]{
                uncheckedColor,
                checkedColor
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        bottomNavigationView.setItemTextColor(colorStateList);
        bottomNavigationView.setItemIconTintList(colorStateList);
    }
}


