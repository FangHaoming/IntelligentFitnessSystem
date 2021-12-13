package com.example.intelligentfitnesssystem.bean;

public class MyResponse<T> {
    /**
     * 状态码,成功为0,失败为1
     */
    private int status;
    /**
     * 消息,成功消息或者失败消息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
