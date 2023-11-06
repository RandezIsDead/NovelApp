package com.randez_trying.novel.Activities.MainFragments.MatchActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.randez_trying.novel.Activities.ProfileActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMatchesActivity extends AppCompatActivity {

    private int showPos = 0;
    private RecyclerView recLikeOptions, recLikes;
    private List<String> likes;
    private List<String> matches;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_matches);

        likes = new ArrayList<>();
        matches = new ArrayList<>();

        for (int i = 0; i < StaticHelper.matches.size(); i++) {
            for (int j = 0; j < StaticHelper.matches.size(); j++) {
                if (StaticHelper.matches.get(i).getUserOne().equals(StaticHelper.matches.get(j).getUserTwo())
                        && StaticHelper.matches.get(i).getUserTwo().equals(StaticHelper.matches.get(j).getUserOne())) {
                    String add = "";
                    if (StaticHelper.matches.get(i).getUserOne().equals(StaticHelper.me.getPersonalId())) {
                        add = StaticHelper.matches.get(i).getUserTwo();
                    } else if (StaticHelper.matches.get(i).getUserTwo().equals(StaticHelper.me.getPersonalId())) {
                        add = StaticHelper.matches.get(i).getUserOne();
                    }

                    boolean exist = false;
                    for (int k = 0; k < matches.size(); k++) {
                        if (matches.get(k).equals(add)) exist = true;
                    }
                    if (!exist) matches.add(add);
                }
            }
        }

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });

        recLikeOptions = findViewById(R.id.rec_like_options);
        recLikeOptions.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recLikeOptions.setHasFixedSize(true);
        recLikeOptions.setAdapter(new OptionsAdapter(getApplicationContext()));

        TextView countMatches = findViewById(R.id.your_likes);
        countMatches.setText("У вас " + matches.size() +  " взаимных симпатий");

        recLikes = findViewById(R.id.rec_likes);
        recLikes.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recLikes.setHasFixedSize(true);
        recLikes.setAdapter(new LikesAdapter(getApplicationContext(), this, calcLikes()));
    }

    private void reload() {
        recLikeOptions.setAdapter(new OptionsAdapter(getApplicationContext()));
        recLikes.setAdapter(new LikesAdapter(getApplicationContext(), this, calcLikes()));
    }

    private List<String> calcLikes() {
        likes.clear();

        if (showPos == 0) {
            likes.addAll(matches);
        } else if (showPos == 1) {
            for (int i = 0; i < StaticHelper.matches.size(); i++) {
                if (StaticHelper.matches.get(i).getUserOne().equals(StaticHelper.me.getPersonalId())) {
                    boolean isExist = false;
                    for (int j = 0; j < likes.size(); j++) {
                        if (StaticHelper.matches.get(i).getUserTwo().equals(likes.get(j))) isExist = true;
                    }
                    if (!isExist) likes.add(StaticHelper.matches.get(i).getUserTwo());
                }
            }
        } else if (showPos == 2) {
            for (int i = 0; i < StaticHelper.matches.size(); i++) {
                if (StaticHelper.matches.get(i).getUserTwo().equals(StaticHelper.me.getPersonalId())) {
                    boolean isExist = false;
                    for (int j = 0; j < likes.size(); j++) {
                        if (StaticHelper.matches.get(i).getUserOne().equals(likes.get(j))) isExist = true;
                    }
                    if (!isExist) likes.add(StaticHelper.matches.get(i).getUserOne());
                }
            }
        }

        return likes;
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

    private class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

        private final Context context;

        public OptionsAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_like_view_option, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
            if (position == 0) holder.text.setText("Совпадения");
            else if (position == 1) holder.text.setText("Мои лайки");
            else if (position == 2) holder.text.setText("Я понравился");

            if (showPos == position) {
                holder.rel.setBackgroundResource(R.drawable.btn_cool);
                holder.text.setTextColor(context.getColor(R.color.light_grey));
            } else {
                holder.rel.setBackgroundResource(R.drawable.btn_light_grey);
                holder.text.setTextColor(context.getColor(R.color.other_grey));
            }

            holder.itemView.setOnClickListener(v -> {
                showPos = holder.getAdapterPosition();
                reload();
            });
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public RelativeLayout rel;
            public TextView text;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                rel = itemView.findViewById(R.id.rel_bg);
                text = itemView.findViewById(R.id.text_op);
            }
        }
    }

    private class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final List<String> likes;

        public LikesAdapter(Context context, Activity activity, List<String> likes) {
            this.context = context;
            this.activity = activity;
            this.likes = likes;
        }

        @NonNull
        @Override
        public LikesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_like_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LikesAdapter.ViewHolder holder, int position) {
            getUser(holder, likes.get(position));
        }

        private void getUser(ViewHolder holder, String uid) {
            @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_USER,
                    response -> {
                        try {
                            JSONArray o = new JSONArray(response);

                            for (int i = 0; i < o.length(); i++) {
                                JSONObject jsonObject = o.getJSONObject(i);
                                String personalId = jsonObject.getString("personalId");

                                User user = new User(
                                        Encrypt.decode(jsonObject.getString("bDate").getBytes(), personalId),
                                        "",
                                        Encrypt.decode(jsonObject.getString("city").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("company").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("education").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("growth").getBytes(), personalId),
                                        null,
                                        Encrypt.decode(jsonObject.getString("interests").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("job").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("languages").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("mediaLinks").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("name").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("gender").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("about").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("familyPlans").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("relationshipGoals").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("sports").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("alcohol").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("smoke").getBytes(), personalId),
                                        personalId,
                                        Encrypt.decode(jsonObject.getString("personalityType").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("socialMediaLinks").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("status").getBytes(), personalId),
                                        "",
                                        Encrypt.decode(jsonObject.getString("zodiacSign").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("playlist").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("location").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("talkStyle").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("loveLang").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("pets").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("food").getBytes(), personalId),
                                        Encrypt.decode(jsonObject.getString("isOnline").getBytes(), personalId)
                                );

                                RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30));
                                Glide.with(context).load(user.getMediaLinks().split("&")[0]).apply(requestOptions).into(holder.userImg);
                                holder.userNameAge.setText(user.getName() + ", " + StaticHelper.getAge(user.getbDate()));

                                holder.itemView.setOnClickListener(v -> {
                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    intent.putExtra("user", new Gson().toJson(user));

                                    boolean isExist = false;
                                    for (int a = 0; a < matches.size(); a++) {
                                        if (matches.get(a).equals(user.getPersonalId())) {
                                            isExist = true;
                                            break;
                                        }
                                    }
                                    if (isExist) intent.putExtra("msg", "true");
                                    else intent.putExtra("msg", "false");

                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    System.out::println){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("personalId", uid);
                    return params;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }

        @Override
        public int getItemCount() {
            return likes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView userImg;
            public TextView userNameAge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userImg = itemView.findViewById(R.id.user_img);
                userNameAge = itemView.findViewById(R.id.user_name_age);
            }
        }
    }
}