package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.R;

import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    CoachInfo newCoach;
    private NumberPicker ageNumberPicker;
    private EditText fullName, RegEmail, RegPassword, regConfirmPassword, regPhone, studio;
    Button registerBtn;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]

        // age Number Picker
        final String[] ageRange = {"Under 13", "13-18", "19-30", "30+"};
        ageNumberPicker = findViewById(R.id.coachAge);
        ageNumberPicker.setMaxValue(3);
        ageNumberPicker.setMinValue(0);
        ageNumberPicker.setDisplayedValues(ageRange);

        fullName = (EditText) findViewById(R.id.CoachName);
        RegEmail = findViewById(R.id.coachEmail);
        RegPassword = (EditText) findViewById(R.id.regPassword);
        regConfirmPassword = (EditText) findViewById(R.id.regConfirmPassword);
        registerBtn = findViewById(R.id.registerBtn);
        regPhone = findViewById(R.id.regPhone);
        studio = findViewById(R.id.regStudio);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String studioName = studio.getText().toString();
                studioManagerExist(studioName);

            }
        }); // end of button on click
    }

    private void studioManagerExist(String studioName) {
        DocumentReference docRef = fStore.collection("studios").document(studioName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.ManagerAlreadyExist), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Register();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();

                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    private void Register() {
        boolean checkError = true;


        final String Email = RegEmail.getText().toString().trim();
        final String Password = RegPassword.getText().toString();
        final String confirmPassword = regConfirmPassword.getText().toString();
        final String fullname = fullName.getText().toString();
        final String phone = regPhone.getText().toString();
        final String studioName = studio.getText().toString();
        String[] ageRange = {"Under 13", "13-18", "19-30", "30+"};
        final String age = ageRange[ageNumberPicker.getValue()];
        while (checkError) {
            boolean feError = false;
            if (TextUtils.isEmpty(fullname)) {
                fullName.setError("full name is Required.");
                feError = true;
            }

            if (TextUtils.isEmpty(Email)) {
                RegEmail.setError("Email is Required.");
                feError = true;

            }

            if (TextUtils.isEmpty(Password)) {
                RegPassword.setError("Password is Required.");
                feError = true;

            }
            if (TextUtils.isEmpty(phone)) {
                regPhone.setError("Password is Required.");
                feError = true;

            }
            if (Password.length() < 6) {
                RegPassword.setError("Password Must be >= 6 Characters");
                feError = true;

            }

            if (!Password.matches(confirmPassword)) {
                regConfirmPassword.setError("Password dont match!");
                feError = true;

            }

            if (feError)
                return;
            else
                checkError = false;
        }
        fAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();


                    //for user profile pic in realtime database
                    FirebaseUser firebaseUser = fAuth.getCurrentUser();
                    String userUiD = firebaseUser.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUiD);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", userUiD);
                    map.put("fullName", fullname);
                    map.put("type", getResources().getString(R.string.manager));
                    map.put("status", "offline");
                    map.put("search", fullname.toLowerCase());
                    map.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
                    reference.setValue(map);

                    userID = Email;
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    newCoach = new CoachInfo(fAuth.getCurrentUser().getUid(), fullname, age, phone, Email, Password, "manager", studioName);
                    Log.d("sighup", "new manager " + newCoach);
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", userUiD);
                    user.put("fullName", fullname);
                    user.put("age", age);
                    user.put("password", Password);
                    user.put("type", "manager");
                    user.put("phone", phone);
                    user.put("studio", studioName);

                    documentReference.set(newCoach).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Added to firestore ! ", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                            FitForLifeDataManager.getInstance().createCoach(newCoach);
                            FitForLifeDataManager.getInstance().setCurrentManager(newCoach);
                            FitForLifeDataManager.getInstance().setCurrentUser(null);
                            FitForLifeDataManager.getInstance().setCurrentCoach(null);
                            updateUI(fAuth.getCurrentUser());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                            Toast.makeText(RegisterActivity.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //  startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });  //end of creating user

    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
//            FitForLifeDataManager.getInstance().openDataBase(this);
//            FitForLifeDataManager.getInstance().createCoach(newCoach);
//            FitForLifeDataManager.getInstance().setCurrentCoach(newCoach);
            Intent intent = new Intent(this, HomeActivity.class);
            //  intent.putExtra("user_name", user.getEmail());
            startActivity(intent);
            finish();
        }
    }


//    @Override
//    public void onBackPressed()
//    {
//    }
}
