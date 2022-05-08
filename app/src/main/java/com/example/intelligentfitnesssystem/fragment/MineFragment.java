package com.example.intelligentfitnesssystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.activity.ModifyInfoActivity;
import com.example.intelligentfitnesssystem.adapter.MineArticleAdapter;
import com.example.intelligentfitnesssystem.adapter.MineUserAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentMineBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.example.intelligentfitnesssystem.MyApplication.global_sp;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.local_sp;

public class MineFragment extends Fragment {

    private LayoutFragmentMineBinding binding;
    private TextView current;
    private MineArticleAdapter mineArticleAdapter;
    private MineUserAdapter mineUserAdapter;
    private boolean isArticle = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentMineBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        current = binding.articleBtn;
        checkIsLogin();
        binding.fragmentMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsLogin();
            }
        });
        binding.modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsLogin();
                if (isLogin) {
                    Intent intent = new Intent(getContext(), ModifyInfoActivity.class);
                    startActivity(intent);
                }
            }
        });

        initView();

        binding.articleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.articleBtn);
                binding.userRv.setVisibility(View.GONE);
                binding.articleRv.setVisibility(View.VISIBLE);
                mineArticleAdapter = new MineArticleAdapter(requireContext(), new ArrayList<Article>(Arrays.asList(localUser.getArticles())));
                binding.articleRv.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.articleRv.setAdapter(mineArticleAdapter);
                isArticle = true;
            }
        });
        binding.focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.focusBtn);
                binding.userRv.setVisibility(View.VISIBLE);
                binding.articleRv.setVisibility(View.GONE);
                mineUserAdapter = new MineUserAdapter(requireContext(), new ArrayList<User>(Arrays.asList(localUser.getFocus())));
                binding.userRv.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.userRv.setAdapter(mineUserAdapter);
                isArticle = false;
            }
        });
        binding.followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(binding.followerBtn);
                binding.userRv.setVisibility(View.VISIBLE);
                binding.articleRv.setVisibility(View.GONE);
                mineUserAdapter = new MineUserAdapter(requireContext(), new ArrayList<User>(Arrays.asList(localUser.getFollowers())));
                binding.userRv.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.userRv.setAdapter(mineUserAdapter);
                isArticle = false;
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void switchTab(TextView tv) {
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        current.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_normal));
        current = tv;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        current.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selected));
    }


    private void checkIsLogin() {
        if (!isLogin) {
            Intent toLogin = new Intent(getContext(), LoginActivity.class);
            startActivity(toLogin);
        }
    }

    private void initView() {
        if(isArticle && isLogin){
            binding.userRv.setVisibility(View.GONE);
            binding.articleRv.setVisibility(View.VISIBLE);
            mineArticleAdapter = new MineArticleAdapter(requireContext(), new ArrayList<Article>(Arrays.asList(localUser.getArticles())));
            binding.articleRv.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.articleRv.setAdapter(mineArticleAdapter);
        }
        if (isLogin) {
            if (localUser.getImg() != null && !localUser.getImg().equals("")) {
                Glide.with(requireContext()).load(requireContext().getResources().getString(R.string.baseUrl) + requireContext().getResources().getString(R.string.api_get_img) + localUser.getImg()).into(binding.userImg);
            }
            binding.userName.setText(localUser.getNickname());
            Drawable drawable = null;
            if (localUser.getGender() != null) {
                if (localUser.getGender().equals("女")) {
                    drawable = ContextCompat.getDrawable(requireContext(), R.drawable.female);
                } else if (localUser.getGender().equals("男")) {
                    drawable = ContextCompat.getDrawable(requireContext(), R.drawable.male);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            }
            binding.userName.setCompoundDrawables(null, null, drawable, null);
            binding.focusNum.setText(String.valueOf(localUser.getFocus().length));
            binding.followerNum.setText(String.valueOf(localUser.getFollowers().length));
        } else {
            binding.userImg.setImageResource(R.drawable.user_img);
            binding.userName.setCompoundDrawables(null, null, null, null);
            binding.userName.setText(R.string.click_to_login);
            binding.focusNum.setText("-");
            binding.followerNum.setText("-");
        }
    }


}
