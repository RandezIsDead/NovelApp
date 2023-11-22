package com.randez_trying.novel.Activities.Mains.Pricing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;

public class SubscribeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new SubscribeAdapter(getApplicationContext(), this));
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

    private static class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;

        public SubscribeAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public SubscribeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rel_top_back, parent, false));
            else return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_subscribtion, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull SubscribeAdapter.ViewHolder holder, int position) {
            if (position == 0) {
                holder.back.setOnClickListener(v -> {
                    activity.finish();
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                });
            } else if (position == 1) {
                holder.header.setText("Базовая подписка");
                holder.content.setText(
                        "● Фильтр по возрасту и расстоянию\n" +
                                "● Безлимитное количество свайпов\n" +
                                "● Безлимитное количество анкет\n" +
                                "● Уменьшение комиссии на кристаллы"
                );
                holder.cont_text.setText("От 100 р");
                StaticHelper.setCoolTextGradient(holder.header);
                holder.relSubCont.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                holder.cont.setOnClickListener(v -> {
                    Intent intent = new Intent(context, PayActivity.class);
                    intent.putExtra("sum", "100");
                    intent.putExtra("count", "1");
                    intent.putExtra("whatBought", "baseSub");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });
            } else if (position == 2) {
                holder.header.setText("Продвинутая подписка");
                holder.content.setText(
                        "● Преимущества предыдущей подписки\n" +
                                "● \"Перемотка\" без ограничений\n" +
                                "● Расширенный поиск"
                );
                holder.cont_text.setText("От 200 р");
                StaticHelper.setTextGradient(holder.header,
                        new int[]{Color.parseColor("#788CEF"),
                                Color.parseColor("#0CD8E5")});
                holder.content.setTextColor(Color.parseColor("#788CEF"));
                holder.relSub.setBackgroundResource(R.drawable.adv_sub_border);
                holder.cont.setBackgroundResource(R.drawable.btn_sub_200);
                holder.relSubCont.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));
                holder.cont.setOnClickListener(v -> {
                    Intent intent = new Intent(context, PayActivity.class);
                    intent.putExtra("sum", "200");
                    intent.putExtra("count", "1");
                    intent.putExtra("whatBought", "advSub");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });
            } else if (position == 3) {
                holder.header.setText("PRO-ВЕРСИЯ");
                holder.content.setText(
                        "● Преимущества предыдущих подписок\n" +
                                "● Уникальные критерии для поиска\n" +
                                "● Буст профиля"
                );
                holder.cont_text.setText("От 300 р");
                StaticHelper.setTextGradient(holder.header,
                        new int[]{Color.parseColor("#934EEF"),
                                Color.parseColor("#FF3D91")});
                holder.content.setTextColor(Color.parseColor("#934EEF"));
                holder.relSub.setBackgroundResource(R.drawable.pro_sub_border);
                holder.cont.setBackgroundResource(R.drawable.btn_sub_300);
                holder.relSubCont.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                ));

                holder.cont.setOnClickListener(v -> {
                    Intent intent = new Intent(context, PayActivity.class);
                    intent.putExtra("sum", "300");
                    intent.putExtra("count", "1");
                    intent.putExtra("whatBought", "proSub");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources()
                                .getDisplayMetrics());
                params.setMargins(px, 0, px, px);
                holder.relSub.setLayoutParams(params);
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView header, content, cont_text;
            public RelativeLayout relSubCont, relSub, cont;
            public ImageView back;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                relSubCont = itemView.findViewById(R.id.rel_sub_container);
                relSub = itemView.findViewById(R.id.rel_sub);
                header = itemView.findViewById(R.id.header);
                content = itemView.findViewById(R.id.content);
                cont_text = itemView.findViewById(R.id.cont_text);
                cont = itemView.findViewById(R.id.btn_cont);
            }
        }
    }
}