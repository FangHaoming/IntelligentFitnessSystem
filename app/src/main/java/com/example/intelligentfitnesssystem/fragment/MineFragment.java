package com.example.intelligentfitnesssystem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentMineBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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

    private void checkIsLogin() {
        if (!isLogin) {
            Intent toLogin = new Intent(getContext(), LoginActivity.class);
            startActivity(toLogin);
        }
    }

    private void initView(){
        if (isLogin) {
            if (localUser.getImg() != null && !localUser.getImg().equals("")) {
                Glide.with(requireContext()).load(requireContext().getResources().getString(R.string.baseUrl) + requireContext().getResources().getString(R.string.api_get_img)+localUser.getImg()).into(binding.userImg);
            }
            binding.userName.setText(localUser.getNickname());
            //TODO 设置用户信息
        }else{
            binding.userImg.setImageResource(R.drawable.user_img);
            binding.userName.setText(R.string.click_to_login);
        }
    }


}
