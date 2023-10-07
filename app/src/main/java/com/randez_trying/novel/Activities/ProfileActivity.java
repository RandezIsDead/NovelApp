package com.randez_trying.novel.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.TagView.TagContainerLayout;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        User user = new Gson().fromJson(getIntent().getStringExtra("user"), new TypeToken<User>() {}.getType());

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        PreviewAdapter adapter = new PreviewAdapter(getApplicationContext(), this, user);
        adapter.setHasStableIds(true);
        recyclerView.setItemViewCacheSize(6);
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

    private static class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
        private final Context context;
        private final Activity activity;
        private final User user;
        private PreviewAdapter(Context context, Activity activity, User user) {
            this.context = context;
            this.activity = activity;
            this.user = user;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_pager, parent, false));
            else
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_preview_user_info, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                holder.vp.setAdapter(new VPAdapter(context, Arrays.asList(user.getMediaLinks().split("&"))));

                holder.back.setOnClickListener(v -> {
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    activity.finish();
                });
            } else if (position == 1) {
                holder.nameAge.setText(user.getName() + ", " + StaticHelper.getAge(user.getbDate()));
                holder.iSearch.setText(user.getRelationshipGoals());

                if (!user.getLocation().isEmpty()
                        && !StaticHelper.me.getLocation().isEmpty())
                    holder.distance.setText(StaticHelper.getDistance(user.getLocation()) + " км от вас");
                else holder.distance.setText(user.getCity());

                holder.aboutMe.setText(user.getAbout());

                if (!user.getJob().isEmpty()) {
                    holder.myJob.setText(user.getJob());
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
                if (!mains.get(0).isEmpty()) holder.mainTags.setTags(mains);
                else {
                    holder.sepMain.setVisibility(View.GONE);
                    holder.titleMains.setVisibility(View.GONE);
                    holder.mainTags.setVisibility(View.GONE);
                }

                String[] languages = user.getLanguages().split("&");
                if (!languages[0].isEmpty()) holder.langTags.setTags(languages);
                else {
                    holder.sepLang.setVisibility(View.GONE);
                    holder.titleLanguages.setVisibility(View.GONE);
                    holder.langTags.setVisibility(View.GONE);
                }

                String[] interests = user.getInterests().split("&");
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
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewPager2 vp;
            public ImageView back;
            public TextView aboutMe, nameAge, iSearch, myJob, distance;
            public TextView titleMains, titleLanguages, titleInterests, titleSports, titlePets;
            public TagContainerLayout mainTags, langTags, interestsTags, sportsTag, petsTags;
            public RelativeLayout sepMain, sepLang, sepInt, sepSport, sepPets;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
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