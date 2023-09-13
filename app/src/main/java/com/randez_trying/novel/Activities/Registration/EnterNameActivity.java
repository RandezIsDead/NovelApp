package com.randez_trying.novel.Activities.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

public class EnterNameActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_enter_name);

        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.btn_cont).setOnClickListener(v -> {
                    String trim = ((EditText) findViewById(R.id.input_varia)).getText().toString().trim();
                    if (trim.length() > 1) {
                        StaticHelper.me.setName(trim.substring(0, 1).toUpperCase() + trim.substring(1).toLowerCase());
                        startActivity(new Intent(this, EnterBirthDateActivity.class));
                        overridePendingTransition(0, 0);
                    }
                });
    }
}
