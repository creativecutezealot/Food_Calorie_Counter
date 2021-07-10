package com.dev.food_colorie_counter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.dev.food_colorie_counter.R;
import com.dev.food_colorie_counter.utils.Preferences;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btn_next;
    RadioButton radio_1, radio_2, radio_3, radio_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initUI();
    }

    private void initUI(){
        btn_next = findViewById(R.id.btn_next);

        radio_1 = findViewById(R.id.radio_1);
        radio_2 = findViewById(R.id.radio_2);
        radio_3 = findViewById(R.id.radio_3);
        radio_4 = findViewById(R.id.radio_4);

        btn_next.setOnClickListener(this);

        radio_1.setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next:
                setGender();
                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    public void setGender() {
        if (radio_1.isChecked()) {
            Preferences.setValue_Int(this, Preferences.groupCalorie, 1800);
        } else if (radio_2.isChecked()) {
            Preferences.setValue_Int(this, Preferences.groupCalorie, 2600);
        } else if (radio_3.isChecked()) {
            Preferences.setValue_Int(this, Preferences.groupCalorie, 2300);
        } else if (radio_4.isChecked()) {
            Preferences.setValue_Int(this, Preferences.groupCalorie, 2000);
        }
    }
}
