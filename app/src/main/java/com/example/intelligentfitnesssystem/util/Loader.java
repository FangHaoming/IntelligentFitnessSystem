package com.example.intelligentfitnesssystem.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yzs.imageshowpickerview.ImageLoader;

public class Loader extends ImageLoader {

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }

    @Override
    public void displayImage(Context context, Integer resId, ImageView imageView) {
        imageView.setImageResource(resId);
    }
}