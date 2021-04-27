package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    TextView language_dialog, text1;
    boolean lang_selected;
    Context context;
    Resources resources;
    EditText userEmail, userPassword;
    Button loginBut;
    Button btn;
    TextView forgotPass;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FitForLifeDataManager.getInstance().openDataBase(this);
        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]


        // [END initialize_auth]
        userEmail = findViewById(R.id.editText_username);
        userPassword = findViewById(R.id.editText_password);
        forgotPass = findViewById(R.id.textview_forgotpass);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final View PopUpView = getLayoutInflater().inflate(R.layout.reset_password_popup, null);
                PopUpView.setClipToOutline(true);
                TextView x = PopUpView.findViewById(R.id.txtclose);
                final TextView text = PopUpView.findViewById(R.id.text);
                text.setVisibility(View.GONE);
                Button submit = PopUpView.findViewById(R.id.submit);
                final EditText emailToReset = PopUpView.findViewById(R.id.emailForReset);
                dialogBuilder.setView(PopUpView);
                dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //to round popup corners
                dialog.show();
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String emailAddress = emailToReset.getText().toString();
                        if (!TextUtils.isEmpty(emailAddress)) {
                            auth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                text.setVisibility(View.VISIBLE);
                                            } else {
                                                text.setVisibility(View.VISIBLE);
                                                text.setText(getResources().getString(R.string.failedSendingEmail));
                                            }
                                        }
                                    });
                        } else {
                            text.setVisibility(View.VISIBLE);
                            text.setText(getResources().getString(R.string.EnterEmail));
                        }

                    }
                });

            }
        });
    }


    public void OnClickLogin(View view) {
        String email = userEmail.getText().toString();
        final String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Password is Required.");
            return;
        }

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    final FirebaseUser user = fAuth.getCurrentUser();

                    updateUI(user);
                    DocumentReference referenceToUpdatePAss = fStore.collection("users").document(userEmail.getText().toString());

// Set the "isCapital" field of the city 'DC'
                    referenceToUpdatePAss
                            .update("password", password)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });

                    DocumentReference docRef = fStore.collection("users").document(user.getEmail());
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // user is coach
                            if (documentSnapshot.getData().get("type").toString().equals("coach")) {

                                CoachInfo currentCoach = new CoachInfo(user.getUid(), documentSnapshot.getData().get("fullName").toString(),
                                        documentSnapshot.getData().get("age").toString(), documentSnapshot.getData().get("phone").toString(),user.getEmail()
                                        , password, "coach", documentSnapshot.getData().get("studio").toString());
                                FitForLifeDataManager.getInstance().createCoach(currentCoach);
                                FitForLifeDataManager.getInstance().updateCoach(currentCoach);
                                FitForLifeDataManager.getInstance().setCurrentCoach(currentCoach);
                                FitForLifeDataManager.getInstance().setCurrentUser(null);
                                FitForLifeDataManager.getInstance().setCurrentManager(null);
                            }

                            // current user
                            if (documentSnapshot.getData().get("type").toString().equals("user")) {
                                FitForLifeDataManager.getInstance().setCurrentCoach(null);
                                FitForLifeDataManager.getInstance().setCurrentManager(null);

                                UserInfo currentUser = new UserInfo(user.getUid(), documentSnapshot.getData().get("fullName").toString(),
                                        documentSnapshot.getData().get("age").toString(), documentSnapshot.getData().get("phone").toString(),user.getEmail()
                                        , password, "user", documentSnapshot.getData().get("studio").toString());

                                if (documentSnapshot.getData().get("groupId") != null) {
                                    currentUser.setGroupId(documentSnapshot.getData().get("groupId").toString());
                                    if (documentSnapshot.getData().get("weeklyGoal") != null && documentSnapshot.getData().get("weightGoal") != null) {
                                        currentUser.setWeightGoal(documentSnapshot.getDouble("weightGoal"));
                                        currentUser.setWeeklyGoal(documentSnapshot.getData().get("weeklyGoal").toString());
                                    }
                                    FitForLifeDataManager.getInstance().createUser(currentUser);
                                    FitForLifeDataManager.getInstance().setCurrentUser(currentUser);

                                } else {
                                    if (documentSnapshot.contains("weeklyGoal") && documentSnapshot.contains("weightGoal")) {
                                        currentUser.setWeightGoal(documentSnapshot.getDouble("weightGoal"));
                                        currentUser.setWeeklyGoal(documentSnapshot.getData().get("weeklyGoal").toString());
                                    }
                                    FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                                    FitForLifeDataManager.getInstance().createUser(currentUser);
                                }
                                FitForLifeDataManager.getInstance().updateUser(currentUser);
                            }
                            if (documentSnapshot.getData().get("type").toString().equals("manager")) {
                                CoachInfo currentCoach = new CoachInfo(user.getUid(), documentSnapshot.getData().get("fullName").toString(),
                                        documentSnapshot.getData().get("age").toString(), documentSnapshot.getData().get("phone").toString(), user.getEmail()
                                        , documentSnapshot.getData().get("password").toString(), "manager", documentSnapshot.getData().get("studio").toString());
                                FitForLifeDataManager.getInstance().setCurrentManager(currentCoach);
                                FitForLifeDataManager.getInstance().setCurrentCoach(null);
                                FitForLifeDataManager.getInstance().setCurrentUser(null);
                                FitForLifeDataManager.getInstance().createCoach(currentCoach);
                                FitForLifeDataManager.getInstance().updateCoach(currentCoach);
                            }
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Authentication f a i l e d . ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void OnClickSignUp(View view) {
        Intent HomeIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(HomeIntent);


    }

    @Override
    protected void onResume() {
        FitForLifeDataManager.getInstance().openDataBase(this);
        super.onResume();

    }

    @Override
    protected void onPause() {
//        FitForLifeDataManager.getInstance().closeDataBase();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            FitForLifeDataManager.getInstance().openDataBase(this);
            Intent intent = new Intent(this, HomeActivity.class);
            //  intent.putExtra("user_name", user.getEmail());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        //  intent.putExtra("user_name", user.getEmail());
        startActivity(intent);
        finish();
    }
}
