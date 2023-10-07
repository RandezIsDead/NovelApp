package com.randez_trying.novel.Activities.Registration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.BirthDateEditText;
import com.randez_trying.novel.Views.BirthDateTextView;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class EnterBirthDateActivity extends AppCompatActivity {

    private BirthDateEditText et;
    private BirthDateTextView tv;

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_enter_birth_date);

        ImageView back = findViewById(R.id.back);
        RelativeLayout cont = findViewById(R.id.btn_cont);

        et = findViewById(R.id.input_var_c);
        tv = findViewById(R.id.tv);

        if (!StaticHelper.me.getbDate().isEmpty()) {
            String[] split = StaticHelper.me.getbDate().split("-");
            et.setText(split[2] + split[1] + split[0]);
            for (int i = 0; i < 8; i++) tv.paints[i].setColor(getResources().getColor(R.color.light_grey));
            tv.setText(getResources().getText(R.string.dmy));
            tv.draw(new Canvas());
        }

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < s.length(); i++) tv.paints[i].setColor(Color.parseColor("#ffFFFFFF"));
                for (int i = s.length(); i < 8; i++) tv.paints[i].setColor(getResources().getColor(R.color.other_grey));
                tv.setText(getResources().getText(R.string.dmy));
                tv.draw(new Canvas());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                end(v);
                return true;
            }
            return false;
        });

        back.setOnClickListener(v -> {
            startActivity(new Intent(EnterBirthDateActivity.this, EnterNameActivity.class));
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
        });
        cont.setOnClickListener(this::end);
    }

    private void end(View v) {
        try {
            String[] sp = Objects.requireNonNull(et.getText()).toString().split("");
            String day = sp[0] + sp[1];
            String month = sp[2] + sp[3];
            String year = sp[4] + sp[5] + sp[6] + sp[7];
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime parse = LocalDateTime.parse(year + "-" + month + "-" + day + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (Integer.parseInt(day) > 31 || Integer.parseInt(month) > 12 || Integer.parseInt(year) > Year.now().getValue()) {
                Snackbar.make(v, "Некорректно введена дата", 0).show();
            } else if (parse.until(now, ChronoUnit.YEARS) < 16) {
                Snackbar.make(v, "Вам должно быть больше 16 лет", 0).show();
            } else {
                StaticHelper.me.setbDate(year + "-" + month + "-" + day);
                System.out.println(StaticHelper.me.getbDate());
                startActivity(new Intent(this, LocationActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        } catch (RuntimeException e) {
            Snackbar.make(v, "Некорректно введена дата", Snackbar.LENGTH_SHORT).show();
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(EnterBirthDateActivity.this, EnterNameActivity.class));
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}