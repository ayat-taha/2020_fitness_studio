package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anychart.charts.Resource;
import com.example.fitforlife.Adapters.GroupAdapter;
import com.example.fitforlife.Fragments.Groups_Fragment;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    boolean isCoach;
    String studioName;
    EditText text, phone, facebook, instagram, waze;
    Button save;
    ImageView call, wazeIcon, wazeInfoImage;
    Context context;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        context = this;
        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        save = findViewById(R.id.saveAbout);
        text = findViewById(R.id.about_EditText);
        phone = findViewById(R.id.phoneNumber);
        facebook = findViewById(R.id.facebookLink);
        instagram = findViewById(R.id.instagramLink);
        waze = findViewById(R.id.wazeLink);
        wazeIcon = findViewById(R.id.wazeImage);
        wazeInfoImage = findViewById(R.id.wazeInfoImage);
        call = findViewById(R.id.callIcon);

        // cureent is ***** MANAGER *********
        if (FitForLifeDataManager.getInstance().getCurrentManager() != null && FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentUser() == null) {
            studioName = FitForLifeDataManager.getInstance().getCurrentManager().getStudio();
            fillAbout();
            save.setOnClickListener(SaveOnClick);
            call.setVisibility(View.GONE);
            wazeInfoImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
                    LayoutInflater factory = LayoutInflater.from(context);
                    View view = factory.inflate(R.layout.waze_dialog_layout, null);
                    alertadd.setView(view);
                    alertadd.setPositiveButton(context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alertadd.show();
                }
            });

        }
        // cureent is ***** COACH *********
        if (FitForLifeDataManager.getInstance().getCurrentManager() == null && FitForLifeDataManager.getInstance().getCurrentCoach() != null && FitForLifeDataManager.getInstance().getCurrentUser() == null) {
            text.setEnabled(false);
            phone.setEnabled(false);
            facebook.setEnabled(false);
            instagram.setEnabled(false);
            waze.setVisibility(View.GONE);
            wazeIcon.setVisibility(View.GONE);
            wazeInfoImage.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: hu????un???");

            save.setVisibility(View.GONE);
            studioName = FitForLifeDataManager.getInstance().getCurrentCoach().getStudio();
            fillAbout();
            call.setVisibility(View.GONE);

        }
        // cureent is ***** TRAINEE *********

        if (FitForLifeDataManager.getInstance().getCurrentManager() == null && FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentUser() != null) {

            text.setEnabled(false);
            phone.setEnabled(false);
            phone.setClickable(true);
            save.setVisibility(View.GONE);
            facebook.setEnabled(false);
            waze.setVisibility(View.GONE);
            instagram.setEnabled(false);
            wazeIcon.setVisibility(View.GONE);
            wazeInfoImage.setVisibility(View.GONE);
            studioName = FitForLifeDataManager.getInstance().getCurrentUser().getStudio();
            fillAbout();
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                    startActivity(intent);


                }
            });
        }


    }

    private void wazeInfoOnClick() {

        return;
    }

    private void fillAbout() {
        final DocumentReference docRef = fStore.collection("studios").document(studioName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        text.setText(document.get("text").toString());
                        if (document.get("phone") != null)
                            phone.setText(document.get("phone").toString());
                        if (document.get("facebook") != null)
                            facebook.setText(document.get("facebook").toString());
                        if (document.get("instagram") != null)
                            instagram.setText(document.get("instagram").toString());
                        if (document.get("waze") != null)
                            waze.setText(document.get("waze").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private View.OnClickListener SaveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {


            Map<String, Object> studioAbout = new HashMap<>();
            studioAbout.put("name", studioName);
            studioAbout.put("text", text.getText().toString());
            studioAbout.put("phone", phone.getText().toString());
            studioAbout.put("facebook", facebook.getText().toString());
            studioAbout.put("instagram", instagram.getText().toString());
            studioAbout.put("waze", waze.getText().toString());

            fStore.collection("studios").document(studioName)
                    .set(studioAbout)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

        }
    };


    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(AboutActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * LOGOUT METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(MoreIntent);

    }

}
