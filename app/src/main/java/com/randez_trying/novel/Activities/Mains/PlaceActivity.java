package com.randez_trying.novel.Activities.Mains;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Models.Place;
import com.randez_trying.novel.R;

public class PlaceActivity extends AppCompatActivity {

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_view_layout);

        place = new Gson().fromJson(getIntent().getStringExtra("place"), new TypeToken<Place>() {}.getType());

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new PlaceAdapter(getApplicationContext()));
    }

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

        private final Context context;

        private PlaceAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public PlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_place_first, parent, false));
            else
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_place_second, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceAdapter.ViewHolder holder, int position) {
            if (position == 0) {
                holder.back.setOnClickListener(v -> {
                    finish();
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                });
                Glide.with(context).load(place.getImage()).into(holder.image);
            } else {
                holder.name.setText(place.getName());
                holder.address.setText(place.getAddress());
                holder.description.setText(place.getDescription());
                holder.link.setText(place.getLink());

                holder.link.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(holder.link.getText().toString()))));
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

            public TextView type, name, address, description, link;
            public RelativeLayout cont;
            public ImageView back, image;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                back = itemView.findViewById(R.id.back);
                type = itemView.findViewById(R.id.type);
                image = itemView.findViewById(R.id.image);
                name = itemView.findViewById(R.id.name);
                address = itemView.findViewById(R.id.address);
                description = itemView.findViewById(R.id.description);
                link = itemView.findViewById(R.id.link);
                cont = itemView.findViewById(R.id.btn_cont);
            }
        }
    }
}