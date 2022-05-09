package com.example.intelligentfitnesssystem.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.ImageBean;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.databinding.ActivityReleaseArticleBinding;
import com.example.intelligentfitnesssystem.util.GlideV4ImageEngine;
import com.example.intelligentfitnesssystem.util.Http;
import com.example.intelligentfitnesssystem.util.Loader;
import com.example.intelligentfitnesssystem.util.Permission;
import com.google.common.net.MediaType;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yzs.imageshowpickerview.ImageShowPickerBean;
import com.yzs.imageshowpickerview.ImageShowPickerListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.intelligentfitnesssystem.MyApplication.From;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;

public class ReleaseArticleActivity extends AppCompatActivity {

    private ActivityReleaseArticleBinding binding;
    private List<ImageBean> list = new ArrayList<>();
    private View root;
    private Article article = new Article();
    private Article paramsArticle = new Article();
    private String Type = null;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReleaseArticleBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (From != null && From.equals("detail")) {
                    intent = new Intent(ReleaseArticleActivity.this, ArticleDetailActivity.class);
                    From = null;
                    intent.putExtra("Article", JSON.toJSONString(article));
                    startActivity(intent);
                } else if (From == null) {
                    intent = new Intent(ReleaseArticleActivity.this, MainActivity.class);
                    From = "release";
                    startActivity(intent);
                }
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        binding.picker.setImageLoaderInterface(new Loader());
        binding.picker.setNewData(list);
        binding.picker.setShowAnim(false);
        Type = getIntent().getStringExtra("type");
        if (Type != null) {
            if (Type.equals("photo")) {
                binding.picker.setMaxNum(9);
            }
            if (Type.equals("video")) {
                binding.picker.setMaxNum(1);
            }
        } else {
            binding.picker.setMaxNum(0);
            binding.share.setVisibility(View.VISIBLE);
            article = JSON.parseObject(getIntent().getStringExtra("Article"), Article.class);
            paramsArticle.setShareArticle(article);
            paramsArticle.setIsShare(1);
            paramsArticle.setShareArticleId(article.getId());
            if (article.getImg().length > 0 && !article.getImg()[0].split("\\.")[1].equals("mp4")) {
                Glide.with(ReleaseArticleActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + getString(R.string.api_get_articleImg) + article.getImg()[0]).into(binding.shareImg);
            } else if (article.getPublisherImg() != null) {
                Glide.with(ReleaseArticleActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + article.getPublisherImg()).into(binding.shareImg);
            }
            if (article.getText() != null && !article.getText().equals("")) {
                binding.shareContentText.setVisibility(View.VISIBLE);
                binding.shareContentText.setText(article.getText());
                binding.nickname.setText(article.getPublisherName());
            } else {
                binding.nickname.setText(article.getPublisherName() + "分享的动态");
            }

        }

        binding.releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.contentText.getText().toString().trim().equals("")) {
                    paramsArticle.setText(binding.contentText.getText().toString());
                }
                paramsArticle.setUserId(localUser.getId());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.releaseBtn.setEnabled(false);
                            }
                        });
                        MyResponse<Article> result = null;
                        try {
                            result = JSON.parseObject(Http.commitArticle(ReleaseArticleActivity.this, paramsArticle), (java.lang.reflect.Type) MyResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.releaseBtn.setEnabled(true);
                            }
                        });
                        System.out.println("*****release req" + JSON.toJSONString(paramsArticle));
                        System.out.println("*****release res" + JSON.toJSONString(result));
                        if (result != null && result.getStatus() == 0) {
                            Article newArticle = JSON.parseObject(JSON.toJSONString(result.getData()), Article.class);
                            Intent intent = new Intent(ReleaseArticleActivity.this, MainActivity.class);
                            intent.putExtra("newArticle", JSON.toJSONString(newArticle));
                            startActivity(intent);
                            finish();
                        } else if (result != null) {
                            Looper.prepare();
                            Toast.makeText(ReleaseArticleActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });

        //binding.picker对应布局文件中的ImageShowPickerView
        binding.picker.setPickerListener(new ImageShowPickerListener() {
            @Override
            public void addOnClickListener(int remainNum) {
                Matisse.from(ReleaseArticleActivity.this)
                        .choose(Type != null && Type.equals("photo") ? MimeType.of(MimeType.GIF, MimeType.JPEG, MimeType.PNG, MimeType.WEBP) : MimeType.of(MimeType.MP4))//指定可选类型为图片或视频
                        .showSingleMediaType(true) //设置显示可选项为指定的类型
                        .countable(true) //设置选择数量标记
                        .maxSelectable(remainNum + 1)  //设置最大选择数量
                        .imageEngine(new GlideV4ImageEngine())
                        .forResult(233);
            }


            @Override
            public void picOnClickListener(List<ImageShowPickerBean> list, int position, int remainNum) {

            }

            @Override
            public void delOnClickListener(int position, int remainNum) {

            }
        });
        binding.picker.show(); //显示选择图片/视频的图标“+”

        AndPermission.with(ReleaseArticleActivity.this)
                .requestCode(300)
                .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(Permission.getRationaleListener(ReleaseArticleActivity.this))
                .callback(ReleaseArticleActivity.this)
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) binding.contentText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    @PermissionYes(300)
    private void getPermissionYes(List<String> grantedPermissions) {

    }

    @PermissionNo(300)
    private void getPermissionNo(List<String> deniedPermissions) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("*****real data:" + JSON.toJSONString(data));
        if (requestCode == 233 && resultCode == RESULT_OK && data != null) {
            List<Uri> uriList = Matisse.obtainResult(data);
            list = new ArrayList<>();
            List<byte[]> imgData = new ArrayList<>();
            List<String> img = new ArrayList<>();
            for (Uri uri : uriList) {
                String realPath = getRealFilePath(ReleaseArticleActivity.this, uri);
                list.add(new ImageBean(realPath));
                byte[] img_data = null;
                try {
                    File file = new File(realPath);
                    img_data = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(img_data);
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgData.add(img_data);
                img.add(realPath.split("/")[realPath.split("/").length - 1]);
            }
            paramsArticle.setImgData(imgData.toArray(new byte[imgData.size()][]));
            paramsArticle.setImg(img.toArray(new String[img.size()]));
            binding.picker.addData(list);
        }
    }

    public String getRealFilePath(final Context context, final Uri uri) {
        System.out.println("*****real uri:" + JSON.toJSONString(uri));
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        System.out.println("*****realPath:" + JSON.toJSONString(data));
        return data;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = null;
            if (From != null && From.equals("detail")) {
                intent = new Intent(ReleaseArticleActivity.this, ArticleDetailActivity.class);
                From = null;
                intent.putExtra("Article", JSON.toJSONString(article));
                startActivity(intent);
            } else if (From == null) {
                intent = new Intent(ReleaseArticleActivity.this, MainActivity.class);
                From = "release";
                startActivity(intent);
            }
            overridePendingTransition(0, R.anim.slide_right_out);
            finish();
        }
        return true;
    }
}
