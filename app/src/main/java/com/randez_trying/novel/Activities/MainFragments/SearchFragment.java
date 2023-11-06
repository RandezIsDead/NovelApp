package com.randez_trying.novel.Activities.MainFragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.randez_trying.novel.Activities.MainFragments.Adapters.CardStackAdapter;
import com.randez_trying.novel.Activities.MainFragments.SettingsActivities.FiltersSettingsActivity;
import com.randez_trying.novel.Activities.MainFragments.MatchActivities.MatchActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.CardStack.CardStackLayoutManager;
import com.randez_trying.novel.Views.CardStack.CardStackView;
import com.randez_trying.novel.Views.CardStack.Direction;
import com.randez_trying.novel.Views.CardStack.Duration;
import com.randez_trying.novel.Views.CardStack.SwipeAnimationSetting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private final Handler handler = new Handler();
    private CardStackView stackView;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private List<User> users;

    private int minAge = 18;
    private int maxAge = 25;
    private int minDistance = 0;
    private int maxDistance = 10;
    private User filter = StaticHelper.filterUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        users = new ArrayList<>();

        TextView appName = root.findViewById(R.id.app_name);
        ImageView rewind = root.findViewById(R.id.rewind);
        ImageView dislike = root.findViewById(R.id.dislike);
        ImageView like = root.findViewById(R.id.like);
        ImageView gift = root.findViewById(R.id.gift);

        ImageView filter = root.findViewById(R.id.settings);

        StaticHelper.setCoolTextGradient(appName);
        stackView = root.findViewById(R.id.stack_view);
        stackView.setItemViewCacheSize(50);
        manager = new CardStackLayoutManager(requireActivity().getApplicationContext());
        manager.setCanScrollVertical(false);
        stackView.setLayoutManager(manager);
        getUsers();

        rewind.setOnClickListener(v -> {
            //TODO subscriptionType check
            stackView.rewind();
        });
        dislike.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            manager.setSwipeAnimationSetting(setting);
            stackView.swipe();
        });
        like.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            manager.setSwipeAnimationSetting(setting);
            if (!iAlreadyLiked(users.get(manager.getTopPosition()))) sendLike(users.get(manager.getTopPosition()));
            if (checkMatch(users.get(manager.getTopPosition()))) {
                Intent intent = new Intent(requireActivity().getApplicationContext(), MatchActivity.class);
                intent.putExtra("user", new Gson().toJson(users.get(manager.getTopPosition())));
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
            stackView.swipe();
        });
        gift.setOnClickListener(v -> {
            android.app.Dialog dialogWindow = new android.app.Dialog(v.getRootView().getContext());

            dialogWindow.setContentView(R.layout.alert_gift_crystals);
            Objects.requireNonNull(dialogWindow.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final int[] crystals = {1};
            TextView cryCounter = dialogWindow.findViewById(R.id.exc_counter);
            ImageView plus = dialogWindow.findViewById(R.id.plus);
            ImageView minus = dialogWindow.findViewById(R.id.minus);
            TextView cancel = dialogWindow.findViewById(R.id.cancel);
            TextView cont = dialogWindow.findViewById(R.id.cont);
            StaticHelper.setCoolTextGradient(cont);

            cryCounter.setText(String.valueOf(1));
            cancel.setOnClickListener(vi -> dialogWindow.dismiss());
            plus.setOnClickListener(vi -> {
                crystals[0]++;
                cryCounter.setText(String.valueOf(crystals[0]));
            });
            minus.setOnClickListener(vi -> {
                if (crystals[0] > 1) {
                    crystals[0]--;
                    cryCounter.setText(String.valueOf(crystals[0]));
                }
            });
            cont.setOnClickListener(vi -> {
                if (!StaticHelper.me.getBalance().isEmpty()
                        && Integer.parseInt(StaticHelper.me.getBalance()) >= crystals[0]) {
                    User toUser = users.get(manager.getTopPosition());
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Constants.URL_ADD_TRANSACTION,
                            response2 -> StaticHelper.me.setBalance(
                                    String.valueOf(Integer.parseInt(StaticHelper.me.getBalance()) - crystals[0])
                            ),
                            System.out::println){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            try {
                                params.put("fromUser", StaticHelper.me.getPersonalId());
                                params.put("toUser", toUser.getPersonalId());
                                params.put("count", String.valueOf(crystals[0]));
                                params.put("whatBought", "crystal");
                                params.put("date", String.valueOf(System.currentTimeMillis()));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return params;
                        }
                    };

                    RequestHandler.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(stringRequest2);
                }
                dialogWindow.dismiss();
            });

            dialogWindow.show();
        });
        filter.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(requireActivity().getApplicationContext(), FiltersSettingsActivity.class));
            requireActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
        return root;
    }

    private boolean checkMatch(User user) {
        String myId = StaticHelper.me.getPersonalId();
        for (int i = 0; i < StaticHelper.matches.size(); i++) {
            String userOneId = StaticHelper.matches.get(i).getUserOne();
            String userTwoId = StaticHelper.matches.get(i).getUserTwo();

            if ((userOneId.equals(myId) && userTwoId.equals(user.getPersonalId()))
            || (userOneId.equals(user.getPersonalId()) && userTwoId.equals(myId))) {
                return true;
            }
        }
        return false;
    }

    private boolean iAlreadyLiked(User user) {
        for (int i = 0; i < StaticHelper.matches.size(); i++) {
            String userOneId = StaticHelper.matches.get(i).getUserOne();
            String userTwoId = StaticHelper.matches.get(i).getUserTwo();

            if ((userOneId.equals(StaticHelper.me.getPersonalId())
                    && userTwoId.equals(user.getPersonalId()))) {
                return true;
            }
        }
        return false;
    }

    private void sendLike(User user) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Constants.URL_ADD_LIKE,
                System.out::println,
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("userOne", StaticHelper.me.getPersonalId());
                    params.put("userTwo", user.getPersonalId());
                    params.put("likedOneTwo", "true");
                    params.put("likedTwoOne", "false");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return params;
            }
        };

        RequestHandler.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(stringRequest2);
    }

    private void checkNewFilters() {
        Runnable runnable = this::checkNewFilters;
        if (!filter.toString().equals(StaticHelper.filterUser.toString())
        || minAge != StaticHelper.minAge || maxAge != StaticHelper.maxAge
        || minDistance != StaticHelper.minDistance || maxDistance != StaticHelper.maxDistance) {
            filter = StaticHelper.filterUser;
            minAge = StaticHelper.minAge;
            maxAge = StaticHelper.maxAge;
            getUsers();
            filter = StaticHelper.filterUser;
            minAge = StaticHelper.minAge;
            maxAge = StaticHelper.maxAge;
            minDistance = StaticHelper.minDistance;
            maxDistance = StaticHelper.maxDistance;
        }
        handler.postDelayed(runnable, 100);
    }

    private void getUsers() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_USERS,
                response -> {
                    try {
                        JSONArray o = new JSONArray(response);
                        users.clear();

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
                            //TODO booleans showInRange, verified
                            boolean addUserByAge = (StaticHelper.getAge(user.getbDate()) >= StaticHelper.minAge)
                                    && (StaticHelper.getAge(user.getbDate()) <= StaticHelper.maxAge);
                            boolean addUserByDistance = true;

                            if (!StaticHelper.me.getLocation().isEmpty()
                            && !user.getLocation().isEmpty()) {
                                int dis = StaticHelper.getDistance(user.getLocation());
                                if (dis > StaticHelper.maxDistance - 1 && dis < StaticHelper.minDistance + 1) {
                                    addUserByDistance = false;
                                }
                            }

                            if (addUserByAge && addUserByDistance && StaticHelper.filter(user)
                            && !user.getPersonalId().equals(StaticHelper.me.getPersonalId())) users.add(user);
                        }
                        users.add(new User());

                        manager = new CardStackLayoutManager(requireActivity().getApplicationContext());
                        manager.setCanScrollVertical(false);
                        stackView.setLayoutManager(manager);
                        adapter = new CardStackAdapter(requireActivity().getApplicationContext(), requireActivity(), users);
                        adapter.setHasStableIds(true);
                        stackView.setAdapter(adapter);
                        checkNewFilters();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        RequestHandler.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
