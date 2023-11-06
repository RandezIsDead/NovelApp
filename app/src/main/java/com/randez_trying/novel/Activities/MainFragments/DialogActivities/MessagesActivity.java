package com.randez_trying.novel.Activities.MainFragments.DialogActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Activities.ProfileActivity;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.Message;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private User user;
//    private Dialog dialog;
    private List<Message> messages;
    private String dialogId;
    private boolean isVoice = true;
    private final Handler handler = new Handler();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        user = new Gson().fromJson(getIntent().getStringExtra("user"), new TypeToken<User>() {}.getType());

        ImageView back = findViewById(R.id.back);
        ImageView userIco = findViewById(R.id.user_ico);
        TextView userNameAge = findViewById(R.id.user_name_age);
        TextView lastOnline = findViewById(R.id.last_online);

        recyclerView = findViewById(R.id.rec_messages);

        ImageView sendFile = findViewById(R.id.send_file);
        ImageView sticker = findViewById(R.id.sticker);
        ImageView send = findViewById(R.id.send);
        EditText input = findViewById(R.id.write_msg);

        messages = new ArrayList<>();
        boolean foundDialog = false;
        List<List<Message>> messageList = StaticHelper.splitMessagesByDialogs(StaticHelper.messages);
        for (int i = 0; i < messageList.size(); i++) {
            if ((messageList.get(i).get(0).getSender().equals(StaticHelper.me.getPersonalId())
            && messageList.get(i).get(0).getReceiver().equals(user.getPersonalId()))
            || (messageList.get(i).get(0).getReceiver().equals(StaticHelper.me.getPersonalId())
                    && messageList.get(i).get(0).getSender().equals(user.getPersonalId()))) {
                messages.addAll(messageList.get(i));
                dialogId = messageList.get(i).get(0).getDialogId();
                foundDialog = true;
                break;
            }
        }
        if (!foundDialog) dialogId = UUID.randomUUID().toString();

        userNameAge.setText(user.getName() + ", " + StaticHelper.getAge(user.getbDate()));
        Glide.with(getApplicationContext()).load(user.getMediaLinks().split("&")[0]).circleCrop().into(userIco);
        lastOnline.setText("Был(а) недавно");

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new MessagesAdapter(getApplicationContext(), messages);
        adapter.setHasStableIds(true);
        recyclerView.setItemViewCacheSize(messages.size());
        recyclerView.setAdapter(adapter);
        updateMessages();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    send.setImageResource(R.drawable.send_text);
                    isVoice = false;
                } else {
                    send.setImageResource(R.drawable.send_voice);
                    isVoice = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        userIco.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("user", new Gson().toJson(user));
            intent.putExtra("msg", "true");
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
        send.setOnClickListener(v -> {
            if (!isVoice) {
                sendTextMessage(input.getText().toString().trim());
                input.setText("");
            }
        });
        send.setOnLongClickListener(v -> {
            if (isVoice) {
                //TODO
                return true;
            } else {
                return false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateMessages() {
        Runnable runnable = this::updateMessages;

        List<Message> temp = new ArrayList<>();

        boolean foundDialog = false;
        List<List<Message>> messageList = StaticHelper.splitMessagesByDialogs(StaticHelper.messages);
        for (int i = 0; i < messageList.size(); i++) {
            if ((messageList.get(i).get(0).getSender().equals(StaticHelper.me.getPersonalId())
                    && messageList.get(i).get(0).getReceiver().equals(user.getPersonalId()))
                    || (messageList.get(i).get(0).getReceiver().equals(StaticHelper.me.getPersonalId())
                    && messageList.get(i).get(0).getSender().equals(user.getPersonalId()))) {
                temp = messageList.get(i);
                foundDialog = true;
                break;
            }
        }
        if (!foundDialog) temp = new ArrayList<>();

        if (temp.size() != messages.size()) {
            messages.add(temp.get(temp.size() - 1));
            recyclerView.setItemViewCacheSize(messages.size());
            adapter.notifyItemInserted(messages.size());
        } else if (!new HashSet<>(temp).containsAll(messages) || !new HashSet<>(messages).containsAll(temp)) {
            Collections.copy(messages, temp);
            recyclerView.setItemViewCacheSize(messages.size());
            adapter.notifyDataSetChanged();
        } else if (!new Gson().toJson(messages).equals(new Gson().toJson(temp))) {
            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);
                for (int j = 0; j < temp.size(); j++) {
                    Message tempMessage = temp.get(j);
                    if (message.getSendTime().equals(tempMessage.getSendTime())) {
                        if (!new Gson().toJson(message).equals(new Gson().toJson(tempMessage))) {
                            messages.set(i, tempMessage);
                            adapter.notifyItemChanged(i);
                        }
                    }
                }
            }
        }

        handler.postDelayed(runnable, 500);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendTextMessage(String text) {

        Message msg = new Message(
                String.valueOf(UUID.randomUUID()),
                dialogId,
                Encrypt.encode(text, dialogId),
                StaticHelper.me.getPersonalId(),
                user.getPersonalId(),
                String.valueOf(System.currentTimeMillis()),
                "false",
                "false"
        );
        messages.add(msg);
        StaticHelper.messages.add(msg);
        recyclerView.setItemViewCacheSize(messages.size());
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADD_MESSAGE,
                System.out::println,
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("messageId", String.valueOf(UUID.randomUUID()));
                params.put("dialogId", dialogId);
                params.put("text", Encrypt.encode(text, dialogId));
                params.put("sender", StaticHelper.me.getPersonalId());
                params.put("receiver", user.getPersonalId());
                params.put("sendTime", String.valueOf(System.currentTimeMillis()));
                params.put("isMedia", "false");
                params.put("isRead", "false");
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

        private final Context context;
        private final List<Message> messages;

        public MessagesAdapter(Context context, List<Message> messages) {
            this.context = context;
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false));
            else
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
            Message message = messages.get(position);

            holder.text.setText(Encrypt.decode(message.getText().getBytes(), message.getDialogId()));

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(Long.parseLong(message.getSendTime()));
            holder.time.setText(simpleDateFormat.format(date));

            if (message.getSender().equals(StaticHelper.me.getPersonalId()))
                if (message.getIsRead().equals("true")) holder.readStatus.setVisibility(View.VISIBLE);
            if (!message.getSender().equals(StaticHelper.me.getPersonalId()))
                Glide.with(context).load(user.getMediaLinks().split("&")[0]).circleCrop().into(holder.userImg);

            holder.itemView.setOnLongClickListener(v -> {
                System.out.println("Logn");
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (messages.get(position).getSender().equals(StaticHelper.me.getPersonalId())) return 0;
            else return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView text, time;
            public ImageView userImg, readStatus;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                time = itemView.findViewById(R.id.time);
                userImg = itemView.findViewById(R.id.user_img);
                readStatus = itemView.findViewById(R.id.read_status);
            }
        }
    }
}