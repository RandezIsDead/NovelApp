package com.randez_trying.novel.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

        ((TextView) findViewById(R.id.h5)).setText("На указанный E-mail " + this.email + " поступит письмо с кодом. Укажите данные кода...");
        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
        this.sendAgain.setOnClickListener(v -> {
            this.secondsLeft = 60*5;
            verify();
            countdown();
            this.sendAgain.setVisibility(View.GONE);
            this.sendAgainInactive.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.btn_cont).setOnClickListener(v -> {
            TextView textView = findViewById(R.id.err_text);
            String trim = Objects.requireNonNull(((EditText) findViewById(R.id.input_var_c)).getText()).toString().trim();
            if (trim.length() < 4) textView.setVisibility(View.VISIBLE);
            else if (trim.equals(String.valueOf(this.code))) loginOrRegister();
            else textView.setVisibility(View.VISIBLE);
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
        int i = this.secondsLeft;
        if (i > 0) {
            int i2 = i - 1;
            this.secondsLeft = i2;
            long j = ((long) i2) * 1000;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(j);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(j - TimeUnit.MINUTES.toMillis(minutes));
            if (seconds < 10) {
                str = "0" + seconds;
            } else {
                str = String.valueOf(seconds);
            }
            this.timer.setText(minutes + ":" + str);
            this.handler.postDelayed(runnable, 1000);
            return;
        }
        this.sendAgain.setVisibility(View.VISIBLE);
        this.sendAgainInactive.setVisibility(View.GONE);
        this.handler.removeCallbacks(runnable);
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
                            String bDate = Encrypt.decode(jsonObject.getString("bDate").getBytes(), personalId);
                            String balance = Encrypt.decode(jsonObject.getString("balance").getBytes(), personalId);
                            String city = Encrypt.decode(jsonObject.getString("city").getBytes(), personalId);
                            String company = Encrypt.decode(jsonObject.getString("company").getBytes(), personalId);
                            String education = Encrypt.decode(jsonObject.getString("education").getBytes(), personalId);
                            String growth = Encrypt.decode(jsonObject.getString("growth").getBytes(), personalId);
                            String interests = Encrypt.decode(jsonObject.getString("interests").getBytes(), personalId);
                            String job = Encrypt.decode(jsonObject.getString("job").getBytes(), personalId);
                            String languages = Encrypt.decode(jsonObject.getString("languages").getBytes(), personalId);
                            String mediaLinks = Encrypt.decode(jsonObject.getString("mediaLinks").getBytes(), personalId);
                            String name = Encrypt.decode(jsonObject.getString("name").getBytes(), personalId);
                            String gender = Encrypt.decode(jsonObject.getString("gender").getBytes(), personalId);
                            String about = Encrypt.decode(jsonObject.getString("about").getBytes(), personalId);
                            String orientation = Encrypt.decode(jsonObject.getString("orientation").getBytes(), personalId);
                            String familyPlans = Encrypt.decode(jsonObject.getString("familyPlans").getBytes(), personalId);
                            String sports = Encrypt.decode(jsonObject.getString("sports").getBytes(), personalId);
                            String alcohol = Encrypt.decode(jsonObject.getString("alcohol").getBytes(), personalId);
                            String smoke = Encrypt.decode(jsonObject.getString("smoke").getBytes(), personalId);
                            String personalityType = Encrypt.decode(jsonObject.getString("personalityType").getBytes(), personalId);
                            String socialMediaLinks = Encrypt.decode(jsonObject.getString("socialMediaLinks").getBytes(), personalId);
                            String status = Encrypt.decode(jsonObject.getString("status").getBytes(), personalId);
                            String subscriptionType = Encrypt.decode(jsonObject.getString("subscriptionType").getBytes(), personalId);
                            String zodiacSign = Encrypt.decode(jsonObject.getString("zodiacSign").getBytes(), personalId);

                            User me = new User(
                                    bDate,
                                    balance,
                                    city,
                                    company,
                                    education,
                                    growth,
                                    null,
                                    interests,
                                    job,
                                    languages,
                                    mediaLinks,
                                    name,
                                    gender,
                                    about,
                                    orientation,
                                    familyPlans,
                                    sports,
                                    alcohol,
                                    smoke,
                                    personalId,
                                    personalityType,
                                    socialMediaLinks,
                                    status,
                                    subscriptionType,
                                    zodiacSign
                            );
                            Prefs.saveMe(getApplicationContext(), me);
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            StaticHelper.myCredentials.setEmail(this.email);
                            startActivity(new Intent(this, EnterNameActivity.class));
                            overridePendingTransition(0, 0);
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
