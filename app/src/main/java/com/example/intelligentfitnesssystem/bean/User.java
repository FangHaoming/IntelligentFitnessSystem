package com.example.intelligentfitnesssystem.bean;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String phone;
    private String nickname;
    private String gender;
    private String birth;
    private String img;
    private String imgHex;
    private String pwdHex;
    private Article[] articles;
    private User[] focus;
    private User[] followers;

    public User(int id) {
        this.id = id;
    }
    public User(){

    }

    public Article[] getArticles() {
        return articles;
    }

    public void setArticles(Article[] articles) {
        this.articles = articles;
    }

    public User[] getFocus() {
        return focus;
    }

    public void setFocus(User[] focus) {
        this.focus = focus;
    }

    public User[] getFollowers() {
        return followers;
    }

    public void setFollowers(User[] followers) {
        this.followers = followers;
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
