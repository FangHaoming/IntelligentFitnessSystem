package com.example.intelligentfitnesssystem.activity;

import android.Manifest;
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
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.VideoFrame;
import com.example.intelligentfitnesssystem.databinding.ActivityDetectBinding;
import com.example.intelligentfitnesssystem.util.AppManager;
import com.example.intelligentfitnesssystem.util.BitmapProvider;
import com.example.intelligentfitnesssystem.util.FileUtils;
import com.example.intelligentfitnesssystem.util.LoadingDialog;
import com.example.intelligentfitnesssystem.util.MergyHandler;
import com.example.intelligentfitnesssystem.util.OkSocketSendData;
import com.example.intelligentfitnesssystem.util.Permission;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.mylib.creator.handler.CreatorExecuteResponseHander;
import xyz.mylib.creator.task.AvcExecuteAsyncTask;
import xyz.mylib.creator.task.GIFExecuteAsyncTask;

public class DetectActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public String type;
    public Camera CAMERA;
    public SurfaceHolder holder;
    public IConnectionManager manager;
    public LinkedList<VideoFrame> videoFrameList = new LinkedList<>();
    private ActivityDetectBinding binding;
    private boolean isBegin = true;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetectBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //  ??????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        holder = binding.sfv.getHolder();
        mLoadingDialog = new LoadingDialog();
        //????????????
        type = getIntent().getStringExtra("type");

        AndPermission.with(DetectActivity.this)
                .requestCode(300)
                .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(Permission.getRationaleListener(DetectActivity.this))
                .callback(DetectActivity.this)
                .start();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetectActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });

        binding.switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    //record
                    binding.sfv.setVisibility(View.VISIBLE);
//                    initOkSocket("172.16.179.141", 8004, type);
//                    manager.connect();
                    requestSocket(type);
                    binding.switchBtn.setImageResource(R.drawable.stop);
                    binding.download.setVisibility(View.GONE);
                    isBegin = false;
                } else {
                    //stop
                    binding.download.setVisibility(View.VISIBLE);
                    binding.switchBtn.setImageResource(R.drawable.record);
                    if (manager != null && manager.isConnect()) {
                        manager.disconnect();
                    }
                    CAMERA = null;
                    manager.disconnect();
                    manager = null;
                    binding.sfv.setVisibility(View.GONE);
                    binding.switchBtn.setImageResource(R.drawable.record);
                    binding.download.setVisibility(View.VISIBLE);
                    isBegin = true;
                }

            }
        });
        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVideo();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(DetectActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return super.onSupportNavigateUp();
    }


