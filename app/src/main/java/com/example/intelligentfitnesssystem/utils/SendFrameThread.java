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
        if(socket!=null&&!socket.isClosed()){
            socket.close();
            socket.shutdownOutput();
        }
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("客户端接收到服务器回应的数据：begin" );
                //获取输入流，接收服务器发来的数据
                try {
                    InputStream is = socket.getInputStream();
                    while(!socket.isClosed()){
                        byte[] b=new byte[16];
                        is.read(b);
                        int len=Integer.parseInt(new String(b).trim());
                        Log.i("socket*******len",new String(b).trim());
                        byte[] data=new byte[len];
                        is.read(data);
                        Log.i("socket*******data",new String(data));
                        addFrame(new VideoFrame(len,data));
                    }

//                    InputStreamReader isr = new InputStreamReader(is);
//                    BufferedReader br = new BufferedReader(isr);
//                    String data,sal="",total="",last="",next=""; //str_afeter_len
//                    int len=0,length=0;
//                    boolean hasnext=false;
//                    byte[] b;
//                    //读取客户端数据
//                    while ((data = br.readLine()) != null) {
//                        Log.i("客户端接收到服务器回应的数据行：" ,data);
//                        int n=data.length();
//                        if(len==0) { //读取第一个数据（长度+图片数据）
//                            len = Integer.parseInt(data.substring(0, 16).trim());
//                            length=len;
//                            sal=data.substring(16);
//                            total=total.concat(sal);
//                            len=len-sal.length();
//                        }else{
//                            len=len-data.length();
//                        }
//                        if(hasnext){
//                            next=data.substring(0,16-last.length());
//                            len=Integer.parseInt((last+next).trim());
//                            length=len;
//                            total=total.concat(data.substring(next.length()));//从长度后一位字符开始读
//                            hasnext=false;
//                        }
//
//                        if(len<0){ //如果后续数据（图片）不足一行
//                            total=total.concat(data.substring(0,n+len));
//                            //returnFrames.offerLast(new VideoFrame(len, b));
//                            addFrame(new VideoFrame(length, total.getBytes()));//则这一帧图片读取完毕
//                            total="";
//                            if(len<=-16){
//                                len=Integer.parseInt(data.substring((len+n)%n,(len+n)%n-len).trim());
//                                length=len;
//                                sal=data.substring((len+n)%n-len);
//                                total=total.concat(sal);
//                            }else{
//                                last=data.substring((len+n)%n);
//                                hasnext=true;
//                            }
//                        }else if(len==0){ //如果刚好读完了，就
//                            total=total.concat(data);
//                            addFrame(new VideoFrame(length,total.getBytes()));
//                            total="";
//                        }else{
//                            total=total.concat(data);
//                        }
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("客户端接收到服务器回应的数据：end" );
            }
        }).start();
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