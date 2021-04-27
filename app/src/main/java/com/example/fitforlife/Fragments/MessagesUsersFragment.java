package com.example.fitforlife.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fitforlife.Adapters.UserMessageAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesUsersFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserMessageAdapter userAdapter;
    private List<User> mUsers;

    EditText search_users;

    boolean isCoach;
    boolean isManager;
    private CoachInfo currentCoach = null;
    private CoachInfo currentManager = null;
    private UserInfo currentUser = null;
    private List<UserInfo> usersToShow = new ArrayList<>();
    private List<CoachInfo> usersToShow2 = new ArrayList<>();
    private List<String> usersIDToShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
        currentManager = FitForLifeDataManager.getInstance().getCurrentManager();
        currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
        if (currentCoach != null) {
            isCoach = true;
            isManager = false;
        } else if (currentUser != null) {
            isCoach = false;
            isManager = false;
        } else if (currentManager != null) {
            isManager = true;
            isCoach = false;
        }

        readUsers();

        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;

                    if (!user.getId().equals(fuser.getUid()) && usersIDToShow.contains(user.getId())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new UserMessageAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {
                    mUsers.clear();
                    if (isCoach) {
                        usersToShow = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoach);
                        Log.d("messageTag", " manager : " + FitForLifeDataManager.getInstance().getUserManager());

                        if (FitForLifeDataManager.getInstance().getUserManager() != null) {
                            usersToShow2.add(FitForLifeDataManager.getInstance().getUserManager());
                        }
                    } else if (isManager) {
                        usersToShow = FitForLifeDataManager.getInstance().getAllManagerTrainee(currentManager);
                        usersToShow2 = FitForLifeDataManager.getInstance().getAllManagerCoaches(currentManager);
                    }
                    else {//user
                        if (FitForLifeDataManager.getInstance().getUserCoach() != null) {
                            usersToShow2.add(FitForLifeDataManager.getInstance().getUserCoach());
                        }
                        if (FitForLifeDataManager.getInstance().getUserManager() != null) {
                            usersToShow2.add(FitForLifeDataManager.getInstance().getUserManager());
                        }
                    }
                    usersIDToShow = new ArrayList<>();
                    for (UserInfo u : usersToShow) {
                        usersIDToShow.add(u.getId());
                        Log.d("messageTag", " user to show : " +u.getFullName()+"id: "+ u.getId());

                    }
                    if (!usersToShow2.isEmpty()) {
                        for (CoachInfo u : usersToShow2) {
                            usersIDToShow.add(u.getId());
                            Log.d("messageTag", " user to show 2 : " +u.getFullName()+"id: "+ u.getId());

                        }
                    }
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        Log.d("messageTag", " user : " +user.getFullName()+"id: "+ user.getId());

                        if (!user.getId().equals(firebaseUser.getUid()) && usersIDToShow.contains(user.getId())) {

                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserMessageAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
