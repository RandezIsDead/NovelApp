package com.randez_trying.novel.Activities.Login;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.AccountPicker;
import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Activities.Mains.MainActivity;
import com.randez_trying.novel.Activities.Registration.EnterNameActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        EditText emailEnter = findViewById(R.id.input_varia);
        TextView problem = findViewById(R.id.btn_problem);
        RelativeLayout cont = findViewById(R.id.btn_cont);
        RelativeLayout google = findViewById(R.id.btn_google);

        StaticHelper.setCoolTextGradient(findViewById(R.id.app_name));

        problem.setOnClickListener(v -> Snackbar.make(v, "In Development", Snackbar.LENGTH_SHORT).show());
        cont.setOnClickListener(v -> {
            String text = emailEnter.getText().toString().trim();
            if (!emailIsValid(text)) {
                Snackbar.make(v, "Ваш email имеет неправильную структуру", 0).show();
                return;
            }
            Intent intent = new Intent(this, VerifyEmailActivity.class);
            intent.putExtra("email", text);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
        google.setOnClickListener(v -> {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[] {"com.google", "com.google.android.legacyimap"},
                    false, null, null, null, null);
            startActivityForResult(intent, 135);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 135 && resultCode == RESULT_OK) {
            loginOrRegister(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }
    }

    private void loginOrRegister(String email) {
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
                                    Encrypt.decode(jsonObject.getString("familyPlans").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("relationshipGoals").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("sports").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("alcohol").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("smoke").getBytes(), personalId),
                                    personalId,
                                    Encrypt.decode(jsonObject.getString("personalityType").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("socialMediaLinks").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("status").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("subscriptionType").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("zodiacSign").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("playlist").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("location").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("talkStyle").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("loveLang").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("pets").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("food").getBytes(), personalId),
                                    Encrypt.decode(jsonObject.getString("isOnline").getBytes(), personalId)
                            );
                            Prefs.saveMe(getApplicationContext(), me);
                            startActivity(new Intent(this, MainActivity.class));
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            finish();
                        } else {
                            StaticHelper.myCredentials.setEmail(email);
                            startActivity(new Intent(this, EnterNameActivity.class));
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

    public boolean emailIsValid(String str) {
        return Pattern.compile("^(.+)@(\\S+)$").matcher(str).matches();
    }
}
