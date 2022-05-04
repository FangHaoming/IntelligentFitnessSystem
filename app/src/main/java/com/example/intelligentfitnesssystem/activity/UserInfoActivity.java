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

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
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
    private User user;
    private TextView current;
    private Boolean isFocus = false;
    private int index = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutFragmentMineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        current = binding.articleBtn;
        user = JSON.parseObject(getIntent().getStringExtra("User"), User.class);
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
            }
        });
        binding.focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.focusBtn);
            }
        });
        binding.followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.followerBtn);
            }
        });

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
        binding.focusNum.setText(String.valueOf(user.getFocus().length));
        binding.followerNum.setText(String.valueOf(user.getFollowers().length));

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

    private void switchTab(TextView tv) {
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        current.setTextColor(ContextCompat.getColor(this, R.color.text_normal));
        current = tv;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        current.setTextColor(ContextCompat.getColor(this, R.color.text_selected));
    }
}
