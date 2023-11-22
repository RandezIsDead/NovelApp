package com.randez_trying.novel.Activities.Mains;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.randez_trying.novel.R;

import java.util.Arrays;
import java.util.List;

public class ViewImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        String images = getIntent().getStringExtra("img");
        List<String> imagesList = Arrays.asList(images.split("&"));

        ImageView back = findViewById(R.id.back);
        RecyclerView recImages = findViewById(R.id.rec_images);
        recImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recImages.setHasFixedSize(true);
        recImages.setAdapter(new ImagesAdapter(getApplicationContext(), this, imagesList));

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
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

    private static class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final List<String> images;

        public ImagesAdapter(Context context, Activity activity, List<String> images) {
            this.context = context;
            this.activity = activity;
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30));
            Glide.with(context).load(images.get(position)).apply(requestOptions).into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra("img", images.get(position));
                intent.putExtra("name",
                        images.get(position).replace("https://scripsit-itaque.ru/novel/images/", "")
                );
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            });
        }

        @Override
        public int getItemCount() {
            return images.size();
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