package com.randez_trying.novel.Activities.MainFragments.SettingsActivities;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.randez_trying.novel.Activities.PricingActivities.SubscribeActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FiltersSettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        FilterAdapter adapter = new FilterAdapter(getApplicationContext(), this);
        adapter.setHasStableIds(true);
        recyclerView.setItemViewCacheSize(22);
        recyclerView.setAdapter(adapter);
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

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

        private static final int ITEM_TOP_REL = 1;
        private static final int ITEM_SUBSCRIBE = 2;
        private static final int ITEM_DOUBLE_SEEK_BAR = 3;
        private static final int ITEM_SWITCH = 4;
        private static final int ITEM_PARAM_EXTRAS = 5;
        private final Context context;
        private final Activity activity;

        public FilterAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_TOP_REL) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rel_top_back, parent, false));
            else if (viewType == ITEM_SUBSCRIBE) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sub, parent, false));
            else if (viewType == ITEM_DOUBLE_SEEK_BAR) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_double_seek, parent, false));
            else if (viewType == ITEM_SWITCH) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_switch, parent, false));
            else return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_interest, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull FilterAdapter.ViewHolder holder, int position) {
            List<String> options = new ArrayList<>();
            boolean multiple = false;

            if (position == 0) {
                holder.windowTitle.setText("Параметры поиска");
                holder.back.setOnClickListener(v -> {
                    activity.finish();
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                });
            } else if (position == 1 || position == 7) {
                if (position == 7) {
                    holder.subBG.setBackgroundResource(R.drawable.btn_sub_200);
                    holder.subImg.setImageResource(R.drawable.adv_sub_line);
                    holder.subTitle.setText("Продвинутая подписка");
                    holder.subBp.setText("Подбирай партнёра по интересам");
                }
                holder.itemView.setOnClickListener(v -> {
                    activity.startActivity(new Intent(context, SubscribeActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });
            } else if (position == 2) {
                holder.rsTitle.setText("Расстояние");
                holder.rsExtraText.setText(StaticHelper.minDistance + "-" + StaticHelper.maxDistance + " км");
                holder.rs.setValues(Arrays.asList((float) StaticHelper.minDistance, (float) StaticHelper.maxDistance));

                holder.rs.addOnChangeListener((slider, value, fromUser) -> {
                    List<Float> values = slider.getValues();
                    StaticHelper.minDistance = (int) (values.get(0).doubleValue());
                    StaticHelper.maxDistance = (int) (values.get(1).doubleValue());
                    holder.rsExtraText.setText(StaticHelper.minDistance + "-" + StaticHelper.maxDistance + " км");
                    Prefs.write(context, "minDistance", String.valueOf(StaticHelper.minDistance));
                    Prefs.write(context, "maxDistance", String.valueOf(StaticHelper.maxDistance));
                });
            } else if (position == 4) {
                holder.rsTitle.setText("Возраст");
                holder.rsExtraText.setText(StaticHelper.minAge + "-" + StaticHelper.maxAge + " лет");
                holder.rs.setVisibility(View.GONE);
                holder.rsa.setVisibility(View.VISIBLE);

                holder.rsa.setValues(Arrays.asList((float) StaticHelper.minAge, (float) StaticHelper.maxAge));

                holder.rsa.addOnChangeListener((slider, value, fromUser) -> {
                    List<Float> values = slider.getValues();
                    StaticHelper.minAge = (int) (values.get(0).doubleValue());
                    StaticHelper.maxAge = (int) (values.get(1).doubleValue());
                    holder.rsExtraText.setText(StaticHelper.minAge + "-" + StaticHelper.maxAge + " лет");
                    Prefs.write(context, "minAge", String.valueOf(StaticHelper.minAge));
                    Prefs.write(context, "maxAge", String.valueOf(StaticHelper.maxAge));
                });
            } else if (position == 3) {
                holder.swTitle.setText("Показывать людей в диапазоне");
                if (StaticHelper.showInRange) holder.sw.setChecked(true);
                holder.sw.setOnCheckedChangeListener((buttonView, isChecked) -> StaticHelper.showInRange = isChecked);
            } else if (position == 5) {
                holder.swTitle.setText("Показывать меня мужчинам");
                if (StaticHelper.showMen) holder.sw.setChecked(true);
                holder.sw.setOnCheckedChangeListener((buttonView, isChecked) -> StaticHelper.showMen= isChecked);
            } else if (position == 6) {
                holder.swTitle.setText("Показывать меня женщинам");
                if (StaticHelper.showWoman) holder.sw.setChecked(true);
                holder.sw.setOnCheckedChangeListener((buttonView, isChecked) -> StaticHelper.showWoman = isChecked);
            } else if (position == 8) {
                holder.swTitle.setText("Пользователь верифицирован");
                if (StaticHelper.showVerified) holder.sw.setChecked(true);
                holder.sw.setOnCheckedChangeListener((buttonView, isChecked) -> StaticHelper.showVerified = isChecked);
            } else if (position == 9) {
                holder.iTitle.setText("Мои интересы");
                holder.iImg.setImageResource(R.drawable.interests);
                getInterests(options);
                multiple = true;
            } else if (position == 10) {
                holder.iTitle.setText("Знак зодиака");
                holder.iImg.setImageResource(R.drawable.zodiac);
                options.add("Овен");
                options.add("Телец");
                options.add("Близнецы");
                options.add("Рак");
                options.add("Лев");
                options.add("Стрелец");
                options.add("Весы");
                options.add("Дева");
                options.add("Скорпион");
                options.add("Володей");
                options.add("Рыбы");
                options.add("Козерог");
            } else if (position == 11) {
                holder.iTitle.setText("Я ищу");
                holder.iImg.setImageResource(R.drawable.search);
                options.add("Долгосрочного партнёра");
                options.add("Долго- или краткосрочного партнёра");
                options.add("Просто повеселиться");
                options.add("Найти друзей");
                options.add("Не опредилился(ась)");
            } else if (position == 12) {
                holder.iTitle.setText("Образование");
                holder.iImg.setImageResource(R.drawable.education);
            } else if (position == 13) {
                holder.iTitle.setText("Планы на семью");
                holder.iImg.setImageResource(R.drawable.family);
            } else if (position == 14) {
                holder.iTitle.setText("Спорт");
                holder.iImg.setImageResource(R.drawable.sport);
            } else if (position == 15) {
                holder.iTitle.setText("Алкоголь");
                holder.iImg.setImageResource(R.drawable.alcohol);
                options.add("Употребляю");
                options.add("Только в компании");
                options.add("Только по выходным");
                options.add("Редко");
                options.add("Не употребляю");
            } else if (position == 16) {
                holder.iTitle.setText("Как часто ты куришь?");
                holder.iImg.setImageResource(R.drawable.smoke);
                options.add("Курю");
                options.add("Только в компании");
                options.add("Редко");
                options.add("Не курю");
            } else if (position == 17) {
                holder.iTitle.setText("Тип личности");
                holder.iImg.setImageResource(R.drawable.type_per);
            } else if (position == 18) {
                holder.iTitle.setText("Предпочтения в еде");
                holder.iImg.setImageResource(R.drawable.food);
            } else if (position == 19) {
                holder.iTitle.setText("Питомцы");
                holder.iImg.setImageResource(R.drawable.pets);
            } else if (position == 20) {
                holder.iTitle.setText("Стиль общения");
                holder.iImg.setImageResource(R.drawable.talk_style);
            } else if (position == 21) {
                holder.iTitle.setText("Язык любви");
                holder.iImg.setImageResource(R.drawable.love_lang);
            }

            if (position > 8) {
                boolean finalMultiple = multiple;
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog
                            = getBottomSheetDialog(holder.iTitle.getText().toString(), options, finalMultiple);
                    bottomSheetDialog.show();
                });
            }
        }

        private void getInterests(List<String> options) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_INTERESTS,
                    response -> {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                options.add(Encrypt.decode(array.getJSONObject(i).getString("interest").getBytes(), "0"));
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
                    return new HashMap<>();
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }

        @NonNull
        private BottomSheetDialog getBottomSheetDialog(String text, List<String> options, boolean multiple) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
            bottomSheetDialog.setContentView(R.layout.alert_extra);
            Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.7f);

            TextView textView = bottomSheetDialog.findViewById(R.id.text);
            RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

            String[] spl = getStrings(text);
            List<Boolean> optionsSelected = new ArrayList<>();
            for (int i = 0; i < options.size(); i++) {
                optionsSelected.add(false);
                for (String s : spl) {
                    if (options.get(i).equals(s)) {
                        optionsSelected.set(i, true);
                    }
                }
            }
            List<String> sel = new ArrayList<>();

            bottomSheetDialog.setOnDismissListener(arg -> {
                String qwe = String.join("&", sel);
                switch (text) {
                    case "Мои интересы":
                        StaticHelper.filterUser.setInterests(qwe);
                        break;
                    case "Знак зодиака":
                        StaticHelper.filterUser.setZodiacSign(qwe);
                        break;
                    case "Я ищу":
                        StaticHelper.filterUser.setRelationshipGoals(qwe);
                        break;
                    case "Алкоголь":
                        StaticHelper.filterUser.setAlcohol(qwe);
                        break;
                    case "Как часто ты куришь?":
                        StaticHelper.filterUser.setSmoke(qwe);
                        break;
                    default:
                        break;
                }
                Prefs.saveFilter(context, StaticHelper.filterUser);
            });

            if (textView != null) textView.setText(text);
            if (recSelect != null) {
                recSelect.setHasFixedSize(true);
                recSelect.setLayoutManager(new LinearLayoutManager(context));
                recSelect.setItemViewCacheSize(options.size());

                SelectAdapter selectAdapter = new SelectAdapter(context, options, optionsSelected, sel, multiple, text, bottomSheetDialog);
                selectAdapter.setHasStableIds(true);
                recSelect.setAdapter(selectAdapter);
            }
            return bottomSheetDialog;
        }

        private String[] getStrings(String text) {
            String ooo;
            switch (text) {
                case "Мои интересы":
                    ooo = StaticHelper.filterUser.getInterests();
                    break;
                case "Знак зодиака":
                    ooo = StaticHelper.filterUser.getZodiacSign();
                    break;
                case "Я ищу":
                    ooo = StaticHelper.filterUser.getRelationshipGoals();
                    break;
                case "Алкоголь":
                    ooo = StaticHelper.filterUser.getAlcohol();
                    break;
                case "Как часто ты куришь?":
                    ooo = StaticHelper.filterUser.getSmoke();
                    break;
                default:
                    ooo = "";
                    break;
            }
            return ooo.split("&");
        }

        @Override
        public int getItemCount() {
            return 22;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return ITEM_TOP_REL;
            else if (position == 1 || position == 7) return ITEM_SUBSCRIBE;
            else if (position == 2 || position == 4) return ITEM_DOUBLE_SEEK_BAR;
            else if (position == 3 || position == 5 || position == 6
            || position == 8) return ITEM_SWITCH;
            else return ITEM_PARAM_EXTRAS;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView back, subImg, iImg;
            public TextView windowTitle, subTitle, subBp, rsTitle, rsExtraText, swTitle, iTitle;
            public RelativeLayout subBG;
            public RangeSlider rs, rsa;
            @SuppressLint("UseSwitchCompatOrMaterialCode") public Switch sw;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                windowTitle = itemView.findViewById(R.id.text);

                subBG = itemView.findViewById(R.id.rel_sub);
                subImg = itemView.findViewById(R.id.sub_img);
                subTitle = itemView.findViewById(R.id.sub_text);
                subBp = itemView.findViewById(R.id.bp);

                rsTitle = itemView.findViewById(R.id.rs_title);
                rsExtraText = itemView.findViewById(R.id.rs_extra_text);
                rs = itemView.findViewById(R.id.rs);
                rsa = itemView.findViewById(R.id.rs_age);

                swTitle = itemView.findViewById(R.id.sw_title);
                sw = itemView.findViewById(R.id.sw);

                iImg = itemView.findViewById(R.id.image);
                iTitle = itemView.findViewById(R.id.text_title);
            }
        }
    }

    private class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

        private final Context context;
        private final List<String> options;
        private final boolean multiple;
        private final List<ViewHolder> holders;
        public List<Boolean> optionsSelected;
        private final List<String> sel;
        private final String text;
        private final BottomSheetDialog bottomSheetDialog;

        private SelectAdapter(Context context, List<String> options, List<Boolean> optionsSelected, List<String> sel, boolean multiple,
                              String text, BottomSheetDialog bottomSheetDialog) {
            this.context = context;
            this.options = options;
            this.sel = sel;
            this.multiple = multiple;
            this.text = text;
            this.bottomSheetDialog = bottomSheetDialog;
            this.optionsSelected = optionsSelected;
            this.holders = new ArrayList<>();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder viewHolder;
            if (viewType == 0) viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select, parent, false));
            else viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.btn_cont, parent, false));
            holders.add(viewHolder);
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if (options.get(position).isEmpty()) return 1;
            else return 0;
        }

        @Override
        public void onBindViewHolder(@NonNull SelectAdapter.ViewHolder holder, int position) {
            if (!options.get(position).isEmpty()) {
                holder.textOption.setText(options.get(position));
                if (!options.isEmpty()) {
                    if (options.get(0).equals("Овен")) {
                        holder.image.setVisibility(View.VISIBLE);
                        switch (position) {
                            case 0:
                                holder.image.setImageResource(R.drawable.oven);
                                break;
                            case 1:
                                holder.image.setImageResource(R.drawable.telez);
                                break;
                            case 2:
                                holder.image.setImageResource(R.drawable.twins);
                                break;
                            case 3:
                                holder.image.setImageResource(R.drawable.cancer);
                                break;
                            case 4:
                                holder.image.setImageResource(R.drawable.lion);
                                break;
                            case 5:
                                holder.image.setImageResource(R.drawable.strel);
                                break;
                            case 6:
                                holder.image.setImageResource(R.drawable.weight);
                                break;
                            case 7:
                                holder.image.setImageResource(R.drawable.girl);
                                break;
                            case 8:
                                holder.image.setImageResource(R.drawable.scorpion);
                                break;
                            case 9:
                                holder.image.setImageResource(R.drawable.waterley);
                                break;
                            case 10:
                                holder.image.setImageResource(R.drawable.fish);
                                break;
                            case 11:
                                holder.image.setImageResource(R.drawable.koz);
                                break;
                            default:
                                holder.image.setVisibility(View.GONE);
                                break;
                        }
                    }
                }
                if (optionsSelected.get(position)) {
                    holder.select.setImageResource(R.drawable.selected);
                    sel.add(options.get(position));
                }

                holder.itemView.setOnClickListener(v -> {
                    if (multiple) {
                        if (!optionsSelected.get(position)) {
                            if (sel.size() < 5) {
                                holder.select.setImageResource(R.drawable.selected);
                                optionsSelected.set(position, true);
                                sel.add(options.get(position));
                            } else {
                                Toast.makeText(getApplicationContext(), "Нельзя выбрать больше 5 позиций", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            holder.select.setImageResource(R.drawable.not_selected);
                            optionsSelected.set(position, false);

                            String q = options.get(position);
                            for (int a = 0; a < sel.size(); a++) {
                                if (sel.get(a).equals(q)) {
                                    sel.remove(a);
                                    break;
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < options.size() - 1; i++) {
                            optionsSelected.set(i, false);
                            holders.get(i).select.setImageResource(R.drawable.not_selected);
                        }
                        sel.clear();
                        sel.add(options.get(position));
                        optionsSelected.set(position, true);
                        holder.select.setImageResource(R.drawable.selected);
                    }
                });
            } else {
                holder.itemView.setOnClickListener(v -> {
                    List<String> finalOptions = new ArrayList<>();
                    for (int i = 0; i < optionsSelected.size(); i++) {
                        if (optionsSelected.get(i)) finalOptions.add(options.get(i));
                    }
                    String qwe = String.join("&", finalOptions);

                    switch (text) {
                        case "Мои интересы":
                            StaticHelper.filterUser.setInterests(qwe);
                            break;
                        case "Знак зодиака":
                            StaticHelper.filterUser.setZodiacSign(qwe);
                            break;
                        case "Я ищу":
                            StaticHelper.filterUser.setRelationshipGoals(qwe);
                        case "Алкоголь":
                            StaticHelper.filterUser.setAlcohol(qwe);
                            break;
                        case "Как часто ты куришь?":
                            StaticHelper.filterUser.setSmoke(qwe);
                            break;
                        default:
                            break;
                    }
                    Prefs.saveFilter(context, StaticHelper.filterUser);
                    bottomSheetDialog.dismiss();
                });
            }
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public TextView textOption;
            public ImageView select;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                textOption = itemView.findViewById(R.id.text);
                select = itemView.findViewById(R.id.select_btn);
            }
        }
    }
}
