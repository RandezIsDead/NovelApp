package com.randez_trying.novel.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.randez_trying.novel.Activities.Registration.EnterNameActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.VerificationEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyEmailActivity extends AppCompatActivity {
    public String code;
    public String email;
    private final Handler handler = new Handler();
    private int secondsLeft = 60*5;
    private TextView sendAgain;
    private TextView sendAgainInactive;
    private TextView timer;

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_verify_email);

        this.email = getIntent().getStringExtra("email");
        this.code = genCode();
        verify();

        this.timer = findViewById(R.id.timer);
        this.sendAgain = findViewById(R.id.again);
        this.sendAgainInactive = findViewById(R.id.again_inactive);
        setTextGradient(this.sendAgain);
        countdown();

        TextView text = findViewById(R.id.h5);
        ImageView back = findViewById(R.id.back);
        EditText enter = findViewById(R.id.input_var_c);
        TextView errText = findViewById(R.id.err_text);
        RelativeLayout cont = findViewById(R.id.btn_cont);
        RelativeLayout load = findViewById(R.id.rel_load);

        text.setText("На указанный E-mail " + this.email + " поступит письмо с кодом. Укажите данные кода...");

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        this.sendAgain.setOnClickListener(v -> {
            secondsLeft = 60*5;
            verify();
            countdown();
            sendAgain.setVisibility(View.GONE);
            sendAgainInactive.setVisibility(View.VISIBLE);
        });

        enter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredCode = s.toString().trim();

                if (enteredCode.length() == 4) {
                    if (enteredCode.equals(String.valueOf(code))) {
                        View view = enter.findFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        cont.setVisibility(View.GONE);
                        sendAgain.setVisibility(View.GONE);
                        sendAgainInactive.setVisibility(View.GONE);
                        load.setVisibility(View.VISIBLE);
                        loginOrRegister();
                    } else errText.setVisibility(View.VISIBLE);
                } else {
                    errText.setVisibility(View.GONE);
                }
            }
        });
        cont.setOnClickListener(v -> {
            String trim = enter.getText().toString().trim();
            if (trim.length() < 4) errText.setVisibility(View.VISIBLE);
            else if (trim.equals(String.valueOf(this.code))) loginOrRegister();
            else errText.setVisibility(View.VISIBLE);
        });
    }

    private void verify() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_VERIFY,
                System.out::println,
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("to", email);
                params.put("code", code);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    public void countdown() {
        Runnable runnable = this::countdown;
        String str;
        if (secondsLeft > 0) {
            secondsLeft -= 1;
            long j = ((long) secondsLeft) * 1000;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(j);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(j - TimeUnit.MINUTES.toMillis(minutes));
            if (seconds < 10) str = "0" + seconds;
            else str = String.valueOf(seconds);
            timer.setText(minutes + ":" + str);
            handler.postDelayed(runnable, 1000);
            return;
        }
        this.sendAgain.setVisibility(View.VISIBLE);
        this.sendAgainInactive.setVisibility(View.GONE);
        this.handler.removeCallbacks(runnable);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof VerificationEditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setTextGradient(TextView textView) {
        textView.getPaint().setShader(
                new LinearGradient(0.0f, 0.0f,
                        textView.getPaint().measureText(
                                textView.getText().toString()),
                        textView.getTextSize(),
                        new int[]{Color.parseColor("#D2AB54"),
                                Color.parseColor("#FF627E")},
                        null, Shader.TileMode.CLAMP));
    }

    private String genCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) sb.append((int) (Math.random() * 10.0d));
        return sb.toString();
    }

    private void loginOrRegister() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                response -> {
                    try {
                        JSONObject o = new JSONObject(response);

                        if (!o.getBoolean("error")) {
                            JSONObject jsonObject = o.getJSONObject("0");
                            String personalId = jsonObject.getString("personalId");

                            User me = new User(
                                    Encrypt.decode(jsonObject.getString("bDate").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("balance").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("city").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("company").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("education").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("growth").getBytes(), personalId),
                                    null,
                                    Encrypt.decode(jsonObject.getString("interests").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("job").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("languages").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("mediaLinks").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("name").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("gender").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("about").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("orientation").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("familyPlans").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("sports").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("alcohol").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("smoke").getBytes(), personalId),
                                    personalId,
                                    Encrypt.decode(jsonObject.getString("personalityType").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("socialMediaLinks").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("status").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("subscriptionType").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("zodiacSign").getBytes(), personalId)
                            );
                            Prefs.saveMe(getApplicationContext(), me);
                            startActivity(new Intent(this, MainActivity.class));
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            finish();
                        } else {
                            StaticHelper.myCredentials.setEmail(this.email);
                            startActivity(new Intent(this, EnterNameActivity.class));
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
