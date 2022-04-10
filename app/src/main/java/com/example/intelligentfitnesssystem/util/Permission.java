package com.example.intelligentfitnesssystem.util;

import android.content.Context;
import android.content.DialogInterface;

import com.example.intelligentfitnesssystem.activity.ReleaseArticleActivity;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

public class Permission {

    public static RationaleListener getRationaleListener(Context context) {
        return new RationaleListener() {
            @Override
            public void showRequestPermissionRationale(int i, final Rationale rationale) {
                AlertDialog.newBuilder(context)
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
    }
}
