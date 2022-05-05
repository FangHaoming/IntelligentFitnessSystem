package com.example.intelligentfitnesssystem.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.ActivityArticleDetailBinding;
import com.example.intelligentfitnesssystem.util.AppManager;
import com.example.intelligentfitnesssystem.util.EditIsCanUseBtnUtils;
import com.example.intelligentfitnesssystem.util.Http;
import com.example.intelligentfitnesssystem.util.SoftKeyBoardListener;

import static com.example.intelligentfitnesssystem.MyApplication.From;
import static com.example.intelligentfitnesssystem.MyApplication.commentId;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArticleDetailActivity extends AppCompatActivity {


    private ActivityArticleDetailBinding binding;
    private CommentAdapter commentAdapter;
    private Article article = new Article();
    private List<Comment> list = new ArrayList<>();
    private boolean isPraise = false;
    private boolean isFocus = false;
    private int index = -1;

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        article = JSON.parseObject(getIntent().getStringExtra("Article"), (Type) Article.class);
        if (getIntent().getStringExtra("Article") != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyResponse<Article> result = null;
                    try {
                        result = JSON.parseObject(Http.getArticleInfo(ArticleDetailActivity.this, article.getId()), (Type) MyResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (result != null && result.getStatus() == 0) {
                        article = JSON.parseObject(JSON.toJSONString(result.getData()), Article.class);
                        JSONArray jsonArray = (JSONArray) JSONObject.parseObject(JSON.toJSONString(result.getData())).get("comments");
                        if (jsonArray != null) {
                            for (Object object : jsonArray) {
                                list.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Comment.class));
                            }
                        }
                    } else {
                        list = Arrays.asList(article.getComments());
                    }
                    int[] temp = article.getLikeId();
                    sort(temp);
                    isPraise = -1 != binarySearch(temp, localUser.getId());

                    commentAdapter = new CommentAdapter(getApplicationContext(), list);
                    commentAdapter.setEditText(binding.commentInput);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!AppManager.isDestroy(ArticleDetailActivity.this)) {
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(ArticleDetailActivity.this));
                                binding.recyclerView.setAdapter(commentAdapter);
                                updateView();
                            }
                        }
                    });
                }
            }).start();
        }
        if (article.getUserId() != localUser.getId()) {
            binding.focus.setVisibility(View.VISIBLE);
            for (int i = 0; i < localUser.getFocus().length; i++) {
                if (localUser.getFocus()[i].getId() == article.getUserId()) {
                    isFocus = true;
                    index = i;
                    break;
                }
            }
        }

        if (isFocus) {
            binding.focus.setText(getString(R.string.focused));
        } else {
            binding.focus.setText(getString(R.string.focus));
        }
        binding.focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFocus) {
                            try {
                                MyResponse<Object> result = JSON.parseObject(Http.followUser(ArticleDetailActivity.this, article.getUserId()), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    binding.focus.setText(getString(R.string.focused));
                                    List<User> list = new ArrayList<User>(Arrays.asList(localUser.getFocus()));
                                    index = localUser.getFocus().length;
                                    list.add(new User(article.getUserId()));
                                    localUser.setFocus(list.toArray(new User[list.size()]));
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(ArticleDetailActivity.this, getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                MyResponse<Object> result = JSON.parseObject(Http.unFollowUser(ArticleDetailActivity.this, article.getUserId()), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    binding.focus.setText(getString(R.string.focus));
                                    List<User> list = new ArrayList<User>(Arrays.asList(localUser.getFocus()));
                                    list.remove(index);
                                    localUser.setFocus(list.toArray(new User[list.size()]));
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(ArticleDetailActivity.this, getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        isFocus = !isFocus;
                    }
                }).start();

            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                article = article.getShareArticle();
                list = Arrays.asList(article.getComments());
                commentAdapter = new CommentAdapter(getApplicationContext(), list);
                commentAdapter.setEditText(binding.commentInput);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(ArticleDetailActivity.this));
                binding.recyclerView.setAdapter(commentAdapter);
                updateView();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleDetailActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        binding.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isPraise) {
                                MyResponse<Object> result = JSON.parseObject(Http.cancelPraiseArticle(ArticleDetailActivity.this, article.getId()), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    binding.praise.setBackground(ContextCompat.getDrawable(ArticleDetailActivity.this, R.drawable.praise));
                                    article.setLikeCount(article.getLikeCount() - 1);
                                    binding.praiseNum.setText(String.valueOf(article.getLikeCount()));
                                    isPraise = false;
                                }
                            } else {
                                MyResponse<Object> result = JSON.parseObject(Http.praiseArticle(ArticleDetailActivity.this, article.getId()), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    binding.praise.setBackground(ContextCompat.getDrawable(ArticleDetailActivity.this, R.drawable.praise_clicked));
                                    article.setLikeCount(article.getLikeCount() + 1);
                                    binding.praiseNum.setText(String.valueOf(article.getLikeCount()));
                                    isPraise = true;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.commentInput.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) binding.commentInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(binding.commentInput, 0);
            }
        });
        binding.transport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                From = "detail";
                Intent intent = new Intent(ArticleDetailActivity.this, ReleaseArticleActivity.class);
                intent.putExtra("Article", JSON.toJSONString(article));
                startActivity(intent);
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
                                String result = Http.commitComment(ArticleDetailActivity.this, comment);
                                System.out.println("*****comment:" + JSON.toJSONString(comment));
                                JSONObject jsonObject = JSONObject.parseObject(result);
                                System.out.println("*****comment Response:" + result);
                                if (jsonObject.getInteger("status") == 0) {
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                                        @Override
                                        public void run() {
                                            article.setCommentCount((article.getCommentCount() + 1));
                                            binding.commentNum.setText(String.valueOf(article.getCommentCount()));
                                            binding.commentAreaNum.setText("评论(" + article.getCommentCount() + ")");
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

    private void updateView() {
        int[] temp = article.getLikeId();
        sort(temp);
        isPraise = -1 != binarySearch(temp, localUser.getId());
        if (article.getPublisherImg() != null) {
            Glide.with(ArticleDetailActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + article.getPublisherImg()).into(binding.head);
        }
        if (isPraise) {
            binding.praise.setBackground(ContextCompat.getDrawable(ArticleDetailActivity.this, R.drawable.praise_clicked));
        } else {
            binding.praise.setBackground(ContextCompat.getDrawable(ArticleDetailActivity.this, R.drawable.praise));
        }
        binding.commentAreaNum.setText("评论(" + article.getCommentCount() + ")");
        binding.nickname.setText(article.getPublisherName());
        binding.createTime.setText(article.getCreateTime());
        binding.contentText.setText(article.getText());
        binding.praiseNum.setText(String.valueOf(article.getLikeCount()));
        binding.commentNum.setText(String.valueOf(article.getCommentCount()));
        if (article.getImg().length > 0) {
            if (article.getImg()[0].split("\\.")[1].equals("mp4")) {
                binding.contentVideo.setVisibility(View.VISIBLE);
                binding.contentVideo.bind(getString(R.string.baseUrl) + getString(R.string.api_get_img) + getString(R.string.api_get_articleImg) + article.getImg()[0]);
            } else {
                ImageView[] imageViews = {binding.image1, binding.image2, binding.image3, binding.image4, binding.image5, binding.image6, binding.image7, binding.image8, binding.image9};
                for (int i = 0; i < article.getImg().length; i++) {
                    imageViews[i].setVisibility(View.VISIBLE);
                    Glide.with(ArticleDetailActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + getString(R.string.api_get_articleImg) + article.getImg()[i]).into(imageViews[i]);
                }
            }
        }
        if (article.getIsShare() == 1) {
            binding.share.setVisibility(View.VISIBLE);
            Article shareArticle = article.getShareArticle();
            if (shareArticle.getImg().length > 0 && !shareArticle.getImg()[0].split("\\.")[1].equals("mp4")) {
                Glide.with(ArticleDetailActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + getString(R.string.api_get_articleImg) + shareArticle.getImg()[0]).into(binding.shareImg);
            } else if (shareArticle.getPublisherImg() != null) {
                Glide.with(ArticleDetailActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + shareArticle.getPublisherImg()).into(binding.shareImg);
            }
            if (shareArticle.getText() != null && !shareArticle.getText().equals("")) {
                binding.shareText.setVisibility(View.VISIBLE);
                binding.shareText.setText(shareArticle.getText());
                binding.shareNickname.setText(shareArticle.getPublisherName());
            } else {
                binding.shareNickname.setText(shareArticle.getPublisherName() + "分享的动态");
            }
        } else {
            binding.share.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("*****detail pause");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ArticleDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
