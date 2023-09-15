package com.randez_trying.novel.Activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.randez_trying.novel.Activities.MainFragments.AccountFragment;
import com.randez_trying.novel.Activities.MainFragments.CafesFragment;
import com.randez_trying.novel.Activities.MainFragments.DashboardFragment;
import com.randez_trying.novel.Activities.MainFragments.MessagesFragment;
import com.randez_trying.novel.Activities.MainFragments.SearchFragment;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

public class MainActivity extends AppCompatActivity {

    private User me;
    private FragmentManager fm;
    private final SearchFragment searchFragment = new SearchFragment();
    private final DashboardFragment dashboardFragment = new DashboardFragment();
    private final CafesFragment cafesFragment = new CafesFragment();
    private final MessagesFragment messagesFragment = new MessagesFragment();
    private final AccountFragment accountFragment = new AccountFragment();
    private ImageView search, dashboard, cafes, messages, account;

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        me = Prefs.getMe(getApplicationContext());
        fm = getSupportFragmentManager();

        TextView appName = findViewById(R.id.app_name);
        RelativeLayout relFix = findViewById(R.id.rel_fix);
        RelativeLayout fixInternet = findViewById(R.id.btn_cont);
        FrameLayout container = findViewById(R.id.container);

        relFix.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

        search = findViewById(R.id.search);
        dashboard = findViewById(R.id.dashboard);
        cafes = findViewById(R.id.cafes);
        messages = findViewById(R.id.messages);
        account = findViewById(R.id.account);

        setOpened(0);
        setTextGradient(appName);
        search.setOnClickListener(v -> setOpened(0));
        dashboard.setOnClickListener(v -> setOpened(1));
        cafes.setOnClickListener(v -> setOpened(2));
        messages.setOnClickListener(v -> setOpened(3));
        account.setOnClickListener(v -> setOpened(4));

        Glide.with(getApplicationContext()).load(me.getMediaLinks().split("&")[0]).circleCrop().into(account);
    }

    private void setTextGradient(TextView textView) {
        textView.getPaint().setShader(
                new LinearGradient(0.0f, 0.0f,
                        textView.getPaint().measureText(textView.getText().toString()),
                        textView.getTextSize(),
                        new int[]{Color.parseColor("#D2AB54"),
                                Color.parseColor("#FF627E")},
                        null, Shader.TileMode.CLAMP)
        );
    }

    private void setOpened(int window) {
        if (window == 0) {
            setDefColors();
            search.setImageResource(R.drawable.search_main_selected);
            fm.beginTransaction().replace(R.id.container, searchFragment).commit();
        } else if (window == 1) {
            setDefColors();
            dashboard.setImageResource(R.drawable.dashboard_selected);
            fm.beginTransaction().replace(R.id.container, dashboardFragment).commit();
        } else if (window == 2) {
            setDefColors();
            cafes.setImageResource(R.drawable.cafes_selected);
            fm.beginTransaction().replace(R.id.container, cafesFragment).commit();
        } else if (window == 3) {
            setDefColors();
            messages.setImageResource(R.drawable.messages_selected);
            fm.beginTransaction().replace(R.id.container, messagesFragment).commit();
        } else if (window == 4) {
            setDefColors();
            fm.beginTransaction().replace(R.id.container, accountFragment).commit();
        }
    }

    private void setDefColors() {
        search.setImageResource(R.drawable.search_main);
        dashboard.setImageResource(R.drawable.dashboard);
        cafes.setImageResource(R.drawable.cafes);
        messages.setImageResource(R.drawable.messages);
    }
}
