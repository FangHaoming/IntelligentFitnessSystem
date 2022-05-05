package com.example.intelligentfitnesssystem.activity;

import static com.example.intelligentfitnesssystem.MyApplication.localUser;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.adapter.MineArticleAdapter;
import com.example.intelligentfitnesssystem.adapter.MineUserAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentMineBinding;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private LayoutFragmentMineBinding binding;
    private User user_0;
    private User user;
    private TextView current;
    private Boolean isFocus = false;
    private int index = -1;
    private MineArticleAdapter mineArticleAdapter;
    private MineUserAdapter mineUserAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutFragmentMineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        current = binding.articleBtn;
        if (getIntent() != null && getIntent().getStringExtra("User") != null) {
            user_0 = JSON.parseObject(getIntent().getStringExtra("User"), User.class);
        }
        if (user_0 != null && user_0.getFollowers() == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MyResponse<User> result = JSON.parseObject(Http.getUserInfo(UserInfoActivity.this, user_0.getPhone()), (Type) MyResponse.class);
                        switch (result.getStatus()) {
                            case 0:
                                user = JSON.parseObject(JSON.toJSONString(result.getData()), User.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateView();
                                    }
                                });
                                break;
                            case 1:
                                Looper.prepare();
                                Toast.makeText(UserInfoActivity.this, getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else if(user_0 != null){
            user = user_0;
            updateView();
        }
        binding.bar.setVisibility(View.VISIBLE);
        binding.modify.setVisibility(View.GONE);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        binding.articleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.articleBtn);
                binding.userRv.setVisibility(View.GONE);
                binding.articleRv.setVisibility(View.VISIBLE);
                mineArticleAdapter = new MineArticleAdapter(UserInfoActivity.this, new ArrayList<Article>(Arrays.asList(user.getArticles())));
                binding.articleRv.setLayoutManager(new LinearLayoutManager(UserInfoActivity.this));
                binding.articleRv.setAdapter(mineArticleAdapter);
            }
        });
        binding.focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.focusBtn);
                binding.userRv.setVisibility(View.VISIBLE);
                binding.articleRv.setVisibility(View.GONE);
                mineUserAdapter = new MineUserAdapter(UserInfoActivity.this, new ArrayList<User>(Arrays.asList(user.getFocus())));
                binding.userRv.setLayoutManager(new LinearLayoutManager(UserInfoActivity.this));
                binding.userRv.setAdapter(mineUserAdapter);
            }
        });
        binding.followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.followerBtn);
                binding.userRv.setVisibility(View.VISIBLE);
                binding.articleRv.setVisibility(View.GONE);
                mineUserAdapter = new MineUserAdapter(UserInfoActivity.this, new ArrayList<User>(Arrays.asList(user.getFollowers())));
                binding.userRv.setLayoutManager(new LinearLayoutManager(UserInfoActivity.this));
                binding.userRv.setAdapter(mineUserAdapter);
            }
        });
        binding.focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFocus) {
                            try {
                                MyResponse<Object> result = JSON.parseObject(Http.followUser(UserInfoActivity.this, user.getId()), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    binding.focus.setText(getString(R.string.focused));
                                    List<User> list = new ArrayList<User>(Arrays.asList(localUser.getFocus()));
                                    index = localUser.getFocus().length;
                                    list.add(user);
                                    localUser.setFocus(list.toArray(new User[list.size()]));
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(UserInfoActivity.this, getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                MyResponse<Object> result = JSON.parseObject(Http.unFollowUser(UserInfoActivity.this, user.getId()), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    binding.focus.setText(getString(R.string.focus));
                                    List<User> list = new ArrayList<User>(Arrays.asList(localUser.getFocus()));
                                    list.remove(index);
                                    localUser.setFocus(list.toArray(new User[list.size()]));
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(UserInfoActivity.this, getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
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
    }

    private void updateView() {
        binding.userRv.setVisibility(View.GONE);
        binding.articleRv.setVisibility(View.VISIBLE);
        mineArticleAdapter = new MineArticleAdapter(UserInfoActivity.this, new ArrayList<Article>(Arrays.asList(user.getArticles())));
        binding.articleRv.setLayoutManager(new LinearLayoutManager(UserInfoActivity.this));
        binding.articleRv.setAdapter(mineArticleAdapter);
        if (user.getImg() != null && !user.getImg().equals("")) {
            Glide.with(this).load(getResources().getString(R.string.baseUrl) + getResources().getString(R.string.api_get_img) + user.getImg()).into(binding.userImg);
        }
        binding.userName.setText(user.getNickname());
        Drawable drawable = null;
        if (user.getGender() != null) {
            if (user.getGender().equals("女")) {
                drawable = ContextCompat.getDrawable(UserInfoActivity.this, R.drawable.female);
            } else if (user.getGender().equals("男")) {
                drawable = ContextCompat.getDrawable(UserInfoActivity.this, R.drawable.male);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        binding.userName.setCompoundDrawables(null, null, drawable, null);
        if (user.getFocus() != null && user.getFocus().length > 0) {
            binding.focusNum.setText(String.valueOf(user.getFocus().length));
        } else {
            binding.focusNum.setText("0");
        }
        if (user.getFollowers() != null && user.getFollowers().length > 0) {
            binding.followerNum.setText(String.valueOf(user.getFollowers().length));
        } else {
            binding.followerNum.setText("0");
        }
        if (user.getId() != localUser.getId()) {
            binding.focus.setVisibility(View.VISIBLE);
            for (int i = 0; i < localUser.getFocus().length; i++) {
                if (localUser.getFocus()[i].getId() == user.getId()) {
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
    }

    private void switchTab(TextView tv) {
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        current.setTextColor(ContextCompat.getColor(this, R.color.text_normal));
        current = tv;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        current.setTextColor(ContextCompat.getColor(this, R.color.text_selected));
    }
}
