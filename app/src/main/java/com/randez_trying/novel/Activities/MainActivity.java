package com.randez_trying.novel.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

public class MainActivity extends AppCompatActivity {

    private User me;

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        me = Prefs.getMe(getApplicationContext());

        TextView text = findViewById(R.id.text);
        TextView logout = findViewById(R.id.logout);
        text.setText(me.toString());
        logout.setOnClickListener(v -> {
            Prefs.logout(getApplicationContext());
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
