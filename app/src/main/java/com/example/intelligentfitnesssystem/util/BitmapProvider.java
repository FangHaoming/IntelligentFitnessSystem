package com.example.intelligentfitnesssystem.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.intelligentfitnesssystem.bean.VideoFrame;

import java.util.LinkedList;
import java.util.Queue;

import xyz.mylib.creator.IProviderExpand;

public class BitmapProvider implements IProviderExpand<Bitmap> {
    private LinkedList<VideoFrame> list;
    private int index = 0;

    private Queue<byte[]> queue;


    public BitmapProvider(LinkedList<VideoFrame> list) {
        this.list = list;
        queue = new LinkedList<>();

        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(list.get(i).getData(), 0, list.get(i).getData().length);
            queue.add(list.get(i).getData());
            bitmap.recycle();
        }

    }

    @Override
    public void prepare() {
    }

    @Override
    public void finish() {
    }

    @Override
    public void finishItem(Bitmap item) {

    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public int size() {
        return list.size();
    }


    @Override
    public Bitmap next() {
        byte[] bytes = queue.poll();
        index++;
        return BitmapFactory.decodeByteArray(list.get(index-1).getData(), 0, list.get(index-1).getData().length);
    }
}