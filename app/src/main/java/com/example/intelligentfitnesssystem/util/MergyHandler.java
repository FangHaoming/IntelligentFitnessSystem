package com.example.intelligentfitnesssystem.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import xyz.mylib.creator.handler.CreatorExecuteResponseHander;

public class MergyHandler implements CreatorExecuteResponseHander {

    LoadingDialog mLoadingDialog;
    String sPath;
    Context context;

    public MergyHandler(LoadingDialog loadingDialog,
                        String sPath,
                        Context context) {
        this.mLoadingDialog = loadingDialog;
        this.sPath = sPath;
        this.context = context;
    }

    @Override
    public void onSuccess(Object message) {

    }

    @Override
    public void onProgress(Object message) {
        mLoadingDialog.setLoadingInfo(message.toString());
    }

    @Override
    public void onFailure(Object message) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {
        mLoadingDialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(sPath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}