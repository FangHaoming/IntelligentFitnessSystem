package com.example.intelligentfitnesssystem.util;

import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class OkSocketSendData implements ISendable {
    private String str;
    private byte[] b;

    @Override
    public byte[] parse() {
        ByteBuffer bb=ByteBuffer.allocate(b.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(b);
        byte[] res=bb.array();
        return res;
    }


    public void setVideoFrame(int length,byte[] data){
        String len=formatLength(length);
        ByteArrayOutputStream op=new ByteArrayOutputStream();
        try {
            op.write(len.getBytes());
            op.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        b=op.toByteArray();

    }
    private String formatLength(int len){
        StringBuilder s= new StringBuilder(String.valueOf(len));
        while (s.length()<16){
            s.append(" ");
        }
        return s.toString();
    }
}
