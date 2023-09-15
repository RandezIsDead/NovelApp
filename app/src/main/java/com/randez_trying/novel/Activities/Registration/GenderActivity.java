package com.randez_trying.novel.Activities.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

public class GenderActivity extends AppCompatActivity {

    private boolean isManSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        ImageView back = findViewById(R.id.back);
        LinearLayout man = findViewById(R.id.lin_man);
        LinearLayout woman = findViewById(R.id.lin_woman);
        ImageView toMale = findViewById(R.id.to_male);
        ImageView toFemale = findViewById(R.id.to_female);
        RelativeLayout cont = findViewById(R.id.btn_fix_internet);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        toFemale.setOnClickListener(v -> {
            man.setVisibility(View.GONE);
            woman.setVisibility(View.VISIBLE);
            isManSelected = !isManSelected;
        });
        toMale.setOnClickListener(v -> {
            man.setVisibility(View.VISIBLE);
            woman.setVisibility(View.GONE);
            isManSelected = !isManSelected;
        });
        cont.setOnClickListener(v -> {
            StaticHelper.me.setGender(isManSelected ? "male" : "female");
            startActivity(new Intent(GenderActivity.this, AboutMeActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
    }
}