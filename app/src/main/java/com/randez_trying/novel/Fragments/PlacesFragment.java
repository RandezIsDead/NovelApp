package com.randez_trying.novel.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.randez_trying.novel.Activities.Mains.PlaceActivity;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.Place;
import com.randez_trying.novel.R;

import java.util.Arrays;
import java.util.List;

public class PlacesFragment extends Fragment {

    private boolean isCafesSel = false, isParksSel = false, isRestSel = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rec_view_layout, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CafesAdapter(requireActivity().getApplicationContext(), requireActivity(), StaticHelper.places));
        return root;
    }

    private class CafesAdapter extends RecyclerView.Adapter<CafesAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final List<Place> places;

        public CafesAdapter(Context context, Activity activity, List<Place> places) {
            this.context = context;
            this.activity = activity;
            this.places = places;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cafes_first, parent, false));
            else
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_place, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                StaticHelper.setCoolTextGradient(holder.appName);

                holder.recPlacesOptions.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.recPlacesOptions.setHasFixedSize(true);
                holder.recPlacesOptions.setAdapter(new OptionsAdapter(context, Arrays.asList("Кафе", "Парки", "Рестораны")));
            } else {
                if (places.get(position) != null) {
                    //TODO images
                    Glide.with(context).load(places.get(position).getImage()).placeholder(R.color.other_grey).into(holder.image);
                    holder.placeName.setText(places.get(position).getName());

                    //TODO stars and distance
                    holder.stars.setText("4.5");
                    holder.distance.setText("3" + " км от вас");

                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, PlaceActivity.class);
                        intent.putExtra("place", new Gson().toJson(places.get(position)));
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return places.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (places.get(position) == null) return 0;
            else return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView appName, placeName, stars, distance;
            public RecyclerView recPlacesOptions;
            public ImageView image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                appName = itemView.findViewById(R.id.app_name);
                recPlacesOptions = itemView.findViewById(R.id.rec_places_options);

                image = itemView.findViewById(R.id.image);
                placeName = itemView.findViewById(R.id.name);
                stars = itemView.findViewById(R.id.job);
                distance = itemView.findViewById(R.id.distance);
            }
        }
    }

    private class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

        private final Context context;
        private final List<String> options;

        public OptionsAdapter(Context context, List<String> options) {
            this.context = context;
            this.options = options;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_like_view_option, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.text.setText(options.get(position));

            holder.itemView.setOnClickListener(v -> {
                if (position == 0) {
                    if (!isCafesSel) {
                        holder.rel.setBackgroundResource(R.drawable.btn_cool);
                        holder.text.setTextColor(context.getColor(R.color.light_grey));
                    } else {
                        holder.rel.setBackgroundResource(R.drawable.btn_light_grey);
                        holder.text.setTextColor(context.getColor(R.color.other_grey));
                    }
                    isCafesSel = !isCafesSel;
                } else if (position == 1) {
                    if (!isParksSel) {
                        holder.rel.setBackgroundResource(R.drawable.btn_cool);
                        holder.text.setTextColor(context.getColor(R.color.light_grey));
                    } else {
                        holder.rel.setBackgroundResource(R.drawable.btn_light_grey);
                        holder.text.setTextColor(context.getColor(R.color.other_grey));
                    }
                    isParksSel = !isParksSel;
                } else if (position == 2) {
                    if (!isRestSel) {
                        holder.rel.setBackgroundResource(R.drawable.btn_cool);
                        holder.text.setTextColor(context.getColor(R.color.light_grey));
                    } else {
                        holder.rel.setBackgroundResource(R.drawable.btn_light_grey);
                        holder.text.setTextColor(context.getColor(R.color.other_grey));
                    }
                    isRestSel = !isRestSel;
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
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
}