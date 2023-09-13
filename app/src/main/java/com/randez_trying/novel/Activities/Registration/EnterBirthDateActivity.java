package com.randez_trying.novel.Activities.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class EnterBirthDateActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_enter_birth_date);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.btn_cont)
                .setOnClickListener(v -> {
                    String trim = ((EditText) findViewById(R.id.input_varia)).getText().toString().trim();
                    String substring = trim.substring(0, 2);
                    String substring2 = trim.substring(2, 4);
                    String substring3 = trim.substring(4);
                    LocalDateTime now = LocalDateTime.now();
                    try {
                        LocalDateTime parse = LocalDateTime.parse(substring3 + "-" + substring2 + "-" + substring + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (Integer.parseInt(substring) > 31 || Integer.parseInt(substring2) > 12 || Integer.parseInt(substring3) > Year.now().getValue()) {
                            Snackbar.make(v, "Некорректно введена дата", 0).show();
                        } else if (parse.until(now, ChronoUnit.YEARS) < 16) {
                            Snackbar.make(v, "Вам должно быть больше 16 лет", 0).show();
                        } else {
                            StaticHelper.me.setbDate(substring3 + "-" + substring2 + "-" + substring);
                            startActivity(new Intent(this, LocationActivity.class));
                            overridePendingTransition(0, 0);
                        }
                    } catch (RuntimeException e) {
                        Snackbar.make(v, "Некорректно введена дата", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
