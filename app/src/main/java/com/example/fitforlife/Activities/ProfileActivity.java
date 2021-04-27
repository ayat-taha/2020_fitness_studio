package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.os.Build;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.storage.StorageTask;
import com.example.fitforlife.Adapters.PageAdapter;
import com.example.fitforlife.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.fitforlife.Model.User;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private FirebaseAuth fAuth;
    boolean isCoach;
    boolean isManager;
    private CoachInfo currentCoach = null;
    private CoachInfo currentManager = null;
    private UserInfo currentUser = null;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab1, tab2, tab3;
    public PageAdapter pageAdapter;
    private ImageView profilePicture;
    private TextView editPicture;
    int TAKE_IMAGE_CODE = 10001;
    String currentUserEmail;
    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        currentUserEmail = fAuth.getCurrentUser().getEmail();
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


        // Bottom Navigation Bar - action Listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        if (isManager || isCoach)
            bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.calendar:
                        if (isManager) {
                            Intent calendarIntent = new Intent(ProfileActivity.this, ManagerCalendarAcitvity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent calendarIntent = new Intent(ProfileActivity.this, CalendarActivity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.progress:
                        Intent ProgressIntent;
                        if (isManager || isCoach) {
                            ProgressIntent = new Intent(ProfileActivity.this, TraineeProgressActivity.class);
                        } else {
                            ProgressIntent = new Intent(ProfileActivity.this, ProgressActivity.class);
                        }
                        startActivity(ProgressIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.home:
                        Intent homeIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent;
                        if (isManager || isCoach) {
                            paymentsIntent = new Intent(ProfileActivity.this, TraineePaymentsActivity.class);
                        } else {
                            paymentsIntent = new Intent(ProfileActivity.this, PaymentsActivity.class);
                        }
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (isCoach) {
                            Intent groupsIntent = new Intent(ProfileActivity.this, GroupsTraineeActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (isManager) {
                            Intent groupsIntent = new Intent(ProfileActivity.this, ManagerCoachTraineeGroupsActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent profileIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                }
                return false;
            }
        });
        // END NAVIGATION BAR

        // tabs
        tabLayout = (TabLayout) findViewById(R.id.profileTabs);
        tab1 = (TabItem) findViewById(R.id.infoTab);
        tab2 = (TabItem) findViewById(R.id.GoalTab);
        if (isCoach || isManager) {
//            ((ViewGroup) tabLayout.getChildAt(1)).getChildAt(1).setVisibility(View.GONE);
            ((LinearLayout) tabLayout.getTabAt(1).view).setVisibility(View.GONE);


        }

        tab3 = (TabItem) findViewById(R.id.notificationsTab);
        viewPager = findViewById(R.id.viewpager);
        profilePicture = findViewById(R.id.profilePicture);
        editPicture = findViewById(R.id.editPicture);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profilePicture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0)
                    pageAdapter.notifyDataSetChanged();
                else if (tab.getPosition() == 1)
                    pageAdapter.notifyDataSetChanged();
                else if (tab.getPosition() == 2)
                    pageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });

    }


    /**
     * logout method
     *
     * @param view - logout icon
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

    /**
     * open more activtiy
     *
     * @param view -more image
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(ProfileActivity.this, MoreActivity.class);
        startActivity(MoreIntent);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (mImageUri != null) {
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("imageUrl", "" + miUrlOk);
                        reference.updateChildren(map1);

                        pd.dismiss();

                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
