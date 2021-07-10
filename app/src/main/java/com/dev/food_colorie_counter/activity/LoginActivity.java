package com.dev.food_colorie_counter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.food_colorie_counter.R;
import com.dev.food_colorie_counter.utils.LoadingDialog;
import com.dev.food_colorie_counter.utils.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();

    ImageView btn_login, btn_signup;
    EditText ed_username, ed_password;

    FirebaseFirestore db;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }

    private void initUI(){
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
    }

    boolean flag = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                loadingDialog = new LoadingDialog(LoginActivity.this, false);
                if (ed_username.length() != 0 && ed_password.length() != 0) {

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

                                        if (ed_username.getText().toString().equals(firstname + " " + secondname) && ed_password.getText().toString().equals(password)) {
                                            Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                                            startActivity(intent);
                                            Preferences.setValue_String(LoginActivity.this, Preferences.user_ID, document.getId());
                                            flag = true;
                                            ed_username.setText("");
                                            ed_password.setText("");
                                            break;
                                        }
                                        if (document.getId().equals("")) showMessage("You can't login. Please signup.");
                                    }
                                    if (!flag) {
                                        showMessage("You can't login.");
                                        ed_username.setText("");
                                        ed_password.setText("");
                                    }
                                    loadingDialog.hide();
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                    loadingDialog.hide();
                                }
                            }
                        });
                } else {
                    showMessage("Please fill all fields.");
                }

                break;
            case R.id.btn_signup:
                Intent signupintent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupintent);
                break;
            default:
                break;
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
