package com.example.intelligentfitnesssystem.util;

import java.io.File;

import xyz.mylib.creator.handler.CreatorExecuteResponseHander;

public class MergyHandler implements CreatorExecuteResponseHander {

    LoadingDialog mLoadingDialog;
    String sPath;
    String dPath;

    public MergyHandler(LoadingDialog loadingDialog,
                        String sPath,
                        String dPath) {
        this.mLoadingDialog = loadingDialog;
        this.sPath = sPath;
        this.dPath = dPath;
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
        File dfile = new File(dPath);
        if (dfile.exists()) {
            dfile.delete();
        }
//        FileUtils.copyFile(sPath, dPath);
    }
}