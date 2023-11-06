package com.randez_trying.novel.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.randez_trying.novel.Activities.MainFragments.AccountFragment;
import com.randez_trying.novel.Activities.MainFragments.PlacesFragment;
import com.randez_trying.novel.Activities.MainFragments.DashboardFragment;
import com.randez_trying.novel.Activities.MainFragments.MessagesFragment;
import com.randez_trying.novel.Activities.MainFragments.SearchFragment;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.Credentials;
import com.randez_trying.novel.Models.Match;
import com.randez_trying.novel.Models.Message;
import com.randez_trying.novel.Models.Place;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    private final SearchFragment searchFragment = new SearchFragment();
    private final DashboardFragment dashboardFragment = new DashboardFragment();
    private final PlacesFragment placesFragment = new PlacesFragment();
    private final MessagesFragment messagesFragment = new MessagesFragment();
    private final AccountFragment accountFragment = new AccountFragment();
    private ImageView search, dashboard, cafes, messages, account;
    private final Handler balanceHandler = new Handler();
    private final Handler dialogHandler = new Handler();

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        readPrefs();
        pullMe();
        getLikes();
        getPlaces();
        pullDialogs();
        fm = getSupportFragmentManager();

        RelativeLayout relFix = findViewById(R.id.rel_fix);
        RelativeLayout fixInternet = findViewById(R.id.btn_cont);
        FrameLayout container = findViewById(R.id.container);

        if (!isInternetAvailable()) {
            relFix.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }

        search = findViewById(R.id.search);
        dashboard = findViewById(R.id.dashboard);
        cafes = findViewById(R.id.cafes);
        messages = findViewById(R.id.messages);
        account = findViewById(R.id.account);

        setOpened(0);
        search.setOnClickListener(v -> setOpened(0));
        dashboard.setOnClickListener(v -> setOpened(1));
        cafes.setOnClickListener(v -> setOpened(2));
        messages.setOnClickListener(v -> setOpened(3));
        account.setOnClickListener(v -> setOpened(4));

        Glide.with(getApplicationContext()).load(StaticHelper.me.getMediaLinks().split("&")[0]).circleCrop().into(account);
        getLocation();
        updateHandler();

        StaticHelper.getInterests(getApplicationContext());
        StaticHelper.getLanguages(getApplicationContext());
        StaticHelper.getSports(getApplicationContext());
        StaticHelper.getPets(getApplicationContext());
        StaticHelper.getFood(getApplicationContext());

        fixInternet.setOnClickListener(v -> {
            //TODO
        });
    }

    private void readPrefs() {
        StaticHelper.me = Prefs.getMe(getApplicationContext());
        StaticHelper.filterUser = Prefs.getFilter(getApplicationContext());

        String s1 = Prefs.read(getApplicationContext(), "minDistance");
        String s2 = Prefs.read(getApplicationContext(), "maxDistance");
        String s3 = Prefs.read(getApplicationContext(), "minAge");
        String s4 = Prefs.read(getApplicationContext(), "maxAge");

        if (StaticHelper.filterUser == null) StaticHelper.filterUser = new User();
        if (s1.isEmpty()) StaticHelper.minDistance = 0;
        else StaticHelper.minDistance = Integer.parseInt(s1);
        if (s2.isEmpty()) StaticHelper.maxDistance = 1000;
        else StaticHelper.maxDistance = Integer.parseInt(s2);
        if (s3.isEmpty()) StaticHelper.minAge = 18;
        else StaticHelper.minAge = Integer.parseInt(s3);
        if (s4.isEmpty()) StaticHelper.maxAge = 25;
        else StaticHelper.maxAge = Integer.parseInt(s4);
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
            fm.beginTransaction().replace(R.id.container, placesFragment).commit();
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

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddress = InetAddress.getByName("google.com");
            return !ipAddress.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    private void updateHandler() {
        Runnable runnable = this::updateHandler;
        updateBalance();
        balanceHandler.postDelayed(runnable, 1000);
    }

    private void pullMe() {
        StringRequest stringRequestCred = new StringRequest(Request.Method.POST, Constants.URL_GET_CREDENTIALS,
                response -> {
                    try {
                        JSONArray o = new JSONArray(response);
                        JSONObject jsonObject = o.getJSONObject(0);
                        Credentials credentials = new Credentials(
                                null,
                                jsonObject.getString("personalId"),
                                jsonObject.getString("password"),
                                jsonObject.getString("email"),
                                jsonObject.getString("apiKey"),
                                jsonObject.getString("signUpDate"),
                                jsonObject.getString("isActive"),
                                jsonObject.getString("isBanned")
                        );
                        Prefs.saveCredentials(getApplicationContext(), credentials);
                        StaticHelper.myCredentials = credentials;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                System.out::println) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personalId", StaticHelper.me.getPersonalId());
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequestCred);
    }

    private void getLikes() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_LIKES,
                response -> {
                    try {
                        JSONArray o = new JSONArray(response);

                        List<Match> matches = new ArrayList<>();
                        for (int i = 0; i < o.length(); i++) {
                            JSONObject jsonObject = o.getJSONObject(i);
                            matches.add(new Match(
                                    jsonObject.getString("userOne"),
                                    jsonObject.getString("userTwo")
                            ));
                        }
                        StaticHelper.matches = matches;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personalId", StaticHelper.me.getPersonalId());
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void updateBalance() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_TRANSACTIONS,
                response -> {
                    try {
                        JSONArray o = new JSONArray(response);

                        int totalCrystals = 0;
                        int totalBoosts = 0;
                        for (int i = 0; i < o.length(); i++) {
                            JSONObject jsonObject = o.getJSONObject(i);
                            String fromUser = jsonObject.getString("fromUser");
                            String toUser = jsonObject.getString("toUser");
                            String whatBought = jsonObject.getString("whatBought");
                            int count = Integer.parseInt(jsonObject.getString("count"));

                            String myId = StaticHelper.me.getPersonalId();

                            if (whatBought.equals("crystal")) {
                                if (fromUser.equals(myId)) totalCrystals -= count;
                                if (myId.equals(toUser)) totalCrystals += count;
                            } else if (whatBought.equals("boost")) {
                                if (fromUser.equals(myId)) totalBoosts -= count;
                                if (myId.equals(toUser)) totalBoosts += count;
                            } else if (whatBought.equals("baseSub")) {
                                //TODO
                            } else if (whatBought.equals("advSub")) {
                                //TODO
                            } else if (whatBought.equals("proSub")) {
                                //TODO
                            }
                        }
                        StaticHelper.me.setBalance(String.valueOf(totalCrystals));
                        StaticHelper.me.setBoosts(String.valueOf(totalBoosts));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personalId", StaticHelper.me.getPersonalId());
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0
                || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
            try {
                if (lastKnownLocation != null) {
                    StaticHelper.me.setLocation(lastKnownLocation.getLatitude() + "&" + lastKnownLocation.getLongitude());
                    StaticHelper.updateUser(getApplicationContext());
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (Prefs.read(getApplicationContext(), "getLocation").isEmpty()
                || !Prefs.read(getApplicationContext(), "getLocation").equals("false")) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION"
                    }, 476);
        }
    }

    private void getPlaces() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_PLACES,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            //TODO not show not my city places
//                            if (StaticHelper.me.getCity().equals(Encrypt.decode(array.getJSONObject(i).getString("city").getBytes(), "0"))) {
                                StaticHelper.places.add(new Place(
                                        Encrypt.decode(array.getJSONObject(i).getString("categoryId").getBytes(), "0"),
                                        Encrypt.decode(array.getJSONObject(i).getString("city").getBytes(), "0"),
                                        Encrypt.decode(array.getJSONObject(i).getString("name").getBytes(), "0"),
                                        Encrypt.decode(array.getJSONObject(i).getString("description").getBytes(), "0"),
                                        Encrypt.decode(array.getJSONObject(i).getString("image").getBytes(), "0"),
                                        Encrypt.decode(array.getJSONObject(i).getString("address").getBytes(), "0"),
                                        Encrypt.decode(array.getJSONObject(i).getString("link").getBytes(), "0")
                                ));
//                            }
                        }
                        StaticHelper.places.add(0, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void pullDialogs() {
        Runnable runnable = this::pullDialogs;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_MESSAGES,
                response -> {
                    try {
                        JSONArray o = new JSONArray(response);
                        List<Message> messageList = new ArrayList<>();

                        for (int i = 0; i < o.length(); i++) {
                            JSONObject jsonObject = o.getJSONObject(i);
                            messageList.add(new Message(
                                    jsonObject.getString("messageId"),
                                    jsonObject.getString("dialogId"),
                                    jsonObject.getString("text"),
                                    jsonObject.getString("sender"),
                                    jsonObject.getString("receiver"),
                                    jsonObject.getString("sendTime"),
                                    jsonObject.getString("isMedia"),
                                    jsonObject.getString("isRead")
                            ));
                        }
                        Collections.sort(messageList);
                        StaticHelper.messages = messageList;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personalId", StaticHelper.me.getPersonalId());
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        dialogHandler.postDelayed(runnable, 2000);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 476) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0
                        || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                    getLocation();
                }
            } else Prefs.write(getApplicationContext(), "getLocation", "false");
        } else Prefs.write(getApplicationContext(), "getLocation", "false");
    }

    public void updateUserOnline(String isOnline) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_USER_ONLINE,
                System.out::println,
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personalId", StaticHelper.me.getPersonalId());
                params.put("isOnline", Encrypt.encode(isOnline, StaticHelper.me.getPersonalId()));
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserOnline("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserOnline("false");
    }
}