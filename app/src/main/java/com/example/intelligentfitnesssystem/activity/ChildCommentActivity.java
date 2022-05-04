package com.example.intelligentfitnesssystem.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.adapter.CommentAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.Comment;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.databinding.ActivityChildCommentBinding;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChildCommentActivity extends AppCompatActivity {
    private ActivityChildCommentBinding binding;
    private CommentAdapter adapter;
    private List<Comment> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        int commentId = getIntent().getIntExtra("commentId", -1);
        if (commentId >= 0) {
            new Thread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    try {
                        MyResponse<Object> result = JSON.parseObject(Http.getChildComment(ChildCommentActivity.this, commentId), (Type) MyResponse.class);
                        if (result != null && result.getStatus() == 0) {
                            JSONObject data = JSON.parseObject(JSON.toJSONString(result.getData()));
                            System.out.println("*****subcomment" + data.toJSONString());
                            JSONArray jsonArray = data.getJSONArray("subComments");
                            if (jsonArray != null) {
                                for (Object object : jsonArray) {
                                    list.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Comment.class));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.title.setText(data.getInteger("length") + getString(R.string.comment_count));
                                    adapter = new CommentAdapter(getApplicationContext(), list);
                                    adapter.setIsShowCommentBtn(false);
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(ChildCommentActivity.this));
                                    binding.recyclerView.setAdapter(adapter);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }
}
