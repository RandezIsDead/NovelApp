package com.randez_trying.novel.Activities.PricingActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Models.CreditCard;
import com.randez_trying.novel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static List<CreditCard> cards;
    private static boolean canceledSave = false;
    private int isPay = 0;
    private String whatBought, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);
        int sumInt = 0;
        String sum = getIntent().getStringExtra("sum");
        if (sum != null) sumInt = Integer.parseInt(sum);

        String pa = getIntent().getStringExtra("isPay");
        if (pa != null) isPay = Integer.parseInt(pa);
        System.out.println("IS PAY " + pa);

        whatBought = getIntent().getStringExtra("whatBought");
        count = getIntent().getStringExtra("count");

        cards = new ArrayList<>();

        recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        getCards(isPay, sumInt);
    }

    private void getCards(int isPay, int sum) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_CARDS,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            cards.add(new CreditCard(
                                    jsonObject.getString("number"),
                                    jsonObject.getString("date"),
                                    jsonObject.getString("cvv")
                            ));
                        }
                        recyclerView.setAdapter(new PayAdapter(getApplicationContext(), this, sum));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", Prefs.getMe(getApplicationContext()).getPersonalId());
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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

    private class PayAdapter extends RecyclerView.Adapter<PayAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final int sum;

        public PayAdapter(Context context, Activity activity, int sum) {
            this.context = context;
            this.activity = activity;
            this.sum = sum;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pay_window, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.back.setOnClickListener(v -> {
                activity.finish();
                activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
            });
            if (isPay == 0) holder.windowTitle.setText("Оплата");
            else holder.windowTitle.setText("Вывод");
            holder.cardNum.addTextChangedListener(new FourDigitCardFormatWatcher());
            holder.cardDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0 && (editable.length() % 3) == 0) {
                        final char c = editable.charAt(editable.length() - 1);
                        if ('/' == c) {
                            editable.delete(editable.length() - 1, editable.length());
                        }
                    }
                    if (editable.length() > 0 && (editable.length() % 3) == 0) {
                        char c = editable.charAt(editable.length() - 1);
                        if (Character.isDigit(c) && TextUtils.split(editable.toString(), "/").length <= 2) {
                            editable.insert(editable.length() - 1, "/");
                        }
                    }
                }
            });
            holder.paySum.setText(sum + " ₽");

            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerView.setAdapter(new CardsAdapter(context, activity, cards));

            holder.cont.setOnClickListener(v -> {
                boolean numCorrect = holder.cardNum.getText().toString().trim().length() == 16;
                boolean dateCorrect = holder.cardDate.getText().toString().trim().length() == 5;
                boolean cvvCorrect = holder.cardCVV.getText().toString().trim().length() == 3;
                if (numCorrect && dateCorrect && cvvCorrect) {
                    if (!canceledSave && !isCardSaved(
                            holder.cardNum.getText().toString(),
                            holder.cardDate.getText().toString(),
                            holder.cardCVV.getText().toString()
                    )) {
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
                        bottomSheetDialog.setContentView(R.layout.alert_save_card);
                        Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.7f);

                        RelativeLayout cont = bottomSheetDialog.findViewById(R.id.btn_cont);
                        bottomSheetDialog.show();

                        bottomSheetDialog.setOnDismissListener(dialog -> canceledSave = true);

                        if (cont != null) cont.setOnClickListener(vi -> {
                            saveCard(
                                    vi,
                                    holder.cardNum.getText().toString(),
                                    holder.cardDate.getText().toString(),
                                    holder.cardCVV.getText().toString()
                            );
                            canceledSave = true;
                            bottomSheetDialog.dismiss();
                        });
                    } else {
                        //TODO connect pay system
                        Intent intent = new Intent(context, PayResultActivity.class);
                        intent.putExtra("result", "success");
                        intent.putExtra("isPay", isPay);
                        intent.putExtra("count", String.valueOf(count));
                        intent.putExtra("whatBought", whatBought);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        activity.finish();
                    }
                } else Snackbar.make(v, "Проверьте корректность введённых данных", Snackbar.LENGTH_SHORT).show();
            });
        }

        private boolean isCardSaved(String num, String date, String cvv) {
            for (int i = 0; i < cards.size(); i++) {
                CreditCard card = cards.get(i);
                if (card.getNumber().equals(num) && card.getDate().equals(date) && card.getCvv().equals(cvv))
                    return true;
            }
            return false;
        }

        private void saveCard(View v, String num, String date, String cvv) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADD_CARD,
                    response -> {
                        cards.add(new CreditCard(num, date, cvv));
                        Snackbar.make(v, "Карта сохранена", Snackbar.LENGTH_SHORT).show();
                    },
                    System.out::println){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("personalId", Prefs.getMe(context).getPersonalId());
                    params.put("number", num);
                    params.put("date", date);
                    params.put("cvv", cvv);
                    return params;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView back;
            public TextView windowTitle;
            public EditText cardNum, cardDate, cardCVV, paySum;
            public RelativeLayout cont;
            public RecyclerView recyclerView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                windowTitle = itemView.findViewById(R.id.window_title);
                cardNum = itemView.findViewById(R.id.input_card_number);
                cardDate = itemView.findViewById(R.id.input_card_date);
                cardCVV = itemView.findViewById(R.id.input_card_cvv);
                paySum = itemView.findViewById(R.id.input_sum);
                cont = itemView.findViewById(R.id.btn_cont);
                recyclerView = itemView.findViewById(R.id.rec_cards);
            }
        }
    }

    private static class FourDigitCardFormatWatcher implements TextWatcher {

        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }

    private class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final List<CreditCard> cards;

        private CardsAdapter(Context context, Activity activity, List<CreditCard> cards) {
            this.context = context;
            this.activity = activity;
            this.cards = cards;
        }

        @NonNull
        @Override
        public CardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pay_card, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CardsAdapter.ViewHolder holder, int position) {
            String num = cards.get(position).getNumber();
            holder.number.setText(num.substring(0, 4) + " **** **** " + num.substring(12));
            holder.img.setImageResource(R.drawable.card);

            holder.itemView.setOnClickListener(v -> {
                //TODO connect pay system
                Intent intent = new Intent(context, PayResultActivity.class);
                intent.putExtra("result", "success");
                intent.putExtra("isPay", isPay);
                intent.putExtra("count", String.valueOf(count));
                intent.putExtra("whatBought", whatBought);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                activity.finish();
            });
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView img;
            public TextView number;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.card_img);
                number = itemView.findViewById(R.id.card_num);
            }
        }
    }
}