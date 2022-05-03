package com.example.intelligentfitnesssystem.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.intelligentfitnesssystem.R;

public class LoadingDialog extends DialogFragment {
    private String loadingInfo;
    private TextView mTvLoading;

    private final static String TAG = "LoadingDialog";

    public LoadingDialog setLoadingInfo(String loadingInfo) {
        this.loadingInfo = loadingInfo;
        if (mTvLoading != null) {
            mTvLoading.setText(loadingInfo);
        }
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_loading, container);
        mTvLoading = view.findViewById(R.id.tv_loading);
        if (loadingInfo != null) {
            mTvLoading.setText(loadingInfo);
        }
        return view;
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
