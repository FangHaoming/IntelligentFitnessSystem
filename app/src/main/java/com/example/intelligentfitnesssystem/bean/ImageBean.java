package com.example.intelligentfitnesssystem.bean;

import com.yzs.imageshowpickerview.ImageShowPickerBean;

public class ImageBean extends ImageShowPickerBean {

    private String url;
    private int resId;

    public ImageBean(String url) {
        this.url = url;
    }

    public ImageBean(int resId) {
        this.resId = resId;
    }


    @Override
    public String setImageShowPickerUrl() {
        return url;
    }

    @Override
    public int setImageShowPickerDelRes() {
        return resId;
    }
}
