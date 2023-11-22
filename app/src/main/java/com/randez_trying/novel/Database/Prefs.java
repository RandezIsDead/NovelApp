package com.randez_trying.novel.Database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Models.Credentials;
import com.randez_trying.novel.Models.Match;
import com.randez_trying.novel.Models.Message;
import com.randez_trying.novel.Models.Place;
import com.randez_trying.novel.Models.User;

import java.util.ArrayList;
import java.util.List;

public class Prefs {

    private static final String SHARED_PREFS = "Novel_Prefs";
    private static final Gson gson = new Gson();

    public static void write(Context context, String key, String text) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, text);
        editor.apply();
    }

    public static String read(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(key, "");
    }

    public static void writeObject(Context context, String key, Object value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    public static String getJson(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void saveMe(Context context, User me) {
        writeObject(context, "me", me);
    }

    public static User getMe(Context context) {
        String json = getJson(context, "me");
        if (gson.fromJson(json, new TypeToken<User>() {}.getType()) == null) return null;
        else return gson.fromJson(json, new TypeToken<User>() {}.getType());
    }

    public static void saveCredentials(Context context, Credentials credentials) {
        writeObject(context, "credentials", credentials);
    }

    public static Credentials getCredentials(Context context) {
        String json = getJson(context, "credentials");
        if (gson.fromJson(json, new TypeToken<Credentials>() {}.getType()) == null) return null;
        else return gson.fromJson(json, new TypeToken<Credentials>() {}.getType());
    }

    public static void saveFilter(Context context, User me) {
        writeObject(context, "filter", me);
    }

    public static User getFilter(Context context) {
        String json = getJson(context, "filter");
        if (gson.fromJson(json, new TypeToken<User>() {}.getType()) == null) return null;
        else return gson.fromJson(json, new TypeToken<User>() {}.getType());
    }

    public static void logout(Context context) {
        write(context, "me", null);
    }

    public static void saveLikes(Context context, List<Match> matches) {
        writeObject(context, "likes", matches);
    }

    public static List<Match> getLikes(Context context) {
        String json = getJson(context, "likes");
        if (gson.fromJson(json, new TypeToken<List<Match>>() {}.getType()) == null) return new ArrayList<>();
        else return gson.fromJson(json, new TypeToken<List<Match>>() {}.getType());
    }

    public static void savePlaces(Context context, List<Place> places) {
        writeObject(context, "places", places);
    }

    public static List<Place> getPlaces(Context context) {
        String json = getJson(context, "places");
        if (gson.fromJson(json, new TypeToken<List<Place>>() {}.getType()) == null) return new ArrayList<>();
        else return gson.fromJson(json, new TypeToken<List<Place>>() {}.getType());
    }

    public static void saveMessages(Context context, List<Message> messages) {
        writeObject(context, "messages", messages);
    }

    public static List<Message> getMessages(Context context) {
        String json = getJson(context, "messages");
        if (gson.fromJson(json, new TypeToken<List<Message>>() {}.getType()) == null) return new ArrayList<>();
        else return gson.fromJson(json, new TypeToken<List<Message>>() {}.getType());
    }

    public static void saveStickers(Context context, List<String> stickers) {
        writeObject(context, "stickers", stickers);
    }

    public static List<String> getStickers(Context context) {
        String json = getJson(context, "stickers");
        if (gson.fromJson(json, new TypeToken<List<String>>() {}.getType()) == null) return new ArrayList<>();
        else return gson.fromJson(json, new TypeToken<List<String>>() {}.getType());
    }
}
