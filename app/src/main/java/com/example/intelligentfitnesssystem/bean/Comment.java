package com.example.intelligentfitnesssystem.bean;

public class Comment {
    private int id;
    /**
     * 所属的动态
     */
    private int articleId;
    /**
     * 楼中楼所属的评论，不是楼中楼则为null
     */
    private int commentId;
    private long timestamp;
    private User publisher;
    private String text;
    private int like;
    private int dislike;
}