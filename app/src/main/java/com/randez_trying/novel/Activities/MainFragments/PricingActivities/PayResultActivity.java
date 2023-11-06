package com.randez_trying.novel.Activities.MainFragments.PricingActivities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;

import java.util.HashMap;
import java.util.Map;

public class PayResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        TextView text = findViewById(R.id.text);

        ImageView back = findViewById(R.id.back);
        String whatBought = getIntent().getStringExtra("whatBought");
        final int isPay = getIntent().getIntExtra("isPay", 0);
        System.out.println("IS PAY^^^ " + isPay);
        String count = getIntent().getStringExtra("count");
        String payRes = getIntent().getStringExtra("result");

        if (payRes != null) {
            if (payRes.equals("success")) {
                text.setText("Оплата произошла\nуспешно");
                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Constants.URL_ADD_TRANSACTION,
                        response2 -> {},
                        System.out::println){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        try {
                            if (isPay == 0) {
                                params.put("fromUser", "buy");
                                params.put("toUser", StaticHelper.me.getPersonalId());
                                params.put("count", count);
                                params.put("whatBought", whatBought);
                                params.put("date", String.valueOf(System.currentTimeMillis()));
                            } else {
                                params.put("fromUser", StaticHelper.me.getPersonalId());
                                params.put("toUser", "sell");
                                params.put("count", count);
                                params.put("whatBought", whatBought);
                                params.put("date", String.valueOf(System.currentTimeMillis()));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return params;
                    }
                };

                RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest2);
            }
            else text.setText("При оплате произошла\nошибка");
        }

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
