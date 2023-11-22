package com.randez_trying.novel.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.randez_trying.novel.Activities.Mains.Settings.AccountSettingsActivity;
import com.randez_trying.novel.Activities.Mains.Pricing.BuyBoostsActivity;
import com.randez_trying.novel.Activities.Mains.Pricing.BuyCrystalsActivity;
import com.randez_trying.novel.Activities.Mains.Pricing.SubscribeActivity;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;

import java.util.Objects;

public class AccountFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rec_view_layout, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AccountAdapter(requireActivity().getApplicationContext(), requireActivity()));

        return root;
    }

    private class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;

        public AccountAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public AccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account_first_part, parent, false));
            else return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account_second_part, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
            if (position == 0) {
                StaticHelper.setCoolTextGradient(holder.appName);
                Glide.with(requireActivity().getApplicationContext()).load(StaticHelper.me.getMediaLinks().split("&")[0]).circleCrop().into(holder.photo);
                holder.nameAge.setText(StaticHelper.me.getName() + ", " + StaticHelper.getAge(StaticHelper.me.getbDate()));

                holder.edit.setOnClickListener(v -> {
                    activity.startActivity(new Intent(context, AccountSettingsActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });
                holder.boost.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
                    bottomSheetDialog.setContentView(R.layout.alert_boost);
                    Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.7f);

                    TextView boosts = bottomSheetDialog.findViewById(R.id.boost_count);
                    RelativeLayout cont = bottomSheetDialog.findViewById(R.id.btn_cont);
                    bottomSheetDialog.show();

                    if (boosts != null) boosts.setText("Бустов: " + StaticHelper.me.getBoosts());
                    if (cont != null) cont.setOnClickListener(vi -> {
                        activity.startActivity(new Intent(context, BuyBoostsActivity.class));
                        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        bottomSheetDialog.dismiss();
                    });
                });
                holder.crystals.setOnClickListener(v -> {
                    activity.startActivity(new Intent(context, BuyCrystalsActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });
                holder.invisibility.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
                    bottomSheetDialog.setContentView(R.layout.alert_invis);
                    Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.7f);

                    TextView h4 = bottomSheetDialog.findViewById(R.id.h4);
                    RelativeLayout cont = bottomSheetDialog.findViewById(R.id.btn_cont);

                    bottomSheetDialog.show();

                    String text = "<font color=#575757>Данный режим можно приобрести\n с подпиской</font> <font color=#934EEF> Премиум</font>";
                    if (h4 != null) h4.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
                    if (cont != null) cont.setOnClickListener(vi -> {
                        activity.startActivity(new Intent(context, SubscribeActivity.class));
                        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        bottomSheetDialog.dismiss();
                    });
                });
            } else if (position == 1) {
                holder.pager.setAdapter(new SubscribeAdapter(requireActivity().getApplicationContext(), requireActivity()));
                holder.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        switch (position) {
                            case 0:
                                holder.in1.setImageResource(R.drawable.cool_circle);
                                holder.in2.setImageResource(R.drawable.cdf_circle);
                                holder.in3.setImageResource(R.drawable.cdf_circle);
                                break;
                            case 1:
                                holder.in1.setImageResource(R.drawable.cdf_circle);
                                holder.in2.setImageResource(R.drawable.cool_circle);
                                holder.in3.setImageResource(R.drawable.cdf_circle);
                                break;
                            case 2:
                                holder.in1.setImageResource(R.drawable.cdf_circle);
                                holder.in2.setImageResource(R.drawable.cdf_circle);
                                holder.in3.setImageResource(R.drawable.cool_circle);
                                break;
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView edit, photo, in1, in2, in3;
            public TextView appName, nameAge;
            public RelativeLayout boost, crystals, invisibility;
            public ViewPager2 pager;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                appName = itemView.findViewById(R.id.app_name);
                edit = itemView.findViewById(R.id.edit);
                photo = itemView.findViewById(R.id.user_ico);
                nameAge = itemView.findViewById(R.id.username_age);
                boost = itemView.findViewById(R.id.get_boost);
                crystals = itemView.findViewById(R.id.get_crystals);
                invisibility = itemView.findViewById(R.id.get_inv);
                pager = itemView.findViewById(R.id.rec_subscribes);
                in1 = itemView.findViewById(R.id.in1);
                in2 = itemView.findViewById(R.id.in2);
                in3 = itemView.findViewById(R.id.in3);
            }
        }
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_subscribtion, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                holder.header.setText("Базовая подписка");
                holder.content.setText(
                        "● Фильтр по возрасту и расстоянию\n" +
                                "● Безлимитное количество свайпов\n" +
                                "● Безлимитное количество анкет\n" +
                                "● Уменьшение комиссии на кристаллы"
                );
                holder.cont_text.setText("От 100 р");
                StaticHelper.setCoolTextGradient(holder.header);
            } else if (position == 1) {
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
            } else if (position == 2) {
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
            }

            holder.cont.setOnClickListener(v -> {
                activity.startActivity(new Intent(context, SubscribeActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView header, content, cont_text;
            public RelativeLayout relSub, cont;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                relSub = itemView.findViewById(R.id.rel_sub);
                header = itemView.findViewById(R.id.header);
                content = itemView.findViewById(R.id.content);
                cont_text = itemView.findViewById(R.id.cont_text);
                cont = itemView.findViewById(R.id.btn_cont);
            }
        }
    }
}