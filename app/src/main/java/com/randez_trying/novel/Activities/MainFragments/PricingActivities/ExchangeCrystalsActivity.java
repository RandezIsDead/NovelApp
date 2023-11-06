package com.randez_trying.novel.Activities.MainFragments.PricingActivities;

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

public class ExchangeCrystalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ExchangeCrystalsAdapter(getApplicationContext(), this));
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

    private static class ExchangeCrystalsAdapter extends RecyclerView.Adapter<ExchangeCrystalsAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;

        private int crystals = 1;
        private static final int costCrystal = 100;

        public ExchangeCrystalsAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exchange, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.back.setOnClickListener(v -> {
                activity.finish();
                activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
            });

            holder.crystalsCount.setText(String.valueOf(StaticHelper.me.getBalance()));
            holder.balance.setText(Integer.parseInt(StaticHelper.me.getBalance()) * costCrystal + " ₽");
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
            holder.cont.setOnClickListener(v -> {
                if (crystals <= Integer.parseInt(StaticHelper.me.getBalance())) {
                    Intent intent = new Intent(context, PayActivity.class);
                    intent.putExtra("sum", String.valueOf(crystals * costCrystal));
                    intent.putExtra("count", String.valueOf(crystals));
                    intent.putExtra("isPay", "1");
                    intent.putExtra("whatBought", "crystal");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                } else Snackbar.make(v, "На счету недостаточно кристаллов", Snackbar.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView back, plus, minus;
            public RelativeLayout cont;
            public TextView crystalsCount, balance, rubCounter;
            public EditText excCounter;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                plus = itemView.findViewById(R.id.plus);
                minus = itemView.findViewById(R.id.minus);
                cont = itemView.findViewById(R.id.btn_cont);
                crystalsCount = itemView.findViewById(R.id.boost_count);
                balance = itemView.findViewById(R.id.balance);
                excCounter = itemView.findViewById(R.id.exc_counter);
                rubCounter = itemView.findViewById(R.id.rub_counter);
            }
        }
    }
}