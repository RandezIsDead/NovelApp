package com.randez_trying.novel.Activities.PricingActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;

public class BuyCrystalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new BuyCrystalsAdapter(getApplicationContext(), this));
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

    private static class BuyCrystalsAdapter extends RecyclerView.Adapter<BuyCrystalsAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private int crystals = 1;
        private int costCrystal = 100;

        public BuyCrystalsAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_buy_crystals, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.back.setOnClickListener(v -> {
                activity.finish();
                activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
            });

            holder.crystalsCount.setText("Всего кристаллов: " + StaticHelper.me.getBalance());
            holder.excCounter.setText(String.valueOf(crystals));
            holder.rubCounter.setText(100 + " ₽");

            holder.excCounter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) crystals = Integer.parseInt(s.toString());
                    if (crystals >= 1) {
                        holder.rubCounter.setText((crystals * costCrystal) + " ₽");
                    } else {
                        crystals = 1;
                        holder.excCounter.setText(String.valueOf(crystals));
                        holder.rubCounter.setText(100 + " ₽");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        crystals = 1;
                        holder.excCounter.setText(String.valueOf(crystals));
                        holder.rubCounter.setText(100 + " ₽");
                    }
                }
            });

            holder.plus.setOnClickListener(v -> {
                crystals++;
                holder.excCounter.setText(String.valueOf(crystals));
                holder.rubCounter.setText((crystals * costCrystal) + " ₽");
            });
            holder.minus.setOnClickListener(v -> {
                if (crystals > 1) {
                    crystals--;
                    holder.excCounter.setText(String.valueOf(crystals));
                    holder.rubCounter.setText((crystals * costCrystal) + " ₽");
                }
            });
            holder.exchange.setOnClickListener(v -> {
                activity.startActivity(new Intent(context, ExchangeCrystalsActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy1.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "100");
                intent.putExtra("count", "1");
                intent.putExtra("whatBought", "crystal");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy2.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "300");
                intent.putExtra("count", "3");
                intent.putExtra("whatBought", "crystal");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy3.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "500");
                intent.putExtra("count", "5");
                intent.putExtra("whatBought", "crystal");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy4.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "1000");
                intent.putExtra("count", "10");
                intent.putExtra("whatBought", "crystal");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.cont.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", String.valueOf(crystals * costCrystal));
                intent.putExtra("count", String.valueOf(crystals));
                intent.putExtra("whatBought", "crystal");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView back, plus, minus;
            public RelativeLayout buy1, buy2, buy3, buy4, cont, exchange;
            public TextView crystalsCount, rubCounter;
            public EditText excCounter;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                plus = itemView.findViewById(R.id.plus);
                minus = itemView.findViewById(R.id.minus);
                exchange = itemView.findViewById(R.id.btn_exchange);
                buy1 = itemView.findViewById(R.id.buy1);
                buy2 = itemView.findViewById(R.id.buy2);
                buy3 = itemView.findViewById(R.id.buy3);
                buy4 = itemView.findViewById(R.id.buy4);
                crystalsCount = itemView.findViewById(R.id.boost_count);

                excCounter = itemView.findViewById(R.id.exc_counter);
                rubCounter = itemView.findViewById(R.id.rub_counter);
                cont = itemView.findViewById(R.id.btn_cont);
            }
        }
    }
}