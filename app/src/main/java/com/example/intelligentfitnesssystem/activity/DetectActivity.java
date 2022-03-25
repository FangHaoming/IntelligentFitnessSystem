package com.example.intelligentfitnesssystem.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.VideoFrame;
import com.example.intelligentfitnesssystem.util.OkSocketSendData;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetectActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    public ImageView iv;
    public Button beginBtn;
    public Button endBtn;
    public String type;

    public Camera camera;
    public SurfaceHolder holder;
    public IConnectionManager manager;
    public SurfaceView surfaceView;
    public LinkedList<VideoFrame> rf=new LinkedList<>();
    public int i_rf=0;
    public Timer timer;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detect);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        iv=findViewById(R.id.iv);
        surfaceView=findViewById(R.id.sfv);
        beginBtn=findViewById(R.id.begin);
        endBtn=findViewById(R.id.end);
        holder=surfaceView.getHolder();
        timer=new Timer();
        Intent intent=getIntent();
        type=intent.getStringExtra("type");
        Toast.makeText(DetectActivity.this,type,Toast.LENGTH_SHORT).show();
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.begin:
                        surfaceView.setVisibility(View.VISIBLE);
//                        initOksocket("192.168.43.200", 8004, null);
//                        manager.connect();
                        requestSocket(type);

                        break;
                    case R.id.end:
                        if(manager!=null&&manager.isConnect()){
                            manager.disconnect();
                        }
                        //iv.setImageBitmap(null);
                        surfaceView.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        };
        beginBtn.setOnClickListener(listener);
        endBtn.setOnClickListener(listener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent=new Intent(DetectActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return super.onSupportNavigateUp();
    }

    public void show(){
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
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
    }

    public void initOksocket(String host,int port,String pos){
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
                        DisplayMetrics dm=getResources().getDisplayMetrics();
//                        Camera.Size previewSize=getCameraPreviewSize(parameters);
//                        Camera.Size previewSize = getBestCameraResolution(parameters,getScreenMetrics(DetectActivity.this));
//                        int width=previewSize.width;
//                        int height=previewSize.height;
                        int width=640;
                        int height=480;
//                        int width =dm.widthPixels;// previewSizFe.width; //640
//                        int height = dm.heightPixels;// previewSize.height;//480
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
                        if(pos!=null) {
                            s.setVideoFrame(pos.length(), pos.getBytes());
                            manager.send(s); //先发送要检测的姿态类型
                        }

                        //设置监听获取视频流的每一帧
                        camera.setPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                if (manager.isDisconnecting()) return;
                                data=rotateYUV420Degree90(data,width,height);
                                data=rotateYUV420Degree90(data,height,width);
                                data=rotateYUV420Degree90(data,width,height);
                                YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, height, width, null);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                yuvimage.compressToJpeg(new Rect(0, 0, height, width), 80, baos);
                                Bitmap bmp = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                                s.setVideoFrame(baos.toByteArray().length,baos.toByteArray());
                                manager.send(s); //发送视频帧
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

                rf.offerLast(new VideoFrame(len,data.getBodyBytes())); //存帧
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(BitmapFactory.decodeByteArray(data.getBodyBytes(), 0, len)); //实时显示帧
                    }
                });
            }
        });
    }

    /**
     * 请求socket的host、port
     */
    public void requestSocket(String posture) {
        String path = "http://172.16.213.177:8080"+"/postures/socket/info";
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
                JSONObject json= JSON.parseObject(info);
                if(json.getString("msg").equals("success")){
                    JSONObject j =json.getJSONObject("data");
                    if(manager==null){
                        initOksocket(j.getString("host"),j.getInteger("port"),posture);
                    }
                    if(null!=manager&&!manager.isConnect()){
                        manager.connect();

                    }

                }
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
            camera.release();
            camera = null;

        }
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera(camera);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 获取最佳预览大小
     *
     * @param parameters       相机参数
     * @param screenResolution 屏幕宽高
     * @return
     */
    private Camera.Size getBestCameraResolution(Camera.Parameters parameters, Point screenResolution) {
        float tmp;
        float mindiff = 100f;
        float x_d_y = (float) screenResolution.x / (float) screenResolution.y;
        Camera.Size best = null;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size s : supportedPreviewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - x_d_y);
            if (tmp < mindiff) {
                mindiff = tmp;
                best = s;
            }
        }
        return best;
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new Point(dm.widthPixels, dm.heightPixels);
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera(camera);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(DetectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}

