package com.example.intelligentfitnesssystem.utils;

import android.util.Log;

import com.example.intelligentfitnesssystem.bean.VideoFrame;

import org.opencv.video.Video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class SendFrameThread extends Thread {
    private final String host;
    private final int port;
    private final LinkedList<VideoFrame> frames = new LinkedList<>();
    private final LinkedList<VideoFrame> returnFrames = new LinkedList<>();
    private Socket socket;
    private OutputStream outputStream;
    private boolean stop = false;
    private boolean close = false;

    public void addFrame(VideoFrame frame) {
        frames.offerLast(frame);
    }

    public void addRtFrame(VideoFrame frame) {
        returnFrames.offerLast(frame);
    }

    public LinkedList<VideoFrame> getReturnFrames() {
        return returnFrames;
    }

    public SendFrameThread(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    public boolean isRemoteClosed() {
        return close;
    }

    public void exit() throws IOException {
        this.stop = true;
        socket.shutdownOutput();
        socket.close();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();
            if (socket.isConnected()) {
                Log.i("socket", "connected");
            }
            close = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        do {
            while (close) {
                try {
                    socket = new Socket(host, port);
                    outputStream = socket.getOutputStream();
                    close = false;
                } catch (Exception se) {
                    se.printStackTrace();
                    close = true;
                }
            }
            //获取输入流，接收服务器发来的数据
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String data;
                //读取客户端数据
                while ((data = br.readLine()) != null) {
                    System.out.println("客户端接收到服务器回应的数据：" + data);
                    returnFrames.offerLast(new VideoFrame(16, data.getBytes()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (!frames.isEmpty()) {
                VideoFrame frame = frames.pollFirst();
                try {
                    outputStream.write(frame.getFormatLen().getBytes());
                    outputStream.write(frame.getData());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    close = true;
                }
            }
        } while (!this.stop);
    }


}