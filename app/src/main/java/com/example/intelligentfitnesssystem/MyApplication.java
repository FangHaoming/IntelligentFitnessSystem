package com.example.intelligentfitnesssystem;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.ArticleList;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
        if (isLogin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取精选动态
                        MyResponse<ArticleList> hot = JSON.parseObject(Http.getArticleList(context, "hot", 1, 3), (Type) MyResponse.class);
                        if (hot.getStatus() == 0) {
                            JSONArray jsonArray = (JSONArray) JSONObject.parseObject(JSON.toJSONString(hot.getData())).get("articles");
                            System.out.println("*****hot"+jsonArray);
                            if (jsonArray != null) {
                                for (Object object : jsonArray) {
                                    chosenArticleList.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Article.class));
                                }
                            }
                        }
                        //获取精选动态
                        MyResponse<ArticleList> newest = JSON.parseObject(Http.getArticleList(context, "newest", 1, 3), (Type) MyResponse.class);
                        if (newest.getStatus() == 0) {
                            JSONArray jsonArray = (JSONArray) JSONObject.parseObject(JSON.toJSONString(newest.getData())).get("articles");
                            System.out.println("*****newest"+jsonArray);
                            if (jsonArray != null) {
                                for (Object object : jsonArray) {
                                    latestArticleList.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Article.class));
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void setTabHost(FragmentTabHost fragmentTabHost, Context context, FragmentManager fragmentManager) {
        tabHost = fragmentTabHost;
        tabHost.setup(context, fragmentManager, android.R.id.tabcontent);
    }

    public static void setCurrentTab(int number) {
        tabHost.setCurrentTab(number);
    }
}
