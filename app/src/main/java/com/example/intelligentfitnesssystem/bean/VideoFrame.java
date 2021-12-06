package com.example.intelligentfitnesssystem.bean;

import android.util.Log;

import android.util.Log;

public class VideoFrame {
    private final String formatLen;
    private final int len;
    private final byte[] data;

    private String formatLength(int len){
        StringBuilder s= new StringBuilder(String.valueOf(len));
        while (s.length()<16){
            s.append(" ");
        }
        Log.e("d", s.toString());
        return s.toString();
    }

    private int formatLength(String len){
        String[] list=len.split(" ");
        return Integer.parseInt(list[0]);
    }

    public int getLen() {
        return len;
    }

    public byte[] getData() {
        return data;
    }

    public String getFormatLen() {
        return formatLen;
    }

    public VideoFrame(int len, byte[] data){
        this.len = len;
        this.formatLen=formatLength(len);
        this.data = data;
    }

}