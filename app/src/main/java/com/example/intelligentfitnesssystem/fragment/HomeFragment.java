package com.example.intelligentfitnesssystem.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.VideoFrame;
import com.example.intelligentfitnesssystem.utils.SendFrameThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
                openCamera();
                try {
                    //172.16.213.177
                    thread=new SendFrameThread("172.16.51.247",8004);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                thread.start();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("timer frames.size()=",frames.size()+"");
                                rf=thread.getReturnFrames();
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
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.stopPreview();
                releaseCamera(camera);
                try {
                    thread.exit();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        //设置监听获取视频流的每一帧
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (thread.isRemoteClosed()) return;
                YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
                Bitmap bmp = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                thread.addFrame(new VideoFrame(baos.toByteArray().length,baos.toByteArray()));
            }
        });
        //调用startPreview()用以更新preview的surface
        camera.startPreview();
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


