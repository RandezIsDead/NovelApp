package com.randez_trying.novel.Activities.Registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        findViewById(R.id.btn_fix_internet).setOnClickListener(v -> {
                    String trim = ((EditText) findViewById(R.id.input_varia)).getText().toString().trim();
                    if (trim.length() > 1) {
                        StaticHelper.me.setName(trim.substring(0, 1).toUpperCase() + trim.substring(1).toLowerCase());
                        startActivity(new Intent(this, EnterBirthDateActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
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
