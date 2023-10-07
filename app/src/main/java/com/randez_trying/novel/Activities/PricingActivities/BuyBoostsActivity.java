package com.randez_trying.novel.Activities.PricingActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;

public class BuyBoostsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new BuyBoostsAdapter(getApplicationContext(), this));
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

    private static class BuyBoostsAdapter extends RecyclerView.Adapter<BuyBoostsAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;

        public BuyBoostsAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_buy_boosts, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.boostCount.setText("Всего бустов: " + StaticHelper.me.getBoosts());
            holder.back.setOnClickListener(v -> {
                activity.finish();
                activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
            });
            holder.relProSub.setOnClickListener(v -> {
                activity.startActivity(new Intent(context, SubscribeActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy1.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "100");
                intent.putExtra("count", "1");
                intent.putExtra("whatBought", "boost");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy2.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "300");
                intent.putExtra("count", "3");
                intent.putExtra("whatBought", "boost");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy3.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "500");
                intent.putExtra("count", "5");
                intent.putExtra("whatBought", "boost");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
            holder.buy4.setOnClickListener(v -> {
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("sum", "900");
                intent.putExtra("count", "10");
                intent.putExtra("whatBought", "boost");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView back;
            public RelativeLayout buy1, buy2, buy3, buy4, relProSub;
            public TextView boostCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                buy1 = itemView.findViewById(R.id.buy1);
                buy2 = itemView.findViewById(R.id.buy2);
                buy3 = itemView.findViewById(R.id.buy3);
                buy4 = itemView.findViewById(R.id.buy4);
                relProSub = itemView.findViewById(R.id.rel_pro_sub);
                boostCount = itemView.findViewById(R.id.boost_count);
            }
        }
    }
}