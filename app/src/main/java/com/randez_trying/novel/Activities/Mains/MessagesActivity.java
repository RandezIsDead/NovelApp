package com.randez_trying.novel.Activities.Mains;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Database.Constants;
import com.randez_trying.novel.Database.RequestHandler;
import com.randez_trying.novel.Helpers.Encrypt;
import com.randez_trying.novel.Helpers.StaticHelper;
import com.randez_trying.novel.Models.Message;
import com.randez_trying.novel.Models.User;
import com.randez_trying.novel.R;
import com.randez_trying.novel.Views.SwipeReply;
import com.randez_trying.novel.Views.VoiceRecorder.OnRecordListener;
import com.randez_trying.novel.Views.VoiceRecorder.RecordButton;
import com.randez_trying.novel.Views.VoiceRecorder.RecordView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MessagesActivity extends AppCompatActivity implements SwipeReply.SwipeControllerActions {

    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private User user;
    private String dialogId;
    private List<Message> messages;
    private List<MessagesAdapter.ViewHolder> holders;
    private final Handler handler = new Handler();

    private MediaRecorder mediaRecorder;
    private String audioPath;

    private RelativeLayout relStickers;
    private RecyclerView recStickers;
    private RelativeLayout relEdit;
    private TextView textEdit;
    private ImageView clearEdit;
    private boolean editMode = false;
    private String editMsgId;

    private RelativeLayout relReply;
    private TextView replyText;
    private ImageView clearReply;
    private boolean replyMode = false;
    private String replyMsgId = "-1";

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

        EditText input = findViewById(R.id.write_msg);
        ImageView sendFile = findViewById(R.id.send_file);
        ImageView sticker = findViewById(R.id.sticker);
        ImageView send = findViewById(R.id.send);
        RecordButton recordButton = findViewById(R.id.record);
        RecordView recordView = findViewById(R.id.record_view);

        relEdit = findViewById(R.id.rel_msg_edit);
        textEdit = findViewById(R.id.edit_msg);
        clearEdit = findViewById(R.id.clear_edit);
        relStickers = findViewById(R.id.rel_stickers);
        recStickers = findViewById(R.id.rec_stickers);
        recStickers.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        recStickers.setHasFixedSize(true);

        relReply = findViewById(R.id.rel_reply);
        replyText = findViewById(R.id.reply_msg);
        clearReply = findViewById(R.id.clear_reply);

        ImageView closeStickers = findViewById(R.id.close_stickers);
        closeStickers.setOnClickListener(v -> relStickers.setVisibility(View.GONE));

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

        holders = new ArrayList<>();

        userNameAge.setText(user.getName() + ", " + StaticHelper.getAge(user.getbDate()));
        Glide.with(getApplicationContext()).load(user.getMediaLinks().split("&")[0]).circleCrop().into(userIco);
        if (user.getIsOnline().equals("true")) {
            lastOnline.setText("Онлайн");
            lastOnline.setTextColor(Color.GREEN);
        } else lastOnline.setText("Был(а) недавно");

        recordButton.setRecordView(recordView);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new MessagesAdapter(getApplicationContext(), messages);
        adapter.setHasStableIds(true);
        recyclerView.setItemViewCacheSize(messages.size());
        recyclerView.setAdapter(adapter);
        updateMessages();

        SwipeReply messageSwipeController = new SwipeReply(getApplicationContext(), this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(messageSwipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if (isRecordingOk()) {
                    setUpRecording();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    recordView.setVisibility(View.VISIBLE);
                } else {
                    System.out.println("NO PERMISSIONS");
                    ActivityCompat.requestPermissions(MessagesActivity.this, permissions(), PERMISSION_GRANTED);
                }
            }

            @Override
            public void onCancel() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()) file.delete();
                recordView.setVisibility(View.GONE);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                recordView.setVisibility(View.GONE);
                mediaRecorder.stop();
                sendVoiceMessage();
            }

            @Override
            public void onLessThanSecond() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()) file.delete();
                recordView.setVisibility(View.GONE);
            }

            @Override
            public void onLock() {}

        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    recordButton.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                } else {
                    send.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
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
        sendFile.setOnClickListener(v -> {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MessagesActivity.this);
            bottomSheetDialog.setContentView(R.layout.alert_ph_vid);
            Objects.requireNonNull(bottomSheetDialog.getWindow()).setDimAmount(0.7f);

            ImageView image = bottomSheetDialog.findViewById(R.id.image);
            ImageView video = bottomSheetDialog.findViewById(R.id.video);
            bottomSheetDialog.show();

            if (image != null) {
                image.setOnClickListener(vi -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("image/*");
                    getContentImage.launch(intent);
                    bottomSheetDialog.dismiss();
                });
            }
            if (video != null) {
                video.setOnClickListener(vi -> {
                    getContentVideo.launch("video/*");
                    bottomSheetDialog.dismiss();
                });
            }
        });
        sticker.setOnClickListener(v -> {
            relStickers.setVisibility(View.VISIBLE);
            recStickers.setAdapter(new StickerAdapter(getApplicationContext(), StaticHelper.stickers));
        });
        send.setOnClickListener(v -> {
            if (!editMode && !replyMode) {
                sendTextMessage(input.getText().toString().trim(), "-1");
                input.setText("");
            } else if (editMode) {
                editTextMessage(input.getText().toString().trim());
                input.setText("");
                editMode = false;
                relEdit.setVisibility(View.GONE);
                textEdit.setText("");
            } else if (replyMode) {
                sendTextMessage(input.getText().toString().trim(), replyMsgId);
                input.setText("");
                replyMode = false;
                replyMsgId = "-1";
                relReply.setVisibility(View.GONE);
                replyText.setText("");
            }
        });
        clearEdit.setOnClickListener(v -> {
            input.setText("");
            editMode = false;
            relEdit.setVisibility(View.GONE);
            textEdit.setText("");
        });
        clearReply.setOnClickListener(v -> {
            input.setText("");
            replyMode = false;
            replyMsgId = "-1";
            relReply.setVisibility(View.GONE);
            replyText.setText("");
        });
    }

    public static String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] permissions_33 = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.RECORD_AUDIO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) p = permissions_33;
        else p = permissions;
        return p;
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

        if (temp.size() > messages.size()) {
            messages.add(temp.get(temp.size() - 1));
            recyclerView.setItemViewCacheSize(messages.size());
            adapter.notifyItemInserted(messages.size());
            recyclerView.scrollToPosition(messages.size() - 1);
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
    private void sendTextMessage(String text, String replyMsgId) {

        Message msg = new Message(
                String.valueOf(UUID.randomUUID()),
                dialogId,
                Encrypt.encode(text, dialogId),
                StaticHelper.me.getPersonalId(),
                user.getPersonalId(),
                replyMsgId,
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
                params.put("replyMsgId", replyMsgId);
                params.put("sendTime", String.valueOf(System.currentTimeMillis()));
                params.put("isMedia", "false");
                params.put("isRead", "false");
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void editTextMessage(String text) {
        for (int i = 0; i < StaticHelper.messages.size(); i++) {
            if (StaticHelper.messages.get(i).getMessageId().equals(editMsgId)) {
                StaticHelper.messages.get(i).setText(Encrypt.encode(text, StaticHelper.messages.get(i).getDialogId()));
            }
        }
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getMessageId().equals(editMsgId)) {
                messages.get(i).setText(Encrypt.encode(text, StaticHelper.messages.get(i).getDialogId()));
                adapter.notifyItemChanged(i);
            }
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_EDIT_MESSAGE,
                System.out::println,
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("messageId", editMsgId);
                params.put("text", Encrypt.encode(text, dialogId));
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private boolean isRecordingOk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        }
    }

    private void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        String fileName = System.currentTimeMillis() + ".3gp";
        File file;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
            StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

            File dir = new File(storageVolume.getDirectory().getPath() + "/Documents/Novel/Recordings/");
            dir.mkdirs();
            file = new File(storageVolume.getDirectory().getPath() + "/Documents/Novel/Recordings/" + fileName);
            audioPath = storageVolume.getDirectory().getPath() + "/Documents/Novel/Recordings/" + fileName;
        } else {
            File dir = new File(Environment.getExternalStorageDirectory(), "/Documents/Novel/Recordings/");
            dir.mkdirs();
            file = new File(dir, fileName);
        }
        audioPath = file.getAbsolutePath();
        mediaRecorder.setOutputFile(audioPath);
    }

    private void sendVoiceMessage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPLOAD_VOICE_MESSAGE,
                response -> {
                    String url = Constants.ROOT_URL + "messages/" + response;

                    Message msg = new Message(
                            String.valueOf(UUID.randomUUID()),
                            dialogId,
                            "voice&" + url,
                            StaticHelper.me.getPersonalId(),
                            user.getPersonalId(),
                            replyMsgId,
                            String.valueOf(System.currentTimeMillis()),
                            "true",
                            "false"
                    );
                    messages.add(msg);
                    StaticHelper.messages.add(msg);
                    recyclerView.setItemViewCacheSize(messages.size());
                    adapter.notifyItemInserted(messages.size() - 1);
                    recyclerView.scrollToPosition(messages.size() - 1);
                    StringRequest stringRequestM = new StringRequest(Request.Method.POST, Constants.URL_ADD_MESSAGE,
                            System.out::println,
                            System.out::println){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("messageId", String.valueOf(UUID.randomUUID()));
                            params.put("dialogId", dialogId);
                            params.put("text", "voice&" + url);
                            params.put("sender", StaticHelper.me.getPersonalId());
                            params.put("receiver", user.getPersonalId());
                            params.put("replyMsgId", replyMsgId);
                            params.put("sendTime", String.valueOf(System.currentTimeMillis()));
                            params.put("isMedia", "true");
                            params.put("isRead", "false");
                            return params;
                        }
                    };

                    RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequestM);
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("audio", getStringFile(audioPath));
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public String getStringFile(String file) {
        InputStream inputStream;
        String encodedFile = "", lastVal;
        try {
            inputStream = Files.newInputStream(Paths.get(file));

            byte[] buffer = new byte[10240];
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            int i;
            while ((i = inputStream.read(buffer, 0, buffer.length)) > 0) {output.write(buffer, 0, i);}
            encodedFile = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    private int counterImages = 0;
    private int totalImages = 10;
    private boolean needSend = true;
    private final StringBuilder textToSend = new StringBuilder();
    private final Handler imageSendHandler = new Handler();

    private void checkImagesToSend() {
        Runnable runnable = this::checkImagesToSend;

        if (counterImages == totalImages && needSend) {
            sendImage(textToSend.toString());
            needSend = false;
        }

        imageSendHandler.postDelayed(runnable, 100);
    }

    private void sendImage(String text) {
        Message msg = new Message(
                String.valueOf(UUID.randomUUID()),
                dialogId,
                text,
                StaticHelper.me.getPersonalId(),
                user.getPersonalId(),
                replyMsgId,
                String.valueOf(System.currentTimeMillis()),
                "true",
                "false"
        );
        messages.add(msg);
        StaticHelper.messages.add(msg);
        recyclerView.setItemViewCacheSize(messages.size());
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        StringRequest stringRequestImg = new StringRequest(Request.Method.POST, Constants.URL_ADD_MESSAGE,
                System.out::println,
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("messageId", String.valueOf(UUID.randomUUID()));
                params.put("dialogId", dialogId);
                params.put("text", text);
                params.put("sender", StaticHelper.me.getPersonalId());
                params.put("receiver", user.getPersonalId());
                params.put("replyMsgId", replyMsgId);
                params.put("sendTime", String.valueOf(System.currentTimeMillis()));
                params.put("isMedia", "true");
                params.put("isRead", "false");
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequestImg);
    }

    ActivityResultLauncher<Intent> getContentImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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

                    if (uris.size() <= 10) {
                        List<String> encodings = new ArrayList<>();
                        List<String> types = new ArrayList<>();

                        try {
                            for (int i = 0; i < uris.size(); i++) {
                                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uris.get(i));
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                encodings.add(Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
                                types.add(".png");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        totalImages = encodings.size();
                        checkImagesToSend();
                        for (int i = 0; i < encodings.size(); i++) {
                            StringRequest stringRequest = getStringRequest(i, encodings, types.get(i));
                            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        }
                    } else Toast.makeText(getApplicationContext(), "Нельзя загрузить больше 10 изображений", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<String> getContentVideo = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                        byte[] bytes;
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        ByteArrayOutputStream output = new ByteArrayOutputStream();

                        try {
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException ignored) {}

                        bytes = output.toByteArray();
                        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPLOAD_IMAGE,
                                response -> sendImage("video&" + Constants.ROOT_URL + response),
                                System.out::println){
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("file", encodedString);
                                params.put("type", ".mp4");
                                return params;
                            }
                        };

                        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                    } catch (FileNotFoundException ignored) {}
                }
            });

    private StringRequest getStringRequest(int i, List<String> encodings, String type) {
        return new StringRequest(Request.Method.POST, Constants.URL_UPLOAD_IMAGE,
                response -> {
                    textToSend.append(Constants.ROOT_URL).append(response).append("&");
                    counterImages++;
                },
                System.out::println){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("file", encodings.get(i));
                params.put("type", type);
                return params;
            }
        };
    }

    @Override
    public void showReplyUI(int position) {
        relReply.setVisibility(View.VISIBLE);
        replyText.setText(Encrypt.decode(messages.get(position).getText().getBytes(), messages.get(position).getDialogId()));
        replyMsgId = messages.get(position).getMessageId();
        relStickers.setVisibility(View.GONE);
        relEdit.setVisibility(View.GONE);
        editMode = false;
        replyMode = true;
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

    public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

        private final Context context;
        private final List<Message> messages;

        public MessagesAdapter(Context context, List<Message> messages) {
            this.context = context;
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder holder;
            if (viewType == 0)
                holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_image_right, parent, false));
            else if (viewType == 1)
                holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false));
            else if (viewType == 2)
                holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_image_left, parent, false));
            else
                holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false));
            holders.add(holder);
            return holder;
        }

        private Handler voiceHandler;

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
            Message message = messages.get(position);

            if (!message.getReplyMsgId().equals("-1")) {
                holder.relReply.setVisibility(View.VISIBLE);
                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i).getMessageId().equals(message.getReplyMsgId())) {
                        String text = Encrypt.decode(messages.get(i).getText().getBytes(), messages.get(i).getDialogId());
                        if (messages.get(i).getIsMedia().equals("false")) holder.replyMsg.setText(text);
                        else {
                            String[] split = message.getText().split("&");
                            if (split[0].equals("sticker")) {
                                holder.replyImg.setVisibility(View.VISIBLE);
                                holder.replyMsg.setVisibility(View.GONE);
                                Glide.with(context).load(split[1]).into(holder.replyImg);
                            } else if(split[0].equals("voice")) {
                                holder.replyImg.setVisibility(View.VISIBLE);
                                holder.replyMsg.setVisibility(View.GONE);
                                holder.replyImg.setImageResource(R.drawable.send_text);
                            } else {
                                holder.replyImg.setVisibility(View.VISIBLE);
                                holder.replyMsg.setVisibility(View.GONE);
                                Glide.with(context).load(split[0])
                                        .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30))).into(holder.replyImg);
                            }
                        }
                    }
                }
                holder.relReply.setOnClickListener(v -> {
                    for (int i = 0; i < messages.size(); i++) {
                        if (messages.get(i).getMessageId().equals(message.getReplyMsgId())) {
                            recyclerView.scrollToPosition(i);
                        }
                    }
                });
            }

            if (message.getIsMedia().equals("false")) {
                holder.text.setText(Encrypt.decode(message.getText().getBytes(), message.getDialogId()));
            } else {
                String[] split = message.getText().split("&");
                if (split[0].equals("sticker")) {
                    Glide.with(context).load(split[1]).into(holder.images);
                    holder.relMsg.setBackgroundResource(0);
                    holder.time.setTextColor(context.getColor(R.color.grey));
                } else if (split[0].equals("voice")) {
                    holder.relImg.setVisibility(View.GONE);
                    holder.relVoice.setVisibility(View.VISIBLE);
                    AtomicInteger playingStatus = new AtomicInteger();
                    MediaPlayer mediaPlayer = new MediaPlayer();

                    holder.playVoice.setOnClickListener(v -> {
                        if (playingStatus.get() == 0) {
                            try {
                                mediaPlayer.reset();
                                mediaPlayer.setAudioAttributes(
                                        new AudioAttributes
                                                .Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .build());
                                mediaPlayer.setDataSource(split[1]);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                playingStatus.set(1);
                                holder.playVoice.setImageResource(com.google.android.gms.R.drawable.cast_ic_mini_controller_pause);
                                holder.visualizerView.setMax(mediaPlayer.getDuration());
                                holder.visualizerView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        if (fromUser) {
                                            mediaPlayer.seekTo(progress);
                                            holder.visualizerView.setProgress(progress);
                                        }
                                    }
                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {}
                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {}
                                });
                            } catch (IOException ignored) {}
                        } else if (playingStatus.get() == 1) {
                            mediaPlayer.pause();
                            playingStatus.set(2);
                            holder.playVoice.setImageResource(R.drawable.send_text);
                        } else if (playingStatus.get() == 2) {
                            mediaPlayer.start();
                            playingStatus.set(1);
                            holder.playVoice.setImageResource(com.google.android.gms.R.drawable.cast_ic_mini_controller_pause);
                        }
                        voiceHandler = new Handler();
                        new Runnable() {
                            @Override
                            public void run() {
                                holder.visualizerView.setProgress(mediaPlayer.getCurrentPosition());
                                voiceHandler.postDelayed(this, 10);
                                if ((mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration())
                                        || playingStatus.get() == 0) {
                                    voiceHandler.removeCallbacks(this);
                                    playingStatus.set(0);
                                    holder.playVoice.setImageResource(R.drawable.send_text);
                                    holder.visualizerView.setProgress(0);
                                }
                            }
                        }.run();
                    });
                } else if (split[0].equals("video")) {
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra("vid", split[1]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    });
                } else {
                    Glide.with(context).load(split[0])
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30))).into(holder.images);
                    if (split.length == 1) {
                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ViewImageActivity.class);
                            intent.putExtra("img", split[0]);
                            startActivity(intent);
                        });
                    } else {
                        holder.imagesCount.setVisibility(View.VISIBLE);
                        holder.imagesCount.setText("+" + (split.length - 1));
                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ViewImagesActivity.class);
                            intent.putExtra("img", message.getText());
                            startActivity(intent);
                        });
                    }
                }
            }

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(Long.parseLong(message.getSendTime()));
            holder.time.setText(simpleDateFormat.format(date));

            if (message.getSender().equals(StaticHelper.me.getPersonalId())) {
                if (message.getIsRead().equals("true")) holder.readStatus.setVisibility(View.VISIBLE);

                if (message.getIsMedia().equals("false")) {
                    holder.itemView.setOnLongClickListener(v -> {
                        relEdit.setVisibility(View.VISIBLE);
                        textEdit.setText(Encrypt.decode(message.getText().getBytes(), message.getDialogId()));
                        editMsgId = message.getMessageId();
                        relStickers.setVisibility(View.GONE);
                        editMode = true;
                        return true;
                    });
                }
            }
            if (!message.getSender().equals(StaticHelper.me.getPersonalId()))
                Glide.with(context).load(user.getMediaLinks().split("&")[0]).circleCrop().into(holder.userImg);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (messages.get(position).getSender().equals(StaticHelper.me.getPersonalId())) {
                if (messages.get(position).getIsMedia().equals("true")) return 0;
                else return 1;
            } else {
                if (messages.get(position).getIsMedia().equals("true")) return 2;
                else return 3;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView text, time, imagesCount, replyMsg;
            public ImageView userImg, readStatus, images, playVoice, replyImg;
            public RelativeLayout relMsg, relImg, relVoice, relReply;
            public SeekBar visualizerView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                time = itemView.findViewById(R.id.time);
                userImg = itemView.findViewById(R.id.user_img);
                readStatus = itemView.findViewById(R.id.read_status);
                images = itemView.findViewById(R.id.images);
                imagesCount = itemView.findViewById(R.id.images_count);
                relMsg = itemView.findViewById(R.id.rel_msg);

                relImg = itemView.findViewById(R.id.rel_image);
                relVoice = itemView.findViewById(R.id.rel_voice);
                playVoice = itemView.findViewById(R.id.play_voice);
                visualizerView = itemView.findViewById(R.id.vis_view);

                relReply = itemView.findViewById(R.id.rel_reply);
                replyMsg = itemView.findViewById(R.id.reply_text);
                replyImg = itemView.findViewById(R.id.reply_img);
            }
        }
    }

    private class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        private final Context context;
        private final List<String> stickers;

        private StickerAdapter(Context context, List<String> stickers) {
            this.context = context;
            this.stickers = stickers;
        }

        @NonNull
        @Override
        public StickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sticker, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StickerAdapter.ViewHolder holder, int position) {
            Glide.with(context).load(stickers.get(position)).into(holder.sticker);
            holder.itemView.setOnClickListener(v -> {
                sendImage("sticker&" + stickers.get(position));
                relStickers.setVisibility(View.GONE);
            });
        }

        @Override
        public int getItemCount() {
            return stickers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView sticker;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                sticker = itemView.findViewById(R.id.image);
            }
        }
    }
}