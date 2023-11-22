package com.randez_trying.novel.Activities.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.randez_trying.novel.Activities.Mains.MainActivity;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            if (Prefs.getMe(getApplicationContext()) == null) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }, 2000);
    }
}
