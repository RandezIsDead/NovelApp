package com.randez_trying.novel.Activities.MainFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.randez_trying.novel.Activities.MainFragments.DialogActivities.MessagesActivity;
import com.randez_trying.novel.Activities.MainFragments.MatchActivities.ViewMatchesActivity;
import com.randez_trying.novel.Activities.ProfileActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.Message;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DialogsAdapter adapter;
    private List<List<Message>> messagesList;
    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rec_view_layout, container, false);
        messagesList = StaticHelper.splitMessagesByDialogs(StaticHelper.messages);
        adapter = new DialogsAdapter(requireActivity().getApplicationContext(), requireActivity(), messagesList);
        recyclerView = root.findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        updateDialogs();
        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDialogs() {
        Runnable runnable = this::updateDialogs;

        List<List<Message>> temp = StaticHelper.splitMessagesByDialogs(StaticHelper.messages);

        if (messagesList.size() != temp.size()) {
            messagesList.add(temp.get(temp.size() - 1));
            adapter.notifyItemInserted(messagesList.size());
        } else if (!new Gson().toJson(messagesList).equals(new Gson().toJson(temp))) {
            for (int i = 0; i < messagesList.size(); i++) {
                List<Message> dialog = messagesList.get(i);
                for (int j = 0; j < temp.size(); j++) {
                    List<Message> tempDialog = temp.get(j);
                    if (dialog.get(0).getDialogId().equals(tempDialog.get(0).getDialogId())) {
                        if (!new Gson().toJson(dialog).equals(new Gson().toJson(tempDialog))) {
                            messagesList.set(i, tempDialog);
                            adapter.notifyItemChanged(i + 1);
                        }
                    }
                }
            }
        }

        handler.postDelayed(runnable, 1000);
    }

    private static class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final List<List<Message>> dialogs;

        public DialogsAdapter(Context context, Activity activity, List<List<Message>> dialogs) {
            this.context = context;
            this.activity = activity;
            this.dialogs = dialogs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_first_messages, parent, false));
            else
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dialog, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                StaticHelper.setCoolTextGradient(holder.appName);

                List<String> users = new ArrayList<>();
                users.add("");
                for (int i = 0; i < StaticHelper.matches.size(); i++) {
                    for (int j = 0; j < StaticHelper.matches.size(); j++) {
                        if (StaticHelper.matches.get(i).getUserOne().equals(StaticHelper.matches.get(j).getUserTwo())
                        && StaticHelper.matches.get(i).getUserTwo().equals(StaticHelper.matches.get(j).getUserOne())) {
                            String add = "";
                            if (StaticHelper.matches.get(i).getUserOne().equals(StaticHelper.me.getPersonalId())) {
                                add = StaticHelper.matches.get(i).getUserTwo();
                            } else if (StaticHelper.matches.get(i).getUserTwo().equals(StaticHelper.me.getPersonalId())) {
                                add = StaticHelper.matches.get(i).getUserOne();
                            }

                            boolean exist = false;
                            for (int k = 0; k < users.size(); k++) {
                                if (users.get(k).equals(add)) exist = true;
                            }
                            if (!exist) users.add(add);
                        }
                    }
                }

                holder.recLikes.setHasFixedSize(true);
                holder.recLikes.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                holder.recLikes.setAdapter(new MatchesAdapter(context, activity, users));
            } else {
                List<Message> dialog = dialogs.get(position - 1);
                getUser(holder, dialog);
            }
        }

        private void getUser(ViewHolder holder, List<Message> messages) {
            @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_USER,
                    response -> {
                        try {
                            JSONArray o = new JSONArray(response);

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

                                String dialogId = messages.get(0).getDialogId();

                                Glide.with(context).load(user.getMediaLinks().split("&")[0]).circleCrop().into(holder.userImg);
                                holder.userNameAge.setText(user.getName() + ", " + StaticHelper.getAge(user.getbDate()));

                                String text = Encrypt.decode(messages.get(messages.size() - 1).getText().getBytes(), dialogId);

                                if (messages.get(messages.size() - 1).getSender().equals(StaticHelper.me.getPersonalId()))
                                    holder.lastMsg.setText("Я: " + text);
                                else holder.lastMsg.setText(text);

                                holder.itemView.setOnClickListener(v -> {
                                    Intent intent = new Intent(context, MessagesActivity.class);
                                    intent.putExtra("user", new Gson().toJson(user));
                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    System.out::println){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    if (messages.get(0).getSender().equals(StaticHelper.me.getPersonalId())) params.put("personalId", messages.get(0).getReceiver());
                    else if (messages.get(0).getReceiver().equals(StaticHelper.me.getPersonalId())) params.put("personalId", messages.get(0).getSender());
                    return params;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }

        @Override
        public int getItemCount() {
            return dialogs.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView appName, userNameAge, lastMsg, cumText;
            public ImageView userImg, muted;
            public RecyclerView recLikes;
            public RelativeLayout relCUM;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                appName = itemView.findViewById(R.id.app_name);
                recLikes = itemView.findViewById(R.id.rec_likes);

                userImg = itemView.findViewById(R.id.user_img);
                userNameAge = itemView.findViewById(R.id.user_name_age);
                lastMsg = itemView.findViewById(R.id.last_message);
                muted = itemView.findViewById(R.id.muted);
                relCUM = itemView.findViewById(R.id.count_unread_messages);
                cumText = itemView.findViewById(R.id.cum_num);
            }
        }
    }

    private static class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {

        private final Context context;
        private final Activity activity;
        private final List<String> users;

        public MatchesAdapter(Context context, Activity activity, List<String> users) {
            this.context = context;
            this.activity = activity;
            this.users = users;
        }

        @NonNull
        @Override
        public MatchesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_match, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MatchesAdapter.ViewHolder holder, int position) {
            if (users.get(position).isEmpty()) {
                RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30));
                Glide.with(context).load(R.drawable.matches).apply(requestOptions).into(holder.userImg);
                holder.userNameAge.setText("Посмотреть лайки");
                holder.userNameAge.setTextColor(context.getColor(R.color.cool));

                holder.addict.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.matches_add).into(holder.addict);

                holder.itemView.setOnClickListener(v -> {
                    activity.startActivity(new Intent(context, ViewMatchesActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                });
            } else getUser(holder, users.get(position));
        }

        private void getUser(ViewHolder holder, String uid) {
            @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_USER,
                    response -> {
                        try {
                            JSONArray o = new JSONArray(response);

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

                                RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30));
                                Glide.with(context).load(user.getMediaLinks().split("&")[0]).apply(requestOptions).into(holder.userImg);
                                holder.userNameAge.setText(user.getName() + ", " + StaticHelper.getAge(user.getbDate()));

                                holder.itemView.setOnClickListener(v -> {
                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    intent.putExtra("user", new Gson().toJson(user));
                                    intent.putExtra("msg", "true");
                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    System.out::println){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("personalId", uid);
                    return params;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView userImg, addict;
            public TextView userNameAge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                userImg = itemView.findViewById(R.id.user_img);
                userNameAge = itemView.findViewById(R.id.user_name_age);

                addict = itemView.findViewById(R.id.img_add);
            }
        }
    }
}