package com.randez_trying.novel.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.R;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        setTextGradient(findViewById(R.id.app_name));

        findViewById(R.id.btn_cont).setOnClickListener(v -> {
            String text =((EditText) findViewById(R.id.input_varia)).getText().toString().trim();
            if (!emailIsValid(text)) {
                Snackbar.make(v, "Ваш email имеет неправильную структуру", 0).show();
                return;
            }
            Intent intent = new Intent(this, VerifyEmailActivity.class);
            intent.putExtra("email", text);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.btn_google).setOnClickListener(v -> Snackbar.make(v, "In Development", Snackbar.LENGTH_SHORT).show());
        findViewById(R.id.btn_problem).setOnClickListener(v -> Snackbar.make(v, "In Development", Snackbar.LENGTH_SHORT).show());
    }

    private void setTextGradient(TextView textView) {
        textView.getPaint().setShader(
                new LinearGradient(0.0f, 0.0f,
                        textView.getPaint().measureText(textView.getText().toString()),
                        textView.getTextSize(),
                        new int[]{Color.parseColor("#D2AB54"),
                                Color.parseColor("#FF627E")},
                        null, Shader.TileMode.CLAMP)
        );
    }

    public boolean emailIsValid(String str) {
        return Pattern.compile("^(.+)@(\\S+)$").matcher(str).matches();
    }
}
