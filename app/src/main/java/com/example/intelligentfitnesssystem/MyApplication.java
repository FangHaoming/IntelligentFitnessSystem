package com.example.intelligentfitnesssystem;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;

import com.alibaba.fastjson.JSONObject;
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
    public static Boolean isLogin = false;
    public static FragmentTabHost tabHost;
    public static SharedPreferences global_sp;
    public static SharedPreferences local_sp;
    public static SharedPreferences.Editor global_editor;
    public static SharedPreferences.Editor local_editor;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static void initApp(Context context) {
        global_sp = context.getSharedPreferences("data_global", MODE_PRIVATE);
        local_sp = context.getSharedPreferences("data_" + global_sp.getInt("user_id", 0), MODE_PRIVATE);
        global_editor = global_sp.edit();
        local_editor = local_sp.edit();
        if (local_sp.getString("localUser", "") != null && !local_sp.getString("localUser", "").equals("")) {
            localUser = (User) JSONObject.parseObject(local_sp.getString("localUser", ""), User.class);
        } else
            localUser = new User();
        isLogin = global_sp.getBoolean("isLogin", false);
    }

    public static void setTabHost(FragmentTabHost fragmentTabHost, Context context, FragmentManager fragmentManager) {
        tabHost = fragmentTabHost;
        tabHost.setup(context, fragmentManager, android.R.id.tabcontent);
    }

    public static void setCurrentTab(int number) {
        tabHost.setCurrentTab(number);
    }
}
