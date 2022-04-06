package com.example.intelligentfitnesssystem.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.adapter.CommentAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.Comment;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.databinding.ActivityArticleDetailBinding;
import com.example.intelligentfitnesssystem.util.EditIsCanUseBtnUtils;
import com.example.intelligentfitnesssystem.util.Http;
import com.example.intelligentfitnesssystem.util.SoftKeyBoardListener;

import static com.example.intelligentfitnesssystem.MyApplication.commentId;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArticleDetailActivity extends AppCompatActivity {


    private ActivityArticleDetailBinding binding;
    private CommentAdapter commentAdapter;
    private Article article;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        article = JSON.parseObject(getIntent().getStringExtra("Article"), (Type) Article.class);
        List<Comment> list = Arrays.asList(article.getComments()); //TODO test if bug
        commentAdapter = new CommentAdapter(getApplicationContext(), list);
        commentAdapter.setEditText(binding.commentInput);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(commentAdapter);
        if(article.getPublisherImg()!=null){
            Glide.with(this).load(getString(R.string.baseUrl)+getString(R.string.api_get_img)+article.getPublisherImg()).into(binding.head);
        }
        binding.nickname.setText(article.getPublisherName());
        binding.contentText.setText(article.getText());
        binding.praiseNum.setText(String.valueOf(article.getLikeCount()));
        binding.commentNum.setText(String.valueOf(article.getCommentCount()));
        //TODO judge praise and focus
        //TODO image and video
        binding.commentAreaNum.setText("评论("+String.valueOf(article.getCommentCount())+")");
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleDetailActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        binding.commentInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    commentId = -1;
                }
            }
        });
        binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.commentInput.getText().toString().trim().equals("")) {
                    Comment comment = new Comment();
                    if (commentId != -1) {
                        comment.setCommentId(commentId);
                    } else {
                        comment.setCommentId(null);
                    }
                    comment.setArticleId(article.getId());
                    comment.setPublisherId(localUser.getId());
                    comment.setContent(binding.commentInput.getText().toString().trim());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                String result = JSON.parseObject(Http.commitComment(ArticleDetailActivity.this, comment), (Type) MyResponse.class);
                                String result = Http.commitComment(ArticleDetailActivity.this, comment);
                                System.out.println("*****comment:" + JSON.toJSONString(comment));
                                JSONObject jsonObject = JSONObject.parseObject(result);
                                System.out.println("*****comment Response:" + result);
                                if (jsonObject.getInteger("status") == 0) {
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void run() {
                                            if (commentId == -1) {
                                                commentAdapter.addData(JSON.parseObject(JSON.toJSONString(jsonObject.get("data")), Comment.class));
                                            } else {
                                                commentAdapter.addItemCommentCount();
                                            }
                                            binding.commentInput.clearFocus();
                                            binding.commentInput.setText("");
                                            InputMethodManager inputMethodManager = (InputMethodManager) binding.commentInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(), 0);
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(ArticleDetailActivity.this, getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(ArticleDetailActivity.this, getResources().getString(R.string.hint_input), Toast.LENGTH_SHORT).show();
                }

            }
        });

        EditIsCanUseBtnUtils.getInstance()
                .addContext(this)
                .addEdittext(binding.commentInput)
                .setBtn(binding.commentBtn)
                .build();
        SoftKeyBoardListener.setListener(ArticleDetailActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
            }

            @Override
            public void keyBoardHide(int height) {
                commentId = -1;
                binding.commentInput.clearFocus();
            }
        });
    }
}
