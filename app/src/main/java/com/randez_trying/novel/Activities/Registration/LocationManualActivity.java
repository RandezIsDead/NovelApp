package com.randez_trying.novel.Activities.Registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

import java.util.ArrayList;

public class LocationManualActivity extends AppCompatActivity {

    String cities = "Королёв&Москва&Санкт-Петербург&Новосибирск&Екатеринбург&Казань&Нижний Новгород&Ростов-на-Дону&Хабаровск&Владивосток&Пятигорск&Красноярск"
            + "Челябинск&Самара&Уфа&Краснодар&Омск&Воронеж&Пермь&Волгоград&Саратов&Тюмень&Барнаул&Махачкала&Ижевск&Севастополь&Томск&Ставрополь&Минск&Гомель&Витебск";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_location_manual);

        ImageView back = findViewById(R.id.back);
        EditText editText = findViewById(R.id.input_varia);
        ListView listView = findViewById(R.id.lv_hints);
        RelativeLayout cont = findViewById(R.id.btn_cont);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });

        editText.setText(getIntent().getStringExtra("city"));
        final String[] split = this.cities.split("&");

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
//                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
//                        System.out.print(arrayList.get(i4) + " ");
//                    }
                } else listView.setVisibility(View.GONE);
            }
        });
        cont.setOnClickListener(v -> {
            String city = editText.getText().toString().trim();
            StaticHelper.me.setCity(city);
            startActivity(new Intent(LocationManualActivity.this, AddPhotosActivity.class));
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
