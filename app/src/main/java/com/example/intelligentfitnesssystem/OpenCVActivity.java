package com.example.intelligentfitnesssystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRotatedRect;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private CameraBridgeViewBase mOpenCvCameraView;
    private SurfaceHolder mSurfaceHolder = null; // SurfaceHolder对象：(抽象接口)SurfaceView支持类
    private Camera mCamera = null; // Camera对象，相机预览
    private Mat src;

    /**服务器地址*/
    private String pUsername="XZY";
    /**服务器地址*/
    private String serverUrl="172.16.213.177";
    /**服务器端口*/
    private int serverPort=8004;
    /**视频刷新间隔*/
    private int VideoPreRate=1;
    /**当前视频序号*/
    private int tempPreRate=0;
    /**视频质量*/
    private int VideoQuality=85;

    /**发送视频宽度比例*/
    private float VideoWidthRatio=1;
    /**发送视频高度比例*/
    private float VideoHeightRatio=1;

    /**发送视频宽度*/
    private int VideoWidth=320;
    /**发送视频高度*/
    private int VideoHeight=240;
    /**视频格式索引*/
    private int VideoFormatIndex=0;
    /**是否发送视频*/
    private boolean startSendVideo=false;
    /**是否连接主机*/
    private boolean connectedServer=false;

    private Button myBtn01, myBtn02;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_opencv);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView= findViewById(R.id.surfaceview);
        myBtn01=(Button)findViewById(R.id.button1);
        myBtn02=(Button)findViewById(R.id.button2);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();

        //开始连接主机按钮
        myBtn01.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                //Common.SetGPSConnected(LoginActivity.this, false);
                if(connectedServer){//停止连接主机，同时断开传输
                    startSendVideo=false;
                    connectedServer=false;
                    myBtn02.setEnabled(false);
                    myBtn01.setText("开始连接");
                    myBtn02.setText("开始传输");
                    //断开连接
                    Thread th = new MySendCommondThread("PHONEDISCONNECT|"+pUsername+"|");
                    th.start();
                }
                else//连接主机
                {
                    //启用线程发送命令PHONECONNECT
                    Thread th = new MySendCommondThread("PHONECONNECT|"+pUsername+"|");
                    th.start();
                    connectedServer=true;
                    myBtn02.setEnabled(true);
                    myBtn01.setText("停止连接");
                }
            }});

        myBtn02.setEnabled(false);
        myBtn02.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                if(startSendVideo)//停止传输视频
                {
                    startSendVideo=false;
                    myBtn02.setText("开始传输");
                }
                else{ // 开始传输视频
                    startSendVideo=true;
                    myBtn02.setText("停止传输");
                }
            }});
    }

    @Override
    public void onStart()//重新启动的时候
    {
        //读取配置文件
        serverUrl="172.16.213.177";
        String tempStr="8004";
        serverPort=Integer.parseInt(tempStr);
        tempStr="1";
        VideoPreRate=Integer.parseInt(tempStr);
        tempStr="85";
        VideoQuality=Integer.parseInt(tempStr);
        tempStr="100";
        VideoWidthRatio=Integer.parseInt(tempStr);
        tempStr="100";
        VideoHeightRatio=Integer.parseInt(tempStr);
        VideoWidthRatio=VideoWidthRatio/100f;
        VideoHeightRatio=VideoHeightRatio/100f;

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.i("opencv","Internal OpenCV library not found , Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }else{
            Log.i("opencv","OpenCV library found inside package , Using it");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i("opencv", "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i("opencv", "加载失败");
                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        src= new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        src.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat dst = new Mat();

        src = inputFrame.rgba();


        Mat rotateMat = Imgproc.getRotationMatrix2D(new Point(src.rows()/2,src.cols()/2), -90, 1);

        Imgproc.warpAffine(src, dst, rotateMat, dst.size());

        MatOfByte params = new MatOfByte();
        Imgcodecs.imencode(".jpg",dst,params);



        if(startSendVideo){
            //byte[] bytebuffer=new byte[dst.height()*dst.width()];

            //dst.get(0,0,bytebuffer);
            byte[] byteArray=params.toArray();
            try {
                Thread th = new MySendFileThread(byteArray,pUsername,serverUrl,serverPort);
                th.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dst;

    }

    /**发送命令线程*/
    class MySendCommondThread extends Thread{
        private String commond;
        public MySendCommondThread(String commond){
            this.commond=commond;
        }
        public void run(){
            //实例化Socket
            try {
                Socket socket=new Socket("https://172.16.213.177",8004);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(commond);
                out.flush();
            } catch (UnknownHostException e) {

            } catch (IOException e) {

            }
        }
    }

    /**发送文件线程*/
    class MySendFileThread extends Thread{
        private String username;
        private String ipname;
        private int port;
        private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;
        private byte[] myoutputstream;

        public MySendFileThread(byte[] myoutputstream,String username,String ipname,int port){
            this.myoutputstream = myoutputstream;
            this.username=username;
            this.ipname = ipname;
            this.port=port;

        }

        public void run() {
            try{
                //将图像数据通过Socket发送出去
                Socket tempSocket = new Socket(ipname, port);
                outsocket = tempSocket.getOutputStream();
//                StringBufferInputStream StringInputStream=new StringBufferInputStream(myoutputstream.toByteArray().toString());
//                ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
                int amount;
//                while ((amount = StringInputStream.read(byteBuffer)) != -1) {
//                    outsocket.write(byteBuffer, 0, amount);
//                }
//                myoutputstream.flush();
//                myoutputstream.close();
                //byte[] length = int2BytesA(myoutputstream.length,16);
                System.out.println("********lenght"+myoutputstream.length);
                outsocket.write(formatLength(myoutputstream.length).getBytes());
                outsocket.write(myoutputstream);
                outsocket.flush();
                tempSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static byte[] int2BytesA(int n, int len) throws IllegalArgumentException
    {
        if (len <= 0) {
            throw new IllegalArgumentException("Illegal of length");
        }
        byte[] b = new byte[len];
        for (int i = len; i > 0; i--) {
            b[(i - 1)] = ((byte)(n >> 8 * (len - i) & 0xFF));
        }
        return b;
    }
    public static String add(int input){
        String length=String.valueOf(input);
        while(length.length()<16){
            length+=" ";
        }
        return length;
    }

    private String formatLength(int len){
        StringBuilder s= new StringBuilder(String.valueOf(len));
        while (s.length()<16){
            s.append(" ");
        }
        Log.e("d", s.toString());
        return s.toString();
    }
}