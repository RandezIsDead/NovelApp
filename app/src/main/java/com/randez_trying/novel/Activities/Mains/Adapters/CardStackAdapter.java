package com.randez_trying.novel.Activities.Mains.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.randez_trying.novel.Activities.Mains.ProfileActivity;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

import java.util.Arrays;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<User> list;

    public CardStackAdapter(Context context, Activity activity, List<User> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public CardStackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull CardStackAdapter.ViewHolder holder, int position) {
        if (position < list.size() - 1) {
            holder.name.setText(list.get(position).getName());
            holder.search.setText(list.get(position).getRelationshipGoals());
            if (!list.get(position).getLocation().isEmpty()
            && !StaticHelper.me.getLocation().isEmpty())
                holder.distance.setText(StaticHelper.getDistance(list.get(position).getLocation()) + " км от вас");
            else holder.distance.setText(list.get(position).getCity());

            if (!list.get(position).getJob().isEmpty()) {
                holder.job.setText(list.get(position).getJob());
            } else holder.job.setText("Нет");

            holder.recImages.setAdapter(new PhotosAdapter(context, activity, list.get(position),
                    Arrays.asList(list.get(position).getMediaLinks().split("&"))));
        } else {
            holder.mainRel.setVisibility(View.GONE);
            holder.end.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView mainRel;
        public TextView end;
        public ViewPager2 recImages;
        public TextView name, search, job, distance;
        public ImageView dim;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainRel = itemView.findViewById(R.id.rel_info);
            end = itemView.findViewById(R.id.end);

            dim = itemView.findViewById(R.id.dim);

            recImages = itemView.findViewById(R.id.rec_images);
            name = itemView.findViewById(R.id.name);
            search = itemView.findViewById(R.id.i_search);
            job = itemView.findViewById(R.id.job);
            distance = itemView.findViewById(R.id.distance);
        }
    }

    private static class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final User user;
        private final List<String> urls;
        public PhotosAdapter(Context context, Activity activity, User user, List<String> urls) {
            this.context = context;
            this.activity = activity;
            this.user = user;
            this.urls = urls;
        }

        @NonNull
        @Override
        public PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo_full, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PhotosAdapter.ViewHolder holder, int position) {
            Glide.with(context).load(urls.get(position)).into(holder.photo);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", new Gson().toJson(user));
                intent.putExtra("msg", "false");
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView photo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                photo = itemView.findViewById(R.id.image);
            }
        }
    }
}