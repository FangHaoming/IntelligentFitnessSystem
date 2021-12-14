package com.example.intelligentfitnesssystem.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.bean.VideoFrame;
import com.example.intelligentfitnesssystem.utils.OkSocketSendData;
import com.example.intelligentfitnesssystem.utils.SendFrameThread;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;


import org.opencv.ml.LogisticRegression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private Camera camera;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Button onButton;
    private Button startButton;
    private TextView txt;
    private SendFrameThread thread;
    private LinkedList<VideoFrame> frames=new LinkedList<>();
    private LinkedList<VideoFrame> rf=new LinkedList<>();
    private int i_rf=0;
    private ImageView iv;
    public int socketHost,socketPort;
    public IConnectionManager manager;


    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.layout_fragment_home,container,false);

        SurfaceView surfaceView=root.findViewById(R.id.sfv);
        holder=surfaceView.getHolder();
        holder.addCallback(this);

        onButton=root.findViewById(R.id.on);
        startButton=root.findViewById(R.id.start);
        txt= root.findViewById(R.id.text);
        iv=root.findViewById(R.id.iv);
        Timer timer=new Timer();

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSocket();
                //requestSocket("pullup",manager);

            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manager.isConnect()){
                    manager.disconnect();
                }
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("timer rf.size()=",rf.size()+"");
                                if(i_rf< rf.size()){
                                    Log.i("timer i=",i_rf+"");
                                    iv.setImageBitmap(BitmapFactory.decodeByteArray(rf.get(i_rf).getData(), 0, rf.get(i_rf).getData().length));
                                    i_rf++;
                                }
                            }
                        });
                    }
                };
                if(i_rf<rf.size()){
                    timer.schedule(task,0,63);
                }else{
                    timer.cancel();
                }
                camera.stopPreview();
                releaseCamera(camera);
            }
        });
        return root;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        //openCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera(camera);
    }


    /**
     * 打开相机
     */
    private void openCamera() {

    }

    public void sendByPost(int user_id,String phone, String user_nickname) {
        User user=new User();
        user.setId(3);
        user.setNickname("大范甘迪个");
        user.setPhone("4522425");
        String path = "http://172.16.213.177:8080"+"/users/";
        OkHttpClient client = new OkHttpClient();
        MediaType JSONTYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody=RequestBody.create(JSONTYPE, JSON.toJSONString(user));
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String info = response.body().string();
                MyResponse<User> user1=JSON.parseObject(info, MyResponse.class);
                Log.i("okhttp",JSON.toJSONString(user1));
            }
        });
    }

    /**
     * 请求socket的host、port
     */
    public void requestSocket() {
        String path = "http://172.16.213.177:8080"+"/postures";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String info = response.body().string();
                JSONObject json=JSON.parseObject(info);
                if(json.getString("msg").equals("success")){
                    JSONObject j =json.getJSONObject("data");
                    initOksocket(j.getString("host"),j.getInteger("port"));
                    manager.connect();
                }
            }
        });
    }
    public void initOksocket(String host,int port){
        ConnectionInfo info = new ConnectionInfo(host, port);
        manager= OkSocket.open(info);
        OkSocketOptions options=manager.getOption();
        OkSocketOptions.Builder builder=new OkSocketOptions.Builder(options);
        builder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                return 16;
            }
            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                return Integer.parseInt(new String(header).trim());
            }
        });
        manager.option(builder.build());
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                super.onSocketConnectionSuccess(info, action);
                Log.i("socket***conn","connected");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }

                        //获取相机参数
                        Camera.Parameters parameters = camera.getParameters();
                        //获取相机支持的预览的大小
                        //Camera.Size previewSize = getCameraPreviewSize(parameters);
                        int width = 640;// previewSizFe.width;
                        int height = 480;// previewSize.height;
                        //设置预览格式（也就是每一帧的视频格式）YUV420下的NV21
                        parameters.setPreviewFormat(ImageFormat.NV21);
                        List<int[]> fps=parameters.getSupportedPreviewFpsRange();
                        parameters.setPreviewFpsRange(fps.get(0)[0],fps.get(0)[1]);
                        //设置预览图像分辨率
                        parameters.setPreviewSize(width, height);
                        //相机旋转90度
                        camera.setDisplayOrientation(90);
                        //配置camera参数
                        camera.setParameters(parameters);
                        try {
                            camera.setPreviewDisplay(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        OkSocketSendData s=new OkSocketSendData();
                        String pos="pullup";
                        s.setVideoFrame(pos.length(),pos.getBytes());
                        manager.send(s);

                        //设置监听获取视频流的每一帧
                        camera.setPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                if (manager.isDisconnecting()) return;
                                YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
                                Bitmap bmp = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                                int len=baos.toByteArray().length;
                                String d=new String(baos.toByteArray());

                                Log.i("socket***sendLen", len+"");
                                Log.i("socket***sendData", d);
                                s.setVideoFrame(baos.toByteArray().length,baos.toByteArray());

                                manager.send(s);
                            }
                        });
                        //调用startPreview()用以更新preview的surface
                        camera.startPreview();
                    }
                }).start();
            }
        });
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                super.onSocketDisconnection(info, action, e);
            }
        });
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(info, action, data);
                int len=Integer.parseInt(new String(data.getHeadBytes()).trim());
                String datastr=new String(data.getBodyBytes());

                Log.i("socket***rlen",len+"");
                Log.i("socket***rdata",new String(data.getBodyBytes()));

                rf.offerLast(new VideoFrame(len,data.getBodyBytes()));
            }
        });
    }

    /**
     * 获取设备支持的最小分辨率
     */
    private Camera.Size getCameraPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        Camera.Size needSize = null;
        for (Camera.Size size : list) {
            if (needSize == null) {
                needSize = size;
                continue;
            }
            if (size.width <= needSize.width) {
                if (size.height < needSize.height) {
                    needSize = size;
                }
            }
        }
        return needSize;
    }

    /**
     * 关闭相机
     */
    public void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;

        }
    }



}


