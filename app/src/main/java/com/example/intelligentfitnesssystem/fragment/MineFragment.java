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

import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.activity.ModifyInfoActivity;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentMineBinding;

import java.util.Objects;

import static com.example.intelligentfitnesssystem.MyApplication.global_sp;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.local_sp;

public class MineFragment extends Fragment {

    private LayoutFragmentMineBinding binding;
    private TextView current;

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
        return view;
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
        System.out.println("*****user_img:" + localUser.getImg());
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
            } else {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.unknown);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
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
