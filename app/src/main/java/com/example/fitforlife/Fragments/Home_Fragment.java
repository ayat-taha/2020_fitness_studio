package com.example.fitforlife.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.fitforlife.Adapters.SquaresAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.fitforlife.Adapters.PostAdapter;
//import com.example.fitforlife..Adapter.StoryAdapter;
import com.example.fitforlife.Model.Post;
//import com.example.fitforlife.Model.Story;
import com.example.fitforlife.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Home_Fragment extends Fragment {
    private static final String TAG = "Home_Fragment";

    private RecyclerView recyclerView;
    private RecyclerView recyclerView_Squares;
    private SquaresAdapter squareAdapter;
    private ImageView squaresIc;
    private ImageView linesIc;
    private PostAdapter postAdapter;
    private List<Post> postList;
    String currentStudio;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String currentUser;

    //       private RecyclerView recyclerView_story;
//        private StoryAdapter storyAdapter;
//        private List<Story> storyList;

    private List<String> followingList;

    ProgressBar progress_circular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView_Squares = view.findViewById(R.id.recycler_view_squares);
        recyclerView_Squares.setHasFixedSize(true);
        LinearLayoutManager gLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView_Squares.setLayoutManager(gLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        squareAdapter = new SquaresAdapter(getContext(), postList);
        recyclerView_Squares.setAdapter(squareAdapter);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_Squares.setVisibility(View.GONE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = fAuth.getCurrentUser().getEmail();

        DocumentReference docRef = fStore.collection("users").document(currentUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        currentStudio = document.getData().get("studio").toString();
                        Log.d("posthome1", "p: " + currentStudio );
                        readPosts();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


//        DocumentReference docRef = fStore.collection("users").document(groupCoach);
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                currentStudio = documentSnapshot.getData().get("studio").toString();
//                Log.d("posthome1", "p: " + currentStudio );
//                readPosts();
//
//            }
//        });
//        Log.d("posthome2", "p: " + currentStudio );

//            recyclerView_story = view.findViewById(R.id.recycler_view_story);
//            recyclerView_story.setHasFixedSize(true);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
//                    LinearLayoutManager.HORIZONTAL, false);
//            recyclerView_story.setLayoutManager(linearLayoutManager);
//            storyList = new ArrayList<>();
//            storyAdapter = new StoryAdapter(getContext(), storyList);
//            recyclerView_story.setAdapter(storyAdapter);

        progress_circular = view.findViewById(R.id.progress_circular);
        squaresIc = view.findViewById(R.id.squareIc);
        linesIc = view.findViewById(R.id.lineIc);


        // checkFollowing();
       // readPosts();
        squaresIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView_Squares.setAdapter(squareAdapter);
                recyclerView.setVisibility(View.GONE);
                recyclerView_Squares.setVisibility(View.VISIBLE);
            }
        });

        linesIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView_Squares.setAdapter(postAdapter);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_Squares.setVisibility(View.GONE);
            }
        });

        return view;
    }

//        private void checkFollowing(){
//            followingList = new ArrayList<>();
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .child("following");
//
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    followingList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        followingList.add(snapshot.getKey());
//                    }
//
//                    readPosts();
//                    //readStory();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Log.d("posthome3", "p: " + currentStudio );

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    Log.d("posthome", "p: " + post.getStudio() + "p2:" + currentStudio);

                    if (post.getStudio().equals(currentStudio)) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//        private void readStory(){
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    long timecurrent = System.currentTimeMillis();
//                    storyList.clear();
//                    storyList.add(new Story("", 0, 0, "",
//                            FirebaseAuth.getInstance().getCurrentCoach().getUid()));
//                    for (String id : followingList) {
//                        int countStory = 0;
//                        Story story = null;
//                        for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
//                            story = snapshot.getValue(Story.class);
//                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
//                                countStory++;
//                            }
//                        }
//                        if (countStory > 0){
//                            storyList.add(story);
//                        }
//                    }
//
//                    storyAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
}