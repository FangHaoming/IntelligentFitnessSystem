package com.example.intelligentfitnesssystem.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.intelligentfitnesssystem.activity.DetectActivity;
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

public class HomeFragment extends Fragment{

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
    public IConnectionManager manager;
    public SurfaceView surfaceView;

    public TextView pullup;


    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.layout_fragment_home,container,false);

        pullup=root.findViewById(R.id.pullup);
        View.OnClickListener listener=new View.OnClickListener() {
            String type;
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pullup:
                        type="pullup";
                        break;
                    default:
                        break;
                }
                Intent intent=new Intent(getActivity(),DetectActivity.class);
                intent.putExtra("type",type);
                startActivity(intent);
            }
        };
        pullup.setOnClickListener(listener);
        return root;
    }

    /**
     * 获取用户信息
     * @param user_id
     * @param phone
     * @param user_nickname
     */
    public void sendByPost(int user_id,String phone, String user_nickname) {
        User user=new User();
        user.setId(user_id);
        user.setNickname(user_nickname);
        user.setPhone(phone);
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

}


