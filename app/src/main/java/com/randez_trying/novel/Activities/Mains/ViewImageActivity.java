package com.randez_trying.novel.Activities.Mains;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.randez_trying.novel.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewImageActivity extends AppCompatActivity {

    private String img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView back = findViewById(R.id.back);
        TextView name = findViewById(R.id.img_name);
        ImageView image = findViewById(R.id.image);
        ImageView save = findViewById(R.id.save);

        img = getIntent().getStringExtra("img");
        name.setText(img.replace("https://scripsit-itaque.ru/novel/images/", ""));
        Glide.with(getApplicationContext()).load(img).into(image);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        });
        save.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(ViewImageActivity.this);
            alert.setTitle("Save Image?");

            alert.setPositiveButton("Confirm", (dialog, which) -> {
                image.invalidate();
                BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                try {
                    saveImageBitmap(bitmap);
                } catch (IOException e) {
                    System.out.println(e);
                    Toast.makeText(getApplicationContext(), "Возникла непредвиденная ошибка", Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            alert.create().show();
        });
    }

    private void saveImageBitmap(Bitmap imageBitmap) throws IOException {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//        } else {
//            Toast.makeText(getApplicationContext(), "Не достаточно прав для сохранения", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, permissions(), PackageManager.PERMISSION_GRANTED);
//        }

        ActivityCompat.requestPermissions(this, permissions(), PackageManager.PERMISSION_GRANTED);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
            StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

            File dir = new File(storageVolume.getDirectory().getPath() + "/Pictures/Novel/");
            dir.mkdirs();

            File fileOutput = new File(storageVolume.getDirectory().getPath() + "/Pictures/Novel/"
                    + img.replace("https://scripsit-itaque.ru/novel/images/", ""));
            FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(), "Успешно сохранено", Toast.LENGTH_SHORT).show();
        } else {
            File myDir = new File(Environment.getExternalStorageDirectory(), "/Pictures/Novel/");
            if (!myDir.exists()) myDir.mkdirs();
            File file = new File(myDir, img.replace("https://scripsit-itaque.ru/novel/images/", ""));
            try {
                FileOutputStream out = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

                Toast.makeText(getApplicationContext(), "Успешно сохранено", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.toString()}, new String[]{file.getName()}, null);
        }

    }

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) p = storage_permissions_33;
        else p = storage_permissions;
        return p;
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
}