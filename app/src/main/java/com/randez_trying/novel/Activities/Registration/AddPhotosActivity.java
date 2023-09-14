package com.randez_trying.novel.Activities.Registration;

import static android.content.Intent.ACTION_GET_CONTENT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
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
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Database.StaticHelper;
import com.randez_trying.novel.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPhotosActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private final List<PhotosAdapter.ViewHolder> holders = new ArrayList<>();


    private final List<String> photoUrls = new ArrayList<>();
    private int currentPhoto = 0;
    private boolean canClick = true;
    private RelativeLayout cont, contInactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        ImageView back = findViewById(R.id.back);
        TextView text = findViewById(R.id.h3);
        setTextGradient(text);

        cont = findViewById(R.id.btn_cont);
        contInactive = findViewById(R.id.btn_cont_inactive);

        RecyclerView recyclerView = findViewById(R.id.rec_photos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new PhotosAdapter(getApplicationContext()));

        checkPhotos();

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        cont.setOnClickListener(v -> {
            startActivity(new Intent(AddPhotosActivity.this, GenderActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
    }

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
                        if (i < 4) {
                            StringRequest stringRequest = getStringRequest(encodings.get(i));
                            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        }
                    }
                    canClick = false;
                }
            });

    @NonNull
    private StringRequest getStringRequest(String encode) {
        return new StringRequest(Request.Method.POST, Constants.URL_UPLOAD_IMAGE,
                response -> {
                    holders.get(currentPhoto).photo.setPadding(0 ,0, 0, 0);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(10));
                    Glide.with(getApplicationContext()).load(Base64.decode(encode.getBytes(), Base64.DEFAULT))
                            .apply(requestOptions).into(holders.get(currentPhoto).photo);
                    photoUrls.add(Constants.ROOT_URL + response);
                    canClick = true;
                    currentPhoto++;
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

    private void checkPhotos() {
        Runnable runnable = this::checkPhotos;
        if (photoUrls.size() == 4) {
            StaticHelper.me.setMediaLinks(String.join("&", photoUrls));
            contInactive.setVisibility(View.GONE);
            cont.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
            canClick = false;
        } else handler.postDelayed(runnable, 100);
    }

    private void setTextGradient(TextView textView) {
        textView.getPaint().setShader(
                new LinearGradient(0.0f, 0.0f,
                        textView.getPaint().measureText(
                                textView.getText().toString()),
                        textView.getTextSize(),
                        new int[]{Color.parseColor("#D2AB54"),
                                Color.parseColor("#FF627E")},
                        null, Shader.TileMode.CLAMP));
    }

    private class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

        private final Context context;

        public PhotosAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false));
            holders.add(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PhotosAdapter.ViewHolder holder, int position) {
            if (canClick) {
                holder.photo.setOnClickListener(v -> {
                    Intent intent = new Intent(ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("*/*");
                    getContent.launch(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView photo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                photo = itemView.findViewById(R.id.photo);
            }
        }
    }
}