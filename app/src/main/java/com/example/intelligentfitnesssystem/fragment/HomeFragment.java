package com.example.intelligentfitnesssystem.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.DetectActivity;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.bean.VideoFrame;
import com.example.intelligentfitnesssystem.utils.Tools;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Typeface.BOLD;

public class HomeFragment extends Fragment{

    public LinearLayout layout_type;
    public String type;

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.layout_fragment_home,container,false);
        layout_type=root.findViewById(R.id.layout_type);

        String[] types={"引体向上","仰卧起坐","俯卧撑"};
        String[] types_En={"pullup","situpdown","lieupdown"};
        String[] types_Cl={"#EAAB5D","#BC885A","#FA584C"};
        for(int i=0;i<types.length;i++){
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int padding= Tools.dip2px(getContext(),20);
            lp.setMargins(0,0,0,padding);
            TextView tv=new TextView(getContext());
            tv.setLayoutParams(lp);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.parseColor(types_Cl[i]));
            tv.setPadding(padding,padding,padding,padding);
            tv.setTextAppearance(BOLD);

            tv.setText(types[i]);

            int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type=types_En[finalI];
                    Intent intent=new Intent(getActivity(),DetectActivity.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
            });
            layout_type.addView(tv);

        }
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


