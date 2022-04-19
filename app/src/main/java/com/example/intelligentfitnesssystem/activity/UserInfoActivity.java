package com.example.intelligentfitnesssystem.activity;

import static com.example.intelligentfitnesssystem.MyApplication.localUser;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentMineBinding;

public class UserInfoActivity extends AppCompatActivity {

    private LayoutFragmentMineBinding binding;
    private User user;
    private TextView current;

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
    }

    private void switchTab(TextView tv) {
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        current.setTextColor(ContextCompat.getColor(this, R.color.text_normal));
        current = tv;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        current.setTextColor(ContextCompat.getColor(this, R.color.text_selected));
    }
}
