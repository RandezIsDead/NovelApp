package com.randez_trying.novel.Activities.Mains.Match;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Activities.Mains.MessagesActivity;
import com.randez_trying.novel.Activities.Mains.ProfileActivity;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        ImageView back = findViewById(R.id.back);
        ImageView userOne = findViewById(R.id.user_one);
        ImageView userTwo = findViewById(R.id.user_two);

        RelativeLayout write = findViewById(R.id.btn_talk);
        RelativeLayout toProfile = findViewById(R.id.btn_profile);

        User user = new Gson().fromJson(getIntent().getStringExtra("user"), new TypeToken<User>() {}.getType());
        Glide.with(getApplicationContext()).load(StaticHelper.me.getMediaLinks().split("&")[0]).circleCrop().into(userOne);
        Glide.with(getApplicationContext()).load(user.getMediaLinks().split("&")[0]).circleCrop().into(userTwo);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        write.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
            intent.putExtra("user", new Gson().toJson(user));
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
        toProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("user", new Gson().toJson(user));
            intent.putExtra("msg", "true");
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
    }
}