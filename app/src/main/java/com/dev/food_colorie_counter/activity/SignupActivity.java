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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SignupActivity.class.getName();

    ImageView btn_create;
    EditText ed_firstname, ed_secondname, ed_password, ed_age;
    RadioButton radio_male, radio_female;

    FirebaseFirestore db;

    int Gender = 0;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initUI();
    }

    private void initUI(){
        btn_create = findViewById(R.id.btn_create);

        ed_firstname = findViewById(R.id.ed_firstname);
        ed_secondname = findViewById(R.id.ed_secondname);
        ed_password = findViewById(R.id.ed_password);
        ed_age = findViewById(R.id.ed_age);

        radio_male = findViewById(R.id.radio_male);
        radio_female = findViewById(R.id.radio_female);

        btn_create.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();

        radio_male.setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_create:
                loadingDialog = new LoadingDialog(SignupActivity.this, false);
                setGender();

                Map<String, Object> user = new HashMap<>();

                user.put("firstname", ed_firstname.getText().toString());
                user.put("secondname", ed_secondname.getText().toString());
                user.put("password", ed_password.getText().toString());
                user.put("gender", Gender);
                user.put("age", Integer.valueOf(ed_age.getText().toString()));

                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "success " + documentReference.getId());
                                loadingDialog.hide();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.hide();
                            }
                        });
                break;
            default:
                break;
        }
    }

    public void setGender() {
        if (radio_male.isChecked()) {
            Gender = 0;
        } else if (radio_female.isChecked()) {
            Gender = 1;
        }
    }
}