//    binding.iv.setImageBitmap(BitmapFactory.decodeByteArray(rf.get(i_rf).getData(), 0, rf.get(i_rf).getData().length));


    public void initOkSocket(String host, int port, String posture) {
        //?????????????????????Ip???????????????
        ConnectionInfo info = new ConnectionInfo(host, port);
        //??????OkSocket????????????????????????????????????manager
        manager = OkSocket.open(info);
        //?????????????????????????????????????????????
        OkSocketOptions options = manager.getOption();
        //??????????????????????????????????????????????????????builder???
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        //???????????????????????????????????????
        builder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                //????????????????????????16??????
                return 16;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                return Integer.parseInt(new String(header).trim());
            }
        });
        //???????????????????????????????????????????????????????????????
        manager.option(builder.build());
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                super.onSocketConnectionFailed(info, action, e);
                System.out.println("*****Socket fail:" + e);
            }
        });
        //??????Socket?????????????????????
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                super.onSocketConnectionSuccess(info, action);
                System.out.println("*****Socket connected");
                new Thread(new Runnable() {
                    /**
                     *
                     */
                    @Override
                    public void run() {
                        try {
                            if (!AppManager.isDestroy(DetectActivity.this)) {
                                CAMERA = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                            }
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }

                        //??????????????????
                        Camera.Parameters parameters = CAMERA.getParameters();
                        //????????????????????????????????????
                        DisplayMetrics dm = getResources().getDisplayMetrics();
                        Camera.Size previewSize=getCameraPreviewSize(parameters);
//                        Camera.Size previewSize = getBestCameraResolution(parameters, getScreenMetrics(DetectActivity.this));
//                        int width = previewSize.width;
//                        int height = previewSize.height;
//                        System.out.println("*****w&h:" + width + " " + height);
                        int width = 640;
                        int height = 480;
//                        int width =dm.widthPixels;// previewSizFe.width; //640
//                        int height = dm.heightPixels;// previewSize.height;//480
                        //?????????????????????????????????????????????????????????YUV420??????NV21
                        parameters.setPreviewFormat(ImageFormat.NV21);
                        List<int[]> fps = parameters.getSupportedPreviewFpsRange();
                        parameters.setPreviewFpsRange(fps.get(0)[0], fps.get(0)[1]);
                        //???????????????????????????
                        parameters.setPreviewSize(width, height);
                        //????????????90???
                        if (manager != null) {
//                            CAMERA.stopPreview();
                            CAMERA.setDisplayOrientation(90);
                            //??????CAMERA??????
                            CAMERA.setParameters(parameters);
//                            CAMERA.startPreview();
                            try {
                                CAMERA.setPreviewDisplay(holder);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //?????????OkSocket?????????????????????
                            OkSocketSendData s = new OkSocketSendData();
                            if (posture != null) {
                                s.setVideoFrame(posture.length(), posture.getBytes());
                                manager.send(s); //?????????????????????????????????
                            }

                            //???????????????????????????????????????
                            CAMERA.setPreviewCallback(new Camera.PreviewCallback() {
                                @Override
                                public void onPreviewFrame(byte[] data, Camera camera) {
                                    if (manager == null || manager.isDisconnecting()) return;
                                    data = rotateYUV420Degree90(data, width, height);
                                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                                    Camera.getCameraInfo(0, cameraInfo);
                                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                        // ?????????????????????
                                    } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                        // ?????????????????????
                                        data = rotateYUV420Degree90(data, height, width);
                                        data = rotateYUV420Degree90(data, width, height);
                                    }
                                    YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, height, width, null);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    yuvimage.compressToJpeg(new Rect(0, 0, height, width), 80, baos);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                                    s.setVideoFrame(baos.toByteArray().length, baos.toByteArray());
                                    manager.send(s); //???????????????
                                }
                            });
                            //??????startPreview()????????????preview???surface
                            CAMERA.startPreview();
                        }
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
        //??????Socket????????????????????????????????????
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(info, action, data);
                int len = Integer.parseInt(new String(data.getHeadBytes()).trim());

                videoFrameList.offerLast(new VideoFrame(len, data.getBodyBytes())); //??????
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!AppManager.isDestroy(DetectActivity.this)) {
                            binding.iv.setImageBitmap(BitmapFactory.decodeByteArray(data.getBodyBytes(), 0, len)); //???????????????
                        }
                    }
                });
            }
        });
    }

    /**
     * ??????socket???host???port
     */
    public void requestSocket(String posture) {
        String path = getString(R.string.baseUrl) + "postures/socket/info";
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
                System.out.println("*****socket res:" + info);
                JSONObject json = JSON.parseObject(info);
                if (json.getInteger("status") == 0) {
                    JSONObject j = json.getJSONObject("data");
                    if (manager == null) {
                        initOkSocket(j.getString("host"), j.getInteger("port"), posture);
//                        initOkSocket("40f730q296.qicp.vip",41146,posture);
                    }
                    if (null != manager && !manager.isConnect()) {
                        manager.connect();
                    }

                }
            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    void saveVideo() {
        if (videoFrameList.size() <= 0) {
            Toast.makeText(DetectActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        mLoadingDialog.show(getSupportFragmentManager());
        String name = FileUtils.sha1String(videoFrameList.get(0).getData()) + ".mp4";
        File saveDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "IFS");
        if (!saveDir.exists()) saveDir.mkdir();
        String PATH = saveDir.getPath() + "/" + name;
        CreatorExecuteResponseHander handler = new MergyHandler(mLoadingDialog, PATH, this);
        AvcExecuteAsyncTask.execute(new BitmapProvider(videoFrameList), 16, handler, PATH);
    }


    /**
     * ????????????????????????????????????
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
     * ????????????
     */
    public void releaseCamera(Camera CAMERA) {
        if (DetectActivity.this.CAMERA != null) {
            DetectActivity.this.CAMERA.release();
            DetectActivity.this.CAMERA = null;

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
        releaseCamera(CAMERA);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * ????????????????????????
     *
     * @param parameters       ????????????
     * @param screenResolution ????????????
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
     * ???????????????????????????????????????px
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new Point(dm.widthPixels, dm.heightPixels);
    }

    @PermissionYes(300)
    private void getPermissionYes(List<String> grantedPermissions) {

    }

    @PermissionNo(300)
    private void getPermissionNo(List<String> deniedPermissions) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera(CAMERA);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(DetectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}

