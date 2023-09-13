package com.randez_trying.novel.Activities.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

public class AboutMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        ImageView back = findViewById(R.id.back);
        RelativeLayout cont = findViewById(R.id.btn_cont);
        EditText text = findViewById(R.id.input_varia);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
        cont.setOnClickListener(v -> {
            StaticHelper.me.setAbout(text.getText().toString().trim());
            startActivity(new Intent(AboutMeActivity.this, ExtraDataActivity.class));
            overridePendingTransition(0, 0);
        });
    }
}