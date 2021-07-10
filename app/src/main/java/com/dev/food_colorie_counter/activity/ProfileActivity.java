package com.dev.food_colorie_counter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dev.food_colorie_counter.R;
import com.dev.food_colorie_counter.utils.LoadingDialog;
import com.dev.food_colorie_counter.utils.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getName();
    FirebaseFirestore db;
    private LoadingDialog loadingDialog;

    EditText ed_firstname, ed_secondname, ed_password, ed_age;
    ImageView btn_update;
    RadioButton radio_male, radio_female;

    int Gender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUI();
        getUser(Preferences.getValue_String(ProfileActivity.this, Preferences.user_ID));
    }

    private void initUI(){
        ed_firstname = findViewById(R.id.ed_firstname);
        ed_secondname = findViewById(R.id.ed_secondname);
        ed_password = findViewById(R.id.ed_password);
        ed_age = findViewById(R.id.ed_age);

        radio_male = findViewById(R.id.radio_male);
        radio_female = findViewById(R.id.radio_female);

        btn_update = findViewById(R.id.btn_update);

        btn_update.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
    }

    private void getUser(final String user_id) {
        loadingDialog = new LoadingDialog(ProfileActivity.this, false);
        db.collection("users")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            String firstname = document.getData().get("firstname").toString();
                            String secondname = document.getData().get("secondname").toString();
                            String password = document.getData().get("password").toString();
                            String age = document.getData().get("age").toString();
                            String gender = document.getData().get("gender").toString();

                            if (user_id.equals(document.getId())) {
                                ed_firstname.setText(firstname);
                                ed_secondname.setText(secondname);
                                ed_password.setText(password);
                                ed_age.setText(age);
                                if (Integer.valueOf(gender) == 0) {
                                    radio_male.setChecked(true);
                                } else if (Integer.valueOf(gender) == 1) {
                                    radio_female.setChecked(true);
                                }
                            }
                        }
                        loadingDialog.hide();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        loadingDialog.hide();
                    }
                }
            });
    }

    private void updataUser() {
        if (ed_firstname.length() != 0 && ed_secondname.length() != 0 && ed_password.length() != 0 && ed_age.length() != 0) {
            loadingDialog = new LoadingDialog(ProfileActivity.this, false);
            DocumentReference contact = db.collection("users").document(Preferences.getValue_String(ProfileActivity.this, Preferences.user_ID));
            contact.update("firstname", ed_firstname.getText().toString());
            contact.update("secondname", ed_secondname.getText().toString());
            contact.update("gender", Gender);
            contact.update("password", ed_password.getText().toString());
            contact.update("age", Integer.valueOf(ed_age.getText().toString()))
                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Updated Successfully",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        loadingDialog.hide();
                    }
                });
        } else {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_LONG).show();
        }
    }

    public void setGender() {
        if (radio_male.isChecked()) {
            Gender = 0;
        } else if (radio_female.isChecked()) {
            Gender = 1;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                setGender();
                updataUser();
                break;
            default:
                break;
        }
    }
}
