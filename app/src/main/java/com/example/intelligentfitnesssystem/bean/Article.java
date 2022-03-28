package com.example.intelligentfitnesssystem.bean;

import java.io.Serializable;

public class Article implements Serializable {
    private int id;
    private int userId;
    /**
     * 创建动态的时间戳
     */
    private String createTime;
    /**
     * 动态正文
     */
    private String text;
    /**
     * 图片列表
     */
    private String[] img;

    private int commentCount;
    private int likeCount;
    /**
     * 点赞id数组
     */
    private int[] likeId;
    private Comment[] comments;
    /**
     * 是否为分享动态, 1是0否
     */
    private int isShare;
    private Article shareArticle;

    private Integer shareArticleId;

    private byte[][] imgData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

    public int getIsShare() {
        return isShare;
    }

    public void setIsShare(int isShare) {
        this.isShare = isShare;
    }

    public Article getShareArticle() {
        return shareArticle;
    }

    public void setShareArticle(Article shareArticle) {
        this.shareArticle = shareArticle;
    }

    public Integer getShareArticleId() {
        return shareArticleId;
    }

    public void setShareArticleId(Integer shareArticleId) {
        this.shareArticleId = shareArticleId;
    }

    public byte[][] getImgData() {
        return imgData;
    }

    public void setImgData(byte[][] imgData) {
        this.imgData = imgData;
    }
}