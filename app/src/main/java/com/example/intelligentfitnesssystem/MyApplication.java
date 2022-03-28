package com.example.intelligentfitnesssystem;

import android.app.Application;

import androidx.fragment.app.FragmentTabHost;

import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.User;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    public static Application mApplication;
    public static User localUser = new User();
    public static List<User> follower = new ArrayList<User>();
    public static List<User> focus = new ArrayList<User>();
    public static List<Article> chosenArticleList = new ArrayList<>();
    public static List<Article> focusArticleList = new ArrayList<>();
    public static List<Article> latestArticleList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
