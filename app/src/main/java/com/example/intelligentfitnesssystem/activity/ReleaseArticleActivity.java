package com.example.intelligentfitnesssystem.activity;

import static com.example.intelligentfitnesssystem.MyApplication.isLogin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.ImageBean;
import com.example.intelligentfitnesssystem.databinding.ActivityReleaseArticleBinding;
import com.example.intelligentfitnesssystem.util.GlideV4ImageEngine;
import com.example.intelligentfitnesssystem.util.Loader;
import com.example.intelligentfitnesssystem.widget.SetPermissionDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
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
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class ReleaseArticleActivity extends AppCompatActivity {

    private ActivityReleaseArticleBinding binding;
    private List<ImageBean> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReleaseArticleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReleaseArticleActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        binding.picker.setImageLoaderInterface(new Loader());
        binding.picker.setNewData(list);
        binding.picker.setShowAnim(false);
        binding.picker.setPickerListener(new ImageShowPickerListener() {
            @Override
            public void addOnClickListener(int remainNum) {
                Matisse.from(ReleaseArticleActivity.this)
                        .choose(MimeType.ofAll())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectablePerMediaType(9,1)
                        .gridExpectedSize(1)
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
        binding.picker.show();
        AndPermission.with(ReleaseArticleActivity.this)
                .requestCode(300)
                .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(rationaleListener)
                .callback(ReleaseArticleActivity.this)
                .start();

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
        if (requestCode == 233 && resultCode == RESULT_OK && data != null) {
            List<Uri> uriList = Matisse.obtainResult(data);
            if (uriList.size() == 1) {
                binding.picker.addData(new ImageBean(getRealFilePath(ReleaseArticleActivity.this, uriList.get(0))));
            } else {
                List<ImageBean> list = new ArrayList<>();
                for (Uri uri : uriList) {
                    list.add(new ImageBean(getRealFilePath(ReleaseArticleActivity.this, uri)));
                }
                binding.picker.addData(list);
            }
        }
    }

    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int i, final Rationale rationale) {
            // 自定义对话框。
            AlertDialog.newBuilder(ReleaseArticleActivity.this)
                    .setTitle("请求权限")
                    .setMessage("请求权限")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    }).show();
        }
    };

    public String getRealFilePath(final Context context, final Uri uri) {
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
        return data;
    }
}
