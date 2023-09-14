package com.randez_trying.novel.Activities.Registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.randez_trying.novel.Activities.MainActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ExtraDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_data);

        ImageView back = findViewById(R.id.back);
        RecyclerView recyclerView = findViewById(R.id.rec_extra);
        RelativeLayout cont = findViewById(R.id.btn_cont);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new ExtraDataAdapter(getApplicationContext()));

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        cont.setOnClickListener(v -> {
            StaticHelper.myCredentials.setPersonalId(UUID.randomUUID().toString());
            StaticHelper.me.setPersonalId(StaticHelper.myCredentials.getPersonalId());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                    response -> {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Constants.URL_REGISTER_2,
                                response2 -> {
                                    Prefs.saveMe(getApplicationContext(), StaticHelper.me);
                                    startActivity(new Intent(ExtraDataActivity.this, MainActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                },
                                System.out::println){
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                try {
                                    params.put("personalId", StaticHelper.myCredentials.getPersonalId());
                                    params.put("bDate", Encrypt.encode(StaticHelper.me.getbDate(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("balance", Encrypt.encode(StaticHelper.me.getBalance(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("city", Encrypt.encode(StaticHelper.me.getCity(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("company", Encrypt.encode(StaticHelper.me.getCompany(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("education", Encrypt.encode(StaticHelper.me.getEducation(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("growth", Encrypt.encode(StaticHelper.me.getGrowth(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("interests", Encrypt.encode(StaticHelper.me.getInterests(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("job", Encrypt.encode(StaticHelper.me.getJob(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("languages", Encrypt.encode(StaticHelper.me.getLanguages(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("mediaLinks", Encrypt.encode(StaticHelper.me.getMediaLinks(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("name", Encrypt.encode(StaticHelper.me.getName(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("gender", Encrypt.encode(StaticHelper.me.getGender(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("about", Encrypt.encode(StaticHelper.me.getAbout(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("orientation", Encrypt.encode(StaticHelper.me.getOrientation(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("familyPlans", Encrypt.encode(StaticHelper.me.getFamilyPlans(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("sports", Encrypt.encode(StaticHelper.me.getSports(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("alcohol", Encrypt.encode(StaticHelper.me.getAlcohol(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("smoke", Encrypt.encode(StaticHelper.me.getSmoke(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("personalityType", Encrypt.encode(StaticHelper.me.getPersonalityType(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("socialMediaLinks", Encrypt.encode(StaticHelper.me.getSocialMediaLinks(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("status", Encrypt.encode(StaticHelper.me.getStatus(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("subscriptionType", Encrypt.encode(StaticHelper.me.getSubscriptionType(), StaticHelper.myCredentials.getPersonalId()));
                                    params.put("zodiacSign", Encrypt.encode(StaticHelper.me.getZodiacSign(), StaticHelper.myCredentials.getPersonalId()));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                return params;
                            }
                        };

                        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest2);
                    },
                    System.out::println){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", StaticHelper.myCredentials.getEmail());
                    params.put("isActive", "true");
                    params.put("isBanned", "false");
                    params.put("personalId", StaticHelper.myCredentials.getPersonalId());
                    params.put("signUpDate", String.valueOf(System.currentTimeMillis()));
                    return params;
                }
            };

            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        });
    }

    @NonNull
    private BottomSheetDialog getBottomSheetDialog(String text, List<String> options, boolean multiple) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ExtraDataActivity.this);
        bottomSheetDialog.setContentView(R.layout.alert_extra);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.7f);

        TextView textView = bottomSheetDialog.findViewById(R.id.text);
        RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);
        RelativeLayout contDialog = bottomSheetDialog.findViewById(R.id.btn_cont);

        SelectAdapter selectAdapter = new SelectAdapter(getApplicationContext(), options, multiple);

        if (textView != null) textView.setText(text);
        if (recSelect != null) {
            recSelect.setHasFixedSize(true);
            recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recSelect.setAdapter(selectAdapter);
        }
        if (contDialog != null) {
            contDialog.setOnClickListener(v -> {
                List<Boolean> optionsSelected = selectAdapter.optionsSelected;
                List<String> finalOptions = new ArrayList<>();
                for (int i = 0; i < optionsSelected.size(); i++) {
                    if (optionsSelected.get(i)) finalOptions.add(options.get(i));
                }
                String qwe = String.join("&", finalOptions);

                switch (text) {
                    case "Мои интересы":
                        StaticHelper.me.setInterests(qwe);
                        break;
                    case "Знак зодиака":
                        StaticHelper.me.setZodiacSign(qwe);
                        break;
                    case "Я ищу":
                        StaticHelper.me.setOrientation(qwe);
                    case "Алкоголь":
                        StaticHelper.me.setAlcohol(qwe);
                        break;
                    case "Как часто ты куришь?":
                        StaticHelper.me.setSmoke(qwe);
                        break;
                    default:
                        break;
                }

                bottomSheetDialog.dismiss();
            });
        }
        return bottomSheetDialog;
    }

    private static class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

        private final Context context;
        private final List<String> options;
        private final boolean multiple;
        private final List<ViewHolder> holders;
        public List<Boolean> optionsSelected;

        private SelectAdapter(Context context, List<String> options, boolean multiple) {
            this.context = context;
            this.options = options;
            this.multiple = multiple;

            this.optionsSelected = new ArrayList<>();
            for (int i = 0; i < options.size(); i++) optionsSelected.add(false);
            this.holders = new ArrayList<>();
        }

        @NonNull
        @Override
        public SelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select, parent, false));
            holders.add(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SelectAdapter.ViewHolder holder, int position) {
            holder.textOption.setText(options.get(position));
            if (optionsSelected.get(position)) holder.select.setImageResource(R.drawable.selected);

            holder.itemView.setOnClickListener(v -> {
                if (multiple) {
                    if (!optionsSelected.get(position)) {
                        holder.select.setImageResource(R.drawable.selected);
                        optionsSelected.set(position, true);
                    } else {
                        holder.select.setImageResource(R.drawable.not_selected);
                        optionsSelected.set(position, false);
                    }
                } else {
                    for (int i = 0; i < options.size(); i++) {
                        optionsSelected.set(i, false);
                        holders.get(i).select.setImageResource(R.drawable.not_selected);
                    }
                    optionsSelected.set(position, true);
                    holder.select.setImageResource(R.drawable.selected);
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textOption;
            public ImageView select;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textOption = itemView.findViewById(R.id.text);
                select = itemView.findViewById(R.id.select_btn);
            }
        }
    }

    private class ExtraDataAdapter extends RecyclerView.Adapter<ExtraDataAdapter.ViewHolder> {

        private final Context context;

        public ExtraDataAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_interest, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int drawable;
            String text;
            List<String> options = new ArrayList<>();
            boolean multiple;
            switch (position) {
                case 0:
                    drawable = R.drawable.interests;
                    text = "Мои интересы";
                    options.add("Netflix");
                    options.add("Stand Up");
                    options.add("Harry Potter");
                    options.add("Вкусная еда");
                    options.add("Счастливые часы");
                    multiple = true;
                    break;
                case 1:
                    drawable = R.drawable.zodiac;
                    text = "Знак зодиака";
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
                    multiple = false;
                    break;
                case 2:
                    drawable = R.drawable.search;
                    text = "Я ищу";
                    options.add("Долгосрочного партнёра");
                    options.add("Долго- или краткосрочного партнёра");
                    options.add("Просто повеселиться");
                    options.add("Найти друзей");
                    options.add("Не опредилился(ась)");
                    multiple = false;
                    break;
                case 3:
                    drawable = R.drawable.education;
                    text = "Образование";
                    multiple = false;
                    break;
                case 4:
                    drawable = R.drawable.family;
                    text = "Планы на семью";
                    multiple = false;
                    break;
                case 5:
                    drawable = R.drawable.sport;
                    text = "Спорт";
                    multiple = false;
                    break;
                case 6:
                    drawable = R.drawable.alcohol;
                    text = "Алкоголь";
                    options.add("Употребляю");
                    options.add("Только в компании");
                    options.add("Только по выходным");
                    options.add("Редко");
                    options.add("Не употребляю");
                    multiple = false;
                    break;
                case 7:
                    drawable = R.drawable.smoke;
                    text = "Как часто ты куришь?";
                    options.add("Курю");
                    options.add("Только в компании");
                    options.add("Редко");
                    options.add("Не курю");
                    multiple = false;
                    break;
                default:
                    drawable = R.drawable.grey_circle;
                    text = "";
                    multiple = false;
                    break;
            }
            holder.image.setImageResource(drawable);
            holder.text.setText(text);
            String finalText = text;
            boolean finalMultiple = multiple;
            holder.itemView.setOnClickListener(v -> openDialog(finalText, options, finalMultiple));
        }

        public void openDialog(String text, List<String> options, boolean multiple) {
            final BottomSheetDialog bottomSheetDialog = getBottomSheetDialog(text, options, multiple);
            bottomSheetDialog.show();
        }

        @Override
        public int getItemCount() {
            return 8;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public TextView text;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                text = itemView.findViewById(R.id.text);
            }
        }
    }
}