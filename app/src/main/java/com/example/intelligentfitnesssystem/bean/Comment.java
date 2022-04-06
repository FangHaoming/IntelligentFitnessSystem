package com.example.intelligentfitnesssystem.bean;

import java.io.Serializable;

public class Comment implements Serializable {
    private int id;
    /**
     * 所属的动态
     */
    private int articleId;
    /**
     * 楼中楼所属的评论，不是楼中楼则为null
     */
    private Integer commentId;
    /**
     * 评论者的user_id
     */
    private int publisherId;
    /**
     * 评论时间
     */
    private String createTime;
    /**
     * 评论者的昵称
     */
    private String publisherName;
    /**
     * 评论者的头像
     */
    private String publisherImg;
    private String content;
    private int likeCount;

    private int[] likeId;
    private int commentCount;

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherImg() {
        return publisherImg;
    }

    public void setPublisherImg(String publisherImg) {
        this.publisherImg = publisherImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int[] getLikeId() {
        return likeId;
    }

    public void setLikeId(int[] likeId) {
        this.likeId = likeId;
    }
}