package com.example.intelligentfitnesssystem.bean;

import com.alibaba.fastjson.serializer.SerializeFilter;

public class User implements SerializeFilter {
    private int id;
    private String phone;
    private String nickname;
    private String gender;
    private String birth;
    private String img;
    private String imgHex;
    private String pwdHex;
    private byte[] imgData;

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgHex() {
        return imgHex;
    }

    public void setImgHex(String imgHex) {
        this.imgHex = imgHex;
    }

    public String getPwdHex() {
        return pwdHex;
    }

    public void setPwdHex(String pwdHex) {
        this.pwdHex = pwdHex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
