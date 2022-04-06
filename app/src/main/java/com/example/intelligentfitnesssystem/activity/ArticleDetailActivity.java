package com.example.intelligentfitnesssystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.adapter.CommentAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.Comment;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.databinding.ActivityArticleDetailBinding;
import com.example.intelligentfitnesssystem.util.Http;

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
        View view = binding.getRoot();
        setContentView(view);
        article = JSON.parseObject(getIntent().getStringExtra("Article"), (Type) Article.class);
        List<Comment> list = Arrays.asList(article.getComments()); //TODO test if bug
        commentAdapter = new CommentAdapter(getApplicationContext(), list);
        commentAdapter.setEditText(binding.commentInput);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(commentAdapter);
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
                        comment.setArticleId(article.getId());
                    }
                    comment.setPublisherId(localUser.getId());
                    comment.setContent(binding.commentInput.getText().toString().trim());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MyResponse<Comment> result = JSON.parseObject(Http.commitComment(ArticleDetailActivity.this, comment), (Type) MyResponse.class);
                                if (result.getStatus() == 0) {
                                    commentAdapter.addData(result.getData());
                                }
                                else{
                                    Toast.makeText(ArticleDetailActivity.this, getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
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
    }
}
