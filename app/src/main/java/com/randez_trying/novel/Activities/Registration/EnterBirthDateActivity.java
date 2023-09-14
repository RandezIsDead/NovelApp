package com.randez_trying.novel.Activities.Registration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class EnterBirthDateActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static EditText et1, et2, et3, et4, et5, et6, et7, et8;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_enter_birth_date);

        ImageView back = findViewById(R.id.back);
        RelativeLayout cont = findViewById(R.id.btn_cont);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        et7 = findViewById(R.id.et7);
        et8 = findViewById(R.id.et8);

//        et[0].setOnClickListener(listener);
//        et[1].setOnClickListener(listener);
//        et[2].setOnClickListener(listener);
//        et[3].setOnClickListener(listener);
//        et[4].setOnClickListener(listener);
//        et[5].setOnClickListener(listener);
//        et[6].setOnClickListener(listener);
//        et[7].setOnClickListener(listener);

//        LinearLayout iv = findViewById(R.id.input_varia);
//
//        iv.setOnClickListener(v -> {
//            System.out.println("CLICK");
//            for (EditText editText : et) {
//                String text = editText.getText().toString();
//                if (!text.isEmpty()) {
//                    editText.requestFocus();
//                }
//            }
//        });

        et1.addTextChangedListener(new GenericTextWatcher(et1, et2));
        et2.addTextChangedListener(new GenericTextWatcher(et2, et3));
        et3.addTextChangedListener(new GenericTextWatcher(et3, et4));
        et4.addTextChangedListener(new GenericTextWatcher(et4, et5));
        et5.addTextChangedListener(new GenericTextWatcher(et5, et6));
        et6.addTextChangedListener(new GenericTextWatcher(et6, et7));
        et7.addTextChangedListener(new GenericTextWatcher(et7, et8));

        et2.setOnKeyListener(new GenericKeyEvent(et2, et1));
        et3.setOnKeyListener(new GenericKeyEvent(et3, et2));
        et4.setOnKeyListener(new GenericKeyEvent(et4, et3));
        et5.setOnKeyListener(new GenericKeyEvent(et5, et4));
        et6.setOnKeyListener(new GenericKeyEvent(et6, et5));
        et7.setOnKeyListener(new GenericKeyEvent(et7, et6));
        et8.setOnKeyListener(new GenericKeyEvent(et8, et7));

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        cont.setOnClickListener(v -> {
                    String day = et1.getText().toString() + et2.getText().toString();
                    String month = et3.getText().toString() + et4.getText().toString();
                    String year = et5.getText().toString() + et6.getText().toString() + et7.getText().toString() + et8.getText().toString();
                    LocalDateTime now = LocalDateTime.now();
                    try {
                        LocalDateTime parse = LocalDateTime.parse(year + "-" + month + "-" + day + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (Integer.parseInt(day) > 31 || Integer.parseInt(month) > 12 || Integer.parseInt(year) > Year.now().getValue()) {
                            Snackbar.make(v, "Некорректно введена дата", 0).show();
                        } else if (parse.until(now, ChronoUnit.YEARS) < 16) {
                            Snackbar.make(v, "Вам должно быть больше 16 лет", 0).show();
                        } else {
                            StaticHelper.me.setbDate(year + "-" + month + "-" + day);
                            startActivity(new Intent(this, LocationActivity.class));
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        }
                    } catch (RuntimeException e) {
                        Snackbar.make(v, "Некорректно введена дата", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

//    View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            for (int i = 0; i < et.length; i++) {
//                if (v.equals(et[i])) {
//                    if (et[i].getText().toString().isEmpty()) {
//                        for (int j = i; j > 0; j--) {
//                            if (!et[j].getText().toString().isEmpty()) {
//                                et[j+1].requestFocus();
//                            }
//                        }
//                    } else {
//                        for (int j = i; j < et.length; j++) {
//                            if (et[j].getText().toString().isEmpty()) {
//                                et[j].requestFocus();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    };

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

    private static class GenericKeyEvent implements View.OnKeyListener {
        private final EditText currentView;
        private final EditText previousView;

        public GenericKeyEvent(EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_DEL
                    && currentView.getId() != R.id.et1
                    && currentView.getText().toString().isEmpty()) {
                previousView.setText(null);
                previousView.requestFocus();
                return true;
            }
            return false;
        }
    }

    private static class GenericTextWatcher implements TextWatcher {
        private final View currentView;
        private final View nextView;

        public GenericTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            if (currentView.equals(et1) || currentView.equals(et2)
                    || currentView.equals(et3) || currentView.equals(et4)
                    || currentView.equals(et5) || currentView.equals(et6)
                    || currentView.equals(et7) || currentView.equals(et8)) {
                if (text.length() == 1) {
                    nextView.requestFocus();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    }
}