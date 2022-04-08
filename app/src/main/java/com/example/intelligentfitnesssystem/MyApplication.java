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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyApplication extends Application {

    public static Application mApplication;
    public static User localUser = new User();
    public static List<Article> localArticle = new ArrayList<Article>();
    public static List<User> localFollower = new ArrayList<User>();
    public static List<User> localFocus = new ArrayList<User>();
    public static List<Article> chosenArticleList = new ArrayList<>();
    public static List<Article> focusArticleList = new ArrayList<>();
    public static List<Article> latestArticleList = new ArrayList<>();
    public static Boolean isLogin = false;
    public static FragmentTabHost tabHost;
    public static SharedPreferences global_sp;
    public static SharedPreferences local_sp;
    public static SharedPreferences.Editor global_editor;
    public static SharedPreferences.Editor local_editor;
    public static int commentId = -1;

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
        //更新用户信息
        updateUser(context);
        //获取关注列表
        if (isLogin && focusArticleList.size() == 0) {
            getArticleList(context, "follow", focusArticleList, 1);
        }
        //获取精选动态
        if (chosenArticleList.size() == 0) {
            getArticleList(context, "hot", chosenArticleList, 1);
        }
        //获取最新动态
        if (latestArticleList.size() == 0) {
            getArticleList(context, "newest", latestArticleList, 1);
        }
    }

    public static void getArticleList(Context context, String type, List<Article> list, int pageNum) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyResponse<ArticleList> result = null;
                try {
                    result = JSON.parseObject(Http.getArticleList(context, type, pageNum, 10), (Type) MyResponse.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result != null && result.getStatus() == 0) {
                    JSONArray jsonArray = (JSONArray) JSONObject.parseObject(JSON.toJSONString(result.getData())).get("articles");
                    if (jsonArray != null) {
                        for (Object object : jsonArray) {
                            list.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Article.class));
                        }
                    }
                }
            }
        }).start();
    }

    public static void updateUser(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取用户信息
                if (isLogin) {
                    MyResponse<User> result = null;
                    try {
                        result = JSON.parseObject(Http.getUserInfo(context, localUser.getPhone()), (Type) MyResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (result != null &&result.getStatus() == 0) {
                        localUser = JSON.parseObject(JSON.toJSONString(result.getData()), User.class);
                        local_editor.putString("localUser", JSON.toJSONString(localUser));
                        local_editor.apply();
                        localArticle = new ArrayList<Article>(Arrays.asList(localUser.getArticles()));
                        localFocus = new ArrayList<User>(Arrays.asList(localUser.getFocus()));
                        localFollower = new ArrayList<User>(Arrays.asList(localUser.getFollowers()));
                    }
                }
            }
        }).start();
    }

    public static void setTabHost(FragmentTabHost fragmentTabHost, Context context, FragmentManager fragmentManager) {
        tabHost = fragmentTabHost;
        tabHost.setup(context, fragmentManager, android.R.id.tabcontent);
    }

    public static void setCurrentTab(int number) {
        tabHost.setCurrentTab(number);
    }

}
