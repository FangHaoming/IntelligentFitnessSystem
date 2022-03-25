package com.example.intelligentfitnesssystem.bean;

public class Article {
    private int id;
    private int userId;
    /**
     * 动态正文
     */
    private String text;
    /**
     * 图片列表
     */
    private String[] img;
    private int shareCount;
    private int commentCount;
    private int likeCount;
    private ShareItem[] shares;
    private Comment[] comments;
    private User[] likes;
    /**
     * 是否为分享动态, 1是0否
     */
    private int isShare;
    private Article shareArticle;
}