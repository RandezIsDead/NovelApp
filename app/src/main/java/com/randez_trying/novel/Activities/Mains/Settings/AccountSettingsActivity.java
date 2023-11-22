package com.randez_trying.novel.Activities.Mains.Settings;

import static android.content.Intent.ACTION_GET_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.TagView.TagContainerLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatActivity {

    private List<String> photoUrls;
    private RecyclerView recyclerView;
    private final Handler redactHandler = new Handler();
    private final Handler previewHandler = new Handler();
    private final Handler redactChangeHandler = new Handler();
    private boolean isPreviewSelected = false;
    private boolean notify = false;

    private RedactAdapter redactAdapter;
    private PreviewAdapter previewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);
        photoUrls = new LinkedList<>(Arrays.asList(StaticHelper.me.getMediaLinks().split("&")));

        recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        redactAdapter = new RedactAdapter(getApplicationContext(), this);
        redactAdapter.setHasStableIds(true);
        previewAdapter = new PreviewAdapter(getApplicationContext(), this);

        recyclerView.setItemViewCacheSize(24);
        recyclerView.setAdapter(redactAdapter);

        checkFromRedToPrev();
        changesHandler();
    }

    private void checkFromRedToPrev() {
        Runnable runnable = this::checkFromRedToPrev;
        if (isPreviewSelected) {
            recyclerView.setAdapter(previewAdapter);
            redactHandler.removeCallbacks(runnable);
            checkFromPrevToRed();
        } else redactHandler.postDelayed(runnable, 10);
    }

    private void checkFromPrevToRed() {
        Runnable runnable = this::checkFromPrevToRed;
        if (!isPreviewSelected) {
            recyclerView.setItemViewCacheSize(24);
            recyclerView.setAdapter(redactAdapter);
            previewHandler.removeCallbacks(runnable);
            checkFromRedToPrev();
        } else previewHandler.postDelayed(runnable, 10);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void changesHandler() {
        Runnable runnable = this::changesHandler;
        if (notify && !isPreviewSelected) {
            Prefs.saveMe(getApplicationContext(), StaticHelper.me);
            redactAdapter.notifyDataSetChanged();
            notify = false;
        }

        redactChangeHandler.postDelayed(runnable, 10);
    }

    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> getContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null) {
                    Intent data = result.getData();
                    List<Uri> uris = new ArrayList<>();
                    if (data != null) {
                        if (data.getClipData() != null) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                uris.add(data.getClipData().getItemAt(i).getUri());
                            }
                        } else if (data.getData() != null) uris.add(data.getData());
                    }

                    List<String> encodings = new ArrayList<>();

                    try {
                        for (int i = 0; i < uris.size(); i++) {
                            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uris.get(i));
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            encodings.add(Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < encodings.size(); i++) {
                        if (i < (4 - photoUrls.size())) {
                            photoUrls.add("loading");
                            redactAdapter.notifyDataSetChanged();
                            StringRequest stringRequest = getStringRequest(encodings.get(i));
                            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        }
                    }
                }
            });

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    private StringRequest getStringRequest(String encode) {
        return new StringRequest(Request.Method.POST, Constants.URL_UPLOAD_IMAGE,
                response -> {
                    for (int i = 0; i < photoUrls.size(); i++) {
                        if (photoUrls.get(i).equals("loading")) {
                            photoUrls.set(i, Constants.ROOT_URL + response);
                            StaticHelper.me.setMediaLinks(String.join("&", photoUrls));
                            StaticHelper.updateUser(getApplicationContext());
                            redactAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", encode);
                return params;
            }
        };
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

    private class RedactAdapter extends RecyclerView.Adapter<RedactAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;

        private RedactAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public RedactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account_settings, parent, false));
            else if (viewType == 1) {
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_red_mult, parent, false));
            } else if (viewType == 5) {
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_red_one, parent, false));
            } else return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_red_point, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RedactAdapter.ViewHolder holder, int position) {
            if (position == 0) {
                holder.back.setOnClickListener(v -> {
                    activity.finish();
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                });
                holder.redact.setOnClickListener(v -> {
                    holder.redact.setTextColor(getColor(R.color.light_grey));
                    holder.redact.setBackgroundResource(R.drawable.btn_grad);
                    holder.preview.setTextColor(getColor(R.color.cool));
                    holder.preview.setBackgroundResource(0);
                    isPreviewSelected = false;
                });
                holder.preview.setOnClickListener(v -> {
                    holder.preview.setTextColor(getColor(R.color.light_grey));
                    holder.preview.setBackgroundResource(R.drawable.btn_grad);
                    holder.redact.setTextColor(getColor(R.color.cool));
                    holder.redact.setBackgroundResource(0);
                    isPreviewSelected = true;
                });
            } else if (position == 1) {
                holder.title.setText("Медиафайлы");
                holder.relInfoUser.setVisibility(View.GONE);
                holder.recImages.setVisibility(View.VISIBLE);
                holder.recImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.recImages.setHasFixedSize(true);
                holder.recImages.setAdapter(new PhotosAdapter(context));
            } else if (position == 2) {
                holder.title.setText("Обо мне");
                holder.content.setText(StaticHelper.me.getAbout());
                holder.img.setImageResource(R.drawable.edit);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("in");
                    options.add("");

                    if (textView != null ) textView.setText("Обо мне");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 3) {
                holder.title.setText("Рост");
                holder.content.setText(StaticHelper.me.getGrowth().isEmpty() ? "Добавить" : StaticHelper.me.getGrowth());
                holder.img.setImageResource(R.drawable.edit);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("thumb");
                    options.add("");

                    if (textView != null ) textView.setText("Рост");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                StaticHelper.me.getGrowth(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 4) {
                holder.title.setText("Мои интересы");
                String iR = StaticHelper.me.getInterests().replace("&", ", ");
                holder.content.setText(iR.isEmpty() ? "Добавить" : iR);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>(StaticHelper.interests);
                    options.add("");

                    boolean multiple = true;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setInterests(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 5) {
                holder.title.setText("Цели");
                holder.content.setText(StaticHelper.me.getRelationshipGoals().isEmpty() ? "Добавить" : StaticHelper.me.getRelationshipGoals());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("Долгосрочного партнёра");
                    options.add("Долго- или краткосрочного партнёра");
                    options.add("Просто повеселиться");
                    options.add("Найти друзей");
                    options.add("Не опредилился(ась)");
                    options.add("");

                    boolean multiple = false;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setRelationshipGoals(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 6) {
                holder.title.setText("Языки");
                String sR = StaticHelper.me.getLanguages().replace("&", ", ");
                holder.content.setText(sR.isEmpty() ? "Добавить" : sR);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>(StaticHelper.languages);
                    options.add("");

                    boolean multiple = true;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setLanguages(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 7) {
                holder.tit.setText("Основное");//TODO
                holder.co1.setText("Знак зодиака:\n" + (StaticHelper.me.getZodiacSign().isEmpty() ? "Добавить" : StaticHelper.me.getZodiacSign()));
                holder.co2.setText("Тип личности:\n" + (StaticHelper.me.getPersonalityType().isEmpty() ? "Добавить" : StaticHelper.me.getPersonalityType()));
                holder.co3.setText("Стиль общения:\n" + (StaticHelper.me.getTalkStyle().isEmpty() ? "Добавить" : StaticHelper.me.getTalkStyle()));
                holder.rei4.setVisibility(View.GONE);

                holder.co1.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
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
                    options.add("");

                    boolean multiple = false;

                    String[] spl = getStrings("Знак зодиака");
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
                        StaticHelper.me.setZodiacSign(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.tit.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.tit.getText().toString(),
                                "Знак зодиака", bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 8) {
                holder.title.setText("Образование");
                holder.content.setText(StaticHelper.me.getEducation().isEmpty() ? "Добавить" : StaticHelper.me.getEducation());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("in");
                    options.add("");

                    if (textView != null ) textView.setText("Образование");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                holder.content.getText().toString(),  bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 9) {
                holder.title.setText("Должность");
                holder.content.setText(StaticHelper.me.getJob().isEmpty() ? "Добавить" : StaticHelper.me.getJob());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("in");
                    options.add("");

                    if (textView != null ) textView.setText("Должность");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                holder.content.getText().toString(),  bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 10) {
                holder.title.setText("Компания");
                holder.content.setText(StaticHelper.me.getCompany().isEmpty() ? "Добавить" : StaticHelper.me.getCompany());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("in");
                    options.add("");

                    if (textView != null ) textView.setText("Компания");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                holder.content.getText().toString(),  bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 11) {
                holder.title.setText("Спорт");
                String sR = StaticHelper.me.getSports().replace("&", ", ");
                holder.content.setText(sR.isEmpty() ? "Добавить" : sR);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>(StaticHelper.sports);
                    options.add("");

                    boolean multiple = true;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setSports(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 12) {
                holder.title.setText("Живу в городе");//TODO list of cities
                holder.content.setText(StaticHelper.me.getCity().isEmpty() ? "Добавить" : StaticHelper.me.getCity());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("in");
                    options.add("");

                    if (textView != null ) textView.setText("Живу в городе");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                holder.content.getText().toString(),  bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 13) {
                holder.title.setText("Привязать соцсети");//TODO
                holder.content.setText(StaticHelper.me.getSocialMediaLinks().isEmpty() ? "Добавить" : StaticHelper.me.getSocialMediaLinks());
            } else if (position == 14) {
                holder.title.setText("Добавить плейлист");
                holder.content.setText(StaticHelper.me.getPlaylist().isEmpty() ? "Добавить" : StaticHelper.me.getPlaylist());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("in");
                    options.add("");

                    if (textView != null ) textView.setText("Добавить плейлист");
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(2);

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                null, null, false, holder.title.getText().toString(),
                                holder.content.getText().toString(),  bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 15) {
                holder.title.setText("Пол");
                holder.content.setText(StaticHelper.me.getGender().isEmpty() ? "Добавить" : StaticHelper.me.getGender());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("Мужчина");
                    options.add("Женщина");
                    options.add("");

                    boolean multiple = false;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        String qwe = String.join("", sel);
                        StaticHelper.me.setGender(qwe);
                        notify = true;
                    });

                    if (textView != null) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 16) {
                holder.title.setText("Язык любви");
                holder.content.setText(StaticHelper.me.getLoveLang().isEmpty() ? "Добавить" : StaticHelper.me.getLoveLang());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();

                    options.add("");

                    boolean multiple = false;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setLoveLang(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 17) {
                holder.title.setText("Питомцы");
                String pR = StaticHelper.me.getPets().replace("&", ", ");
                holder.content.setText(pR.isEmpty() ? "Добавить" : pR);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>(StaticHelper.pets);
                    options.add("");

                    boolean multiple = true;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setPets(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 18) {
                holder.title.setText("Предпочтения в еде");
                String fR = StaticHelper.me.getFood().replace("&", ", ");
                holder.content.setText(fR.isEmpty() ? "Добавить" : fR);
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>(StaticHelper.food);
                    options.add("");

                    boolean multiple = true;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setFood(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 19) {
                holder.title.setText("Алкоголь");
                holder.content.setText(StaticHelper.me.getAlcohol().isEmpty() ? "Добавить" : StaticHelper.me.getAlcohol());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("Употребляю");
                    options.add("Только в компании");
                    options.add("Только по выходным");
                    options.add("Редко");
                    options.add("Не употребляю");
                    options.add("");

                    boolean multiple = false;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setAlcohol(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 20) {
                holder.title.setText("Как часто ты куришь?");
                holder.content.setText(StaticHelper.me.getSmoke().isEmpty() ? "Добавить" : StaticHelper.me.getSmoke());
                holder.itemView.setOnClickListener(v -> {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountSettingsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.alert_extra);

                    TextView textView = bottomSheetDialog.findViewById(R.id.text);
                    RecyclerView recSelect = bottomSheetDialog.findViewById(R.id.rec_select);

                    List<String> options = new ArrayList<>();
                    options.add("Курю");
                    options.add("Только в компании");
                    options.add("Редко");
                    options.add("Не курю");
                    options.add("");

                    boolean multiple = false;

                    String[] spl = getStrings(holder.title.getText().toString());
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
                        StaticHelper.me.setSmoke(qwe);
                        notify = true;
                    });

                    if (textView != null ) textView.setText(holder.title.getText().toString());
                    if (recSelect != null) {
                        recSelect.setHasFixedSize(true);
                        recSelect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recSelect.setItemViewCacheSize(options.size());

                        SelectAdapter selectAdapter = new SelectAdapter(context, options,
                                optionsSelected, sel, multiple, holder.title.getText().toString(),
                                holder.content.getText().toString(), bottomSheetDialog);
                        selectAdapter.setHasStableIds(true);
                        recSelect.setAdapter(selectAdapter);
                    }

                    bottomSheetDialog.show();
                });
            } else if (position == 21) {
                holder.tit.setText("Настройки аккаунта");//TODO
                holder.co1.setText("Номер телефона: ");
                holder.co2.setText("Email: " + StaticHelper.myCredentials.getEmail());
                holder.rei3.setVisibility(View.GONE);
                holder.rei4.setVisibility(View.GONE);
            } else if (position == 22) {
                holder.tit.setText("Юридическая информация");
                holder.co1.setText("Лицензия: ");
                holder.co2.setText("Условия обслуживания: ");
                holder.co3.setText("Настройки конфиденциальности: ");
                holder.co4.setText("Политика конфиденциальности: ");//TODO
            } else if (position == 23) {
                holder.title.setText("Ночной режим");
                holder.img.setVisibility(View.GONE);
                holder.sw.setVisibility(View.VISIBLE);
            } else if (position == 24) {
                holder.title.setText("Заморозить аккаунт");//TODO
            } else if (position == 25) {
                holder.title.setText("Удалить аккаунт");//TODO

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources()
                                .getDisplayMetrics());
                params.setMargins(px, px*2, px, px);
                holder.relOneIdk.setLayoutParams(params);
            }
        }

        private String[] getStrings(String text) {
            String ooo;
            switch (text) {
                case "Мои интересы":
                    ooo = StaticHelper.me.getInterests();
                    break;
                case "Языки":
                    ooo = StaticHelper.me.getLanguages();
                    break;
                case "Спорт":
                    ooo = StaticHelper.me.getSports();
                    break;
                case "Питомцы":
                    ooo = StaticHelper.me.getPets();
                    break;
                case "Предпочтения в еде":
                    ooo = StaticHelper.me.getFood();
                    break;
                case "Пол":
                    ooo = StaticHelper.me.getGender();
                    break;
                case "Знак зодиака":
                    ooo = StaticHelper.me.getZodiacSign();
                    break;
                case "Цели":
                    ooo = StaticHelper.me.getRelationshipGoals();
                    break;
                case "Алкоголь":
                    ooo = StaticHelper.me.getAlcohol();
                    break;
                case "Как часто ты куришь?":
                    ooo = StaticHelper.me.getSmoke();
                    break;
                default:
                    ooo = "";
                    break;
            }
            return ooo.split("&");
        }

        @Override
        public int getItemCount() {
            return 26;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return 0;
            else if (position == 7 || position == 21 || position == 22) return 1;
            else if (position == 23 || position == 24 || position == 25) {
                return 5;
            } else return 2;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView back, img;
            public TextView redact, preview, title, content;
            public RecyclerView recView, recImages;
            public RelativeLayout relInfoUser, rei1, rei2, rei3, rei4, relOneIdk;
            public TextView tit, co1, co2, co3, co4;
            public ImageView im1, im2, im3, im4;
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            public Switch sw;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                redact = itemView.findViewById(R.id.redact);
                preview = itemView.findViewById(R.id.preview);
                recView = itemView.findViewById(R.id.rec_view);

                title = itemView.findViewById(R.id.text_title);
                relInfoUser = itemView.findViewById(R.id.rel_info_user);
                content = itemView.findViewById(R.id.text_content);
                recImages = itemView.findViewById(R.id.rec_images_redact);
                img = itemView.findViewById(R.id.img_src);

                rei1 = itemView.findViewById(R.id.rel_i1);
                rei2 = itemView.findViewById(R.id.rel_i2);
                rei3 = itemView.findViewById(R.id.rel_i3);
                rei4 = itemView.findViewById(R.id.rel_i4);

                relOneIdk = itemView.findViewById(R.id.rel_one_idk);

                tit = itemView.findViewById(R.id.tit);
                co1 = itemView.findViewById(R.id.text_content1);
                co2 = itemView.findViewById(R.id.text_content2);
                co3 = itemView.findViewById(R.id.text_content3);
                co4 = itemView.findViewById(R.id.text_content4);

                im1 = itemView.findViewById(R.id.img_src1);
                im2 = itemView.findViewById(R.id.img_src2);
                im3 = itemView.findViewById(R.id.img_src3);
                im4 = itemView.findViewById(R.id.img_src4);

                sw = itemView.findViewById(R.id.sw);
            }
        }
    }

    private class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

        private final Context context;

        public PhotosAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false));
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position < photoUrls.size()) {
                if (photoUrls.get(position).equals("loading")) {
                    holder.photo.setImageResource(0);
                    holder.loading.setVisibility(View.VISIBLE);
                } else {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(10));
                    Glide.with(context).load(photoUrls.get(holder.getAdapterPosition()))
                            .apply(requestOptions).into(holder.photo);
                    holder.rm.setVisibility(View.VISIBLE);
                }
            }
            if (position == 0) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources()
                                .getDisplayMetrics());
                int pxR = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources()
                                .getDisplayMetrics());
                params.setMargins(px, 0, pxR, 0);
                holder.relImage.setLayoutParams(params);
            }
            holder.rm.setOnClickListener(v -> {
                if (photoUrls.size() > 1) {
                    photoUrls.remove(holder.getAdapterPosition());
                    StaticHelper.me.setMediaLinks(String.join("&", photoUrls));
                    StaticHelper.updateUser(getApplicationContext());
                    notify = true;
                } else {
                    Snackbar.make(v, "У вас должно быть хотя бы одно фото", Snackbar.LENGTH_SHORT).show();
                }
            });
            holder.photo.setOnClickListener(v -> {
                if (holder.getAdapterPosition() > photoUrls.size() - 1) {
                    Intent intent = new Intent(ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("*/*");
                    getContent.launch(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView photo, rm;
            public ProgressBar loading;
            public RelativeLayout relImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                photo = itemView.findViewById(R.id.photo);
                rm = itemView.findViewById(R.id.rm_photo);
                loading = itemView.findViewById(R.id.loading);
                relImage = itemView.findViewById(R.id.rel_image);
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
        private final String title;
        private final String text;
        private final BottomSheetDialog bottomSheetDialog;
        private String inputString = "";
        private int inputHeight = 150;

        private SelectAdapter(Context context, List<String> options, List<Boolean> optionsSelected, List<String> sel, boolean multiple,
                              String title, String text, BottomSheetDialog bottomSheetDialog) {
            this.context = context;
            this.options = options;
            this.sel = sel;
            this.multiple = multiple;
            this.title = title;
            this.text = text;
            this.bottomSheetDialog = bottomSheetDialog;
            this.optionsSelected = optionsSelected;
            this.holders = new ArrayList<>();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SelectAdapter.ViewHolder viewHolder;
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
                if (options.get(position).equals("in")) {
                    holder.rel.setVisibility(View.GONE);
                    holder.input.setVisibility(View.VISIBLE);
                    if (!text.equals("Добавить")) holder.input.setText(text);

                    holder.input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void afterTextChanged(Editable s) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            inputString = s.toString();
                        }
                    });
                } else if (options.get(position).equals("thumb")) {
                    holder.rel.setVisibility(View.GONE);
                    holder.relSeek.setVisibility(View.VISIBLE);
                    if (!text.isEmpty()) {
                        holder.seekText.setText(text);
                        holder.slider.setValue(Float.parseFloat(text));
                    }

                    holder.slider.addOnChangeListener((slider, value, fromUser) -> {
                        holder.seekText.setText(String.valueOf((int) (value)));
                        inputHeight = (int) (value);
                    });
                } else {
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
                }
            } else {
                holder.itemView.setOnClickListener(v -> {
                    if (options.get(0).equals("in")) {
                        switch (title) {
                            case "Обо мне":
                                StaticHelper.me.setAbout(inputString);
                                break;
                            case "Образование":
                                StaticHelper.me.setEducation(inputString);
                                break;
                            case "Должность":
                                StaticHelper.me.setJob(inputString);
                                break;
                            case "Компания":
                                StaticHelper.me.setCompany(inputString);
                                break;
                            case "Живу в городе":
                                StaticHelper.me.setCity(inputString);
                                break;
                            case "Добавить плейлист":
                                StaticHelper.me.setPlaylist(inputString);
                                break;
                            default:
                                break;
                        }
                    } else if (options.get(0).equals("thumb")) {
                        StaticHelper.me.setGrowth(String.valueOf(inputHeight));
                    } else {
                        List<String> finalOptions = new ArrayList<>();
                        for (int i = 0; i < optionsSelected.size(); i++) {
                            if (optionsSelected.get(i)) finalOptions.add(options.get(i));
                        }
                        String qwe = String.join("&", finalOptions);

                        switch (title) {
                            case "Мои интересы":
                                StaticHelper.me.setInterests(qwe);
                                break;
                            case "Знак зодиака":
                                StaticHelper.me.setZodiacSign(qwe);
                                break;
                            case "Цели":
                                StaticHelper.me.setRelationshipGoals(qwe);
                                break;
                            case "Языки":
                                StaticHelper.me.setLanguages(qwe);
                                break;
                            case "Тип личности":
                                StaticHelper.me.setPersonalityType(qwe);
                                break;
                            case "Спорт":
                                StaticHelper.me.setSports(qwe);
                                break;
                            case "Язык любви":
                                StaticHelper.me.setLoveLang(qwe);
                                break;
                            case "Питомцы":
                                StaticHelper.me.setPets(qwe);
                                break;
                            case "Предпочтения в еде":
                                StaticHelper.me.setFood(qwe);
                                break;
                            case "Алкоголь":
                                StaticHelper.me.setAlcohol(qwe);
                                break;
                            case "Пол":
                                StaticHelper.me.setGender(qwe);
                                break;
                            case "Как часто ты куришь?":
                                StaticHelper.me.setSmoke(qwe);
                                break;
                            default:
                                break;
                        }
                    }
                    notify = true;
                    StaticHelper.updateUser(context);
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
            public TextView textOption, seekText;
            public ImageView select;
            public EditText input;
            public Slider slider;
            public RelativeLayout rel, relSeek;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                textOption = itemView.findViewById(R.id.text);
                select = itemView.findViewById(R.id.select_btn);
                rel = itemView.findViewById(R.id.rel_select);
                input = itemView.findViewById(R.id.input_text);
                relSeek = itemView.findViewById(R.id.rel_seek);
                seekText = itemView.findViewById(R.id.seek_text);
                slider = itemView.findViewById(R.id.slider);
            }
        }
    }

    private class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
        private final Context context;
        private final Activity activity;

        private PreviewAdapter(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @NonNull
        @Override
        public PreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account_settings, parent, false));
            else if (viewType == 1)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_pager, parent, false));
            else
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_preview_user_info, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull PreviewAdapter.ViewHolder holder, int position) {
            if (position == 0) {
                holder.preview.setTextColor(context.getColor(R.color.light_grey));
                holder.preview.setBackgroundResource(R.drawable.btn_grad);
                holder.redact.setTextColor(context.getColor(R.color.cool));
                holder.redact.setBackgroundResource(0);

                holder.back.setOnClickListener(v -> {
                    activity.finish();
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                });
                holder.redact.setOnClickListener(v -> {
                    holder.redact.setTextColor(context.getColor(R.color.light_grey));
                    holder.redact.setBackgroundResource(R.drawable.btn_grad);
                    holder.preview.setTextColor(context.getColor(R.color.cool));
                    holder.preview.setBackgroundResource(0);
                    isPreviewSelected = false;
                });
                holder.preview.setOnClickListener(v -> {
                    holder.preview.setTextColor(context.getColor(R.color.light_grey));
                    holder.preview.setBackgroundResource(R.drawable.btn_grad);
                    holder.redact.setTextColor(context.getColor(R.color.cool));
                    holder.redact.setBackgroundResource(0);
                    isPreviewSelected = true;
                });
            } else if (position == 1) {
                holder.back.setVisibility(View.GONE);
                holder.vp.setAdapter(new VPAdapter(context, photoUrls));

                holder.back.setOnClickListener(v -> {
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    activity.finish();
                });
            } else {
                //TODO info
                holder.nameAge.setText(StaticHelper.me.getName() + ", " + StaticHelper.getAge(StaticHelper.me.getbDate()));
                holder.iSearch.setText(StaticHelper.me.getRelationshipGoals());
                holder.distance.setText("999 км от вас");
                holder.aboutMe.setText(StaticHelper.me.getAbout());

                if (!StaticHelper.me.getJob().isEmpty()) {
                    holder.myJob.setText(StaticHelper.me.getJob());
                } else holder.myJob.setText("Нет");

                holder.mainTags.setTagBackgroundColor(Color.TRANSPARENT);
                holder.langTags.setTagBackgroundColor(Color.TRANSPARENT);
                holder.interestsTags.setTagBackgroundColor(Color.TRANSPARENT);

                List<String> mains = new LinkedList<>(Arrays.asList(
                        StaticHelper.me.getZodiacSign(),
                        StaticHelper.me.getRelationshipGoals(),
                        StaticHelper.me.getEducation(),
                        StaticHelper.me.getFamilyPlans(),
                        StaticHelper.me.getAlcohol(),
                        StaticHelper.me.getSmoke(),
                        StaticHelper.me.getPersonalityType(),
                        StaticHelper.me.getTalkStyle(),
                        StaticHelper.me.getLoveLang()));
                for (int i = 0; i < mains.size(); i++) {
                    if (mains.get(i).isEmpty()) {
                        mains.remove(i);
                        i = 0;
                    }
                }
                if (!mains.isEmpty()) holder.mainTags.setTags(mains);
                else {
                    holder.sepMain.setVisibility(View.GONE);
                    holder.titleMains.setVisibility(View.GONE);
                    holder.mainTags.setVisibility(View.GONE);
                }

                String[] languages = StaticHelper.me.getLanguages().split("&");
                if (!languages[0].isEmpty()) holder.langTags.setTags(languages);
                else {
                    holder.sepLang.setVisibility(View.GONE);
                    holder.titleLanguages.setVisibility(View.GONE);
                    holder.langTags.setVisibility(View.GONE);
                }

                String[] interests = StaticHelper.me.getInterests().split("&");
                if (!interests[0].isEmpty()) holder.interestsTags.setTags(interests);
                else {
                    holder.sepInt.setVisibility(View.GONE);
                    holder.titleInterests.setVisibility(View.GONE);
                    holder.interestsTags.setVisibility(View.GONE);
                }

                String[] sports = StaticHelper.me.getSports().split("&");
                if (!sports[0].isEmpty()) holder.sportsTag.setTags(sports);
                else {
                    holder.sepSport.setVisibility(View.GONE);
                    holder.titleSports.setVisibility(View.GONE);
                    holder.sportsTag.setVisibility(View.GONE);
                }

                String[] pets = StaticHelper.me.getPets().split("&");
                if (!pets[0].isEmpty()) holder.petsTags.setTags(pets);
                else {
                    holder.sepPets.setVisibility(View.GONE);
                    holder.titlePets.setVisibility(View.GONE);
                    holder.petsTags.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView back;
            public ViewPager2 vp;
            public TextView redact, preview, aboutMe, nameAge, iSearch, myJob, distance;
            public TextView titleMains, titleLanguages, titleInterests, titleSports, titlePets;
            public TagContainerLayout mainTags, langTags, interestsTags, sportsTag, petsTags;
            public RelativeLayout sepMain, sepLang, sepInt, sepSport, sepPets;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                redact = itemView.findViewById(R.id.redact);
                preview = itemView.findViewById(R.id.preview);
                vp = itemView.findViewById(R.id.vp);

                nameAge = itemView.findViewById(R.id.name);
                aboutMe = itemView.findViewById(R.id.text_content);
                distance = itemView.findViewById(R.id.distance);
                myJob = itemView.findViewById(R.id.job);
                iSearch = itemView.findViewById(R.id.i_search);
                mainTags = itemView.findViewById(R.id.main_tags);
                langTags = itemView.findViewById(R.id.lang_tags);
                interestsTags = itemView.findViewById(R.id.interests_tags);
                sportsTag = itemView.findViewById(R.id.sports_tags);
                petsTags = itemView.findViewById(R.id.pets_tags);

                sepMain = itemView.findViewById(R.id.rel_sep2);
                sepLang = itemView.findViewById(R.id.rel_sep3);
                sepInt = itemView.findViewById(R.id.rel_sep4);
                sepSport = itemView.findViewById(R.id.rel_sep5);
                sepPets = itemView.findViewById(R.id.rel_sep6);

                titleMains = itemView.findViewById(R.id.text_title_m);
                titleLanguages = itemView.findViewById(R.id.text_title_l);
                titleInterests = itemView.findViewById(R.id.text_title_i);
                titleSports = itemView.findViewById(R.id.text_title_s);
                titlePets = itemView.findViewById(R.id.text_title_p);
            }
        }
    }

    private static class VPAdapter extends RecyclerView.Adapter<VPAdapter.ViewHolder> {

        private final Context context;
        private final List<String> urls;

        private VPAdapter(Context context, List<String> urls) {
            this.context = context;
            this.urls = urls;
        }

        @NonNull
        @Override
        public VPAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo_full, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VPAdapter.ViewHolder holder, int position) {
            Glide.with(context).load(urls.get(position)).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
            }
        }
    }
}
