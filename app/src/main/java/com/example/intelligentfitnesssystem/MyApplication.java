package com.example.intelligentfitnesssystem;

import android.app.Application;

import androidx.fragment.app.FragmentTabHost;

import com.example.intelligentfitnesssystem.bean.User;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    public static Application mApplication;
    public static User localUser = new User();
    public static List<User> follower = new ArrayList<User>();
    public static List<User> focus = new ArrayList<User>();
    public static FragmentTabHost tabHost;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
