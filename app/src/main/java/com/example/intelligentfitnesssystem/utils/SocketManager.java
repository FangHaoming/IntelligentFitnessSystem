package com.example.intelligentfitnesssystem.utils;

import android.content.Context;
import android.widget.Toast;

import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

public class SocketManager {
    private IConnectionManager manager;
    private Context context;

    public SocketManager(Context context, String host, int port) {
        this.manager=OkSocket.open(new ConnectionInfo(host, port));
        this.context=context;
    }

    public void registReceiver(SocketActionAdapter sa){
        if(sa!=null){
            manager.registerReceiver(sa);
        }else{
            manager.registerReceiver(new SocketActionAdapter() {
                @Override
                public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                    Toast.makeText(context,"连接成功",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void build(){
        OkSocketOptions.Builder builder=new OkSocketOptions.Builder(manager.getOption());
        manager.option(builder.build());
    }
}
