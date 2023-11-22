package com.randez_trying.novel.Helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.location.Location;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.randez_trying.novel.Activities.Mains.Match.MatchActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.Prefs;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Models.Credentials;
import com.randez_trying.novel.Models.Match;
import com.randez_trying.novel.Models.Message;
import com.randez_trying.novel.Models.Place;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.CardStack.Direction;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticHelper {

    public static User me = new User();
    public static Credentials myCredentials = new Credentials();
    public static List<Message> messages = new ArrayList<>();
    public static List<Match> matches = new ArrayList<>();
    public static List<Place> places = new ArrayList<>();

    //List of user options
    public static List<String> interests = new ArrayList<>();
    public static List<String> languages = new ArrayList<>();
    public static List<String> sports = new ArrayList<>();
    public static List<String> pets = new ArrayList<>();
    public static List<String> food = new ArrayList<>();
    public static List<String> stickers = new ArrayList<>();

    //Filters
    public static int minDistance = 0;
    public static int maxDistance = 1000;
    public static int minAge = 18;
    public static int maxAge = 25;
    public static boolean showInRange = false;
    public static boolean showMen = false;
    public static boolean showWoman = false;
    public static boolean showVerified = false;
    public static User filterUser = new User();

    public static void updateUser(Context context) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_USER,
                response -> Prefs.saveMe(context, StaticHelper.me),
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("personalId", me.getPersonalId());
                params.put("city", Encrypt.encode(me.getCity(), me.getPersonalId()));
                params.put("balance", Encrypt.encode(me.getBalance(), me.getPersonalId()));
                params.put("company", Encrypt.encode(me.getCompany(), me.getPersonalId()));
                params.put("education", Encrypt.encode(me.getEducation(), me.getPersonalId()));
                params.put("growth", Encrypt.encode(me.getGrowth(), me.getPersonalId()));
                params.put("interests", Encrypt.encode(me.getInterests(), me.getPersonalId()));
                params.put("job", Encrypt.encode(me.getJob(), me.getPersonalId()));
                params.put("languages", Encrypt.encode(me.getLanguages(), me.getPersonalId()));
                params.put("mediaLinks", Encrypt.encode(me.getMediaLinks(), me.getPersonalId()));
                params.put("gender", Encrypt.encode(me.getGender(), me.getPersonalId()));
                params.put("about", Encrypt.encode(me.getAbout(), me.getPersonalId()));
                params.put("familyPlans", Encrypt.encode(me.getFamilyPlans(), me.getPersonalId()));
                params.put("relationshipGoals", Encrypt.encode(me.getRelationshipGoals(), me.getPersonalId()));
                params.put("sports", Encrypt.encode(me.getSports(), me.getPersonalId()));
                params.put("alcohol", Encrypt.encode(me.getAlcohol(), me.getPersonalId()));
                params.put("smoke", Encrypt.encode(me.getSmoke(), me.getPersonalId()));
                params.put("personalityType", Encrypt.encode(me.getPersonalityType(), me.getPersonalId()));
                params.put("socialMediaLinks", Encrypt.encode(me.getSocialMediaLinks(), me.getPersonalId()));
                params.put("zodiacSign", Encrypt.encode(me.getZodiacSign(), me.getPersonalId()));
                params.put("subscriptionType", Encrypt.encode(me.getSubscriptionType(), me.getPersonalId()));
                params.put("status", Encrypt.encode(me.getStatus(), me.getPersonalId()));
                params.put("playlist", Encrypt.encode(me.getPlaylist(), me.getPersonalId()));
                params.put("location", Encrypt.encode(me.getLocation(), me.getPersonalId()));
                params.put("talkStyle", Encrypt.encode(me.getTalkStyle(), me.getPersonalId()));
                params.put("loveLang", Encrypt.encode(me.getLoveLang(), me.getPersonalId()));
                params.put("pets", Encrypt.encode(me.getPets(), me.getPersonalId()));
                params.put("food", Encrypt.encode(me.getFood(), me.getPersonalId()));
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest2);
    }

    public static void getInterests(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_INTERESTS,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            interests.add(Encrypt.decode(array.getJSONObject(i).getString("interest").getBytes(), "0"));
                        }
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

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getLanguages(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_LANGUAGES,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            languages.add(Encrypt.decode(array.getJSONObject(i).getString("language").getBytes(), "0"));
                        }
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

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getSports(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_SPORTS,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            sports.add(Encrypt.decode(array.getJSONObject(i).getString("sport").getBytes(), "0"));
                        }
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

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getPets(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_PETS,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            pets.add(Encrypt.decode(array.getJSONObject(i).getString("pet").getBytes(), "0"));
                        }
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

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getFood(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_FOOD,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            food.add(Encrypt.decode(array.getJSONObject(i).getString("food").getBytes(), "0"));
                        }
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

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void setCoolTextGradient(TextView textView) {
        textView.getPaint().setShader(
                new LinearGradient(0.0f, 0.0f,
                        textView.getPaint().measureText(textView.getText().toString()),
                        textView.getTextSize(),
                        new int[]{Color.parseColor("#D2AB54"),
                                Color.parseColor("#FF627E")},
                        null, Shader.TileMode.CLAMP)
        );
    }

    public static void setTextGradient(TextView textView, int[] colors) {
        textView.getPaint().setShader(
                new LinearGradient(0.0f, 0.0f,
                        textView.getPaint().measureText(textView.getText().toString()),
                        textView.getTextSize(),
                        colors,
                        null, Shader.TileMode.CLAMP)
        );
    }

    public static int getAge(String date) {
        long now = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        long d = LocalDateTime.parse(date + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        long age = now - d;
        return (int) (age / 31556952000L);
    }

    public static int getDistance(String latLng) {
        String[] split1 = latLng.split("&");
        String[] split2 = StaticHelper.me.getLocation().split("&");

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(Float.parseFloat(split1[0]));
        startPoint.setLongitude(Float.parseFloat(split1[1]));

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(Float.parseFloat(split2[0]));
        endPoint.setLongitude(Float.parseFloat(split2[1]));

        return (int) (startPoint.distanceTo(endPoint) / 1000);
    }

    public static boolean filter(User user) {
        boolean ze = !filterUser.getZodiacSign().isEmpty();
        boolean zodiac = true;
        if (ze) zodiac = user.getZodiacSign().equals(filterUser.getZodiacSign());

        boolean ie = !filterUser.getRelationshipGoals().isEmpty();
        boolean iSearch = true;
        if (ie) iSearch = user.getRelationshipGoals().equals(filterUser.getRelationshipGoals());

        boolean ae = !filterUser.getAlcohol().isEmpty();
        boolean alcohol = true;
        if (ae) alcohol = user.getAlcohol().equals(filterUser.getAlcohol());

        boolean se = !filterUser.getSmoke().isEmpty();
        boolean smoke = true;
        if (se) smoke = user.getSmoke().equals(filterUser.getSmoke());
        //TODO
        return zodiac && iSearch && alcohol && smoke;
    }

    public static boolean like = false;
    public static void swiped(Direction direction) {
        if (direction == Direction.Right) {
            like = true;
        }
    }

    public static List<List<Message>> splitMessagesByDialogs(List<Message> messageList) {
        List<List<Message>> dialogs = new ArrayList<>();
        List<Message> temp = new ArrayList<>(messageList);
        List<String> dialogIds = new ArrayList<>();

        for (int i = 0; i < temp.size(); i++) {
            String dialogId = temp.get(i).getDialogId();
            boolean isExist = false;
            for (int j = 0; j < dialogIds.size(); j++) {
                if (dialogIds.get(j).equals(dialogId)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) dialogIds.add(dialogId);
        }

        for (int i = 0; i < dialogIds.size(); i++) {
            String dId = dialogIds.get(i);
            List<Message> arr = new ArrayList<>();
            for (int j = 0; j < temp.size(); j++) {
                if (temp.get(j).getDialogId().equals(dId)) {
                    arr.add(temp.get(j));
                }
            }
            dialogs.add(arr);
        }

        return dialogs;
    }
}
