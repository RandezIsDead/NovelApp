package com.randez_trying.novel.Activities.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
        RelativeLayout cont = findViewById(R.id.btn_fix_internet);
        EditText text = findViewById(R.id.input_varia);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        cont.setOnClickListener(v -> {
            StaticHelper.me.setAbout(text.getText().toString().trim());
            startActivity(new Intent(AboutMeActivity.this, ExtraDataActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}