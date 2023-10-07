package com.randez_trying.novel.Activities.Registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationManualActivity extends AppCompatActivity {

    private EditText editText;
    private ListView listView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_location_manual);

        editText = findViewById(R.id.input_varia);
        listView = findViewById(R.id.lv_hints);
        ImageView back = findViewById(R.id.back);
        RelativeLayout cont = findViewById(R.id.btn_cont);
        getCities();

        if (!StaticHelper.me.getCity().isEmpty()) {
            editText.setText(StaticHelper.me.getCity());
        }

        back.setOnClickListener(v -> {
            startActivity(new Intent(LocationManualActivity.this, EnterBirthDateActivity.class));
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
        });

        editText.setText(getIntent().getStringExtra("city"));
        cont.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                String city = editText.getText().toString().trim();
                StaticHelper.me.setCity(city);
                startActivity(new Intent(LocationManualActivity.this, AddPhotosActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            } else Snackbar.make(v, "Поле не может быть пустым", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void getCities() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_CITIES,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        final String[] split = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            split[i] = Encrypt.decode(array.getJSONObject(i).getString("city").getBytes(), "0");
                        }

                        editText.addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable editable) {}
                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                if (!charSequence.toString().trim().isEmpty()) {
                                    ArrayList<String> arrayList = new ArrayList<>();
                                    for (String str : split) {
                                        if (str.toLowerCase().startsWith(charSequence.toString().trim().toLowerCase())) {
                                            arrayList.add(str);
                                        }
                                    }
                                    listView.setVisibility(View.VISIBLE);
                                    listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.item_text, arrayList));
                                    listView.setOnItemClickListener((parent, view, position, id) -> {
                                        editText.setText(arrayList.get(position));
                                        listView.setVisibility(View.GONE);
                                    });
                                } else listView.setVisibility(View.GONE);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(LocationManualActivity.this, EnterBirthDateActivity.class));
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
