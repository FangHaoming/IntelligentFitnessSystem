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
import androidx.fragment.app.Fragment;

import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentMineBinding;

public class MineFragment extends Fragment {

    private LayoutFragmentMineBinding binding;
    private SharedPreferences local_sp;
    private SharedPreferences.Editor local_editor;
    private SharedPreferences global_sp;
    private SharedPreferences.Editor global_editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentMineBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        global_sp = getContext().getSharedPreferences("data_global", Context.MODE_PRIVATE);
        local_sp = getContext().getSharedPreferences("data_" + global_sp.getInt("user_id", 0), Context.MODE_PRIVATE);
        local_editor = local_sp.edit();
        global_editor = global_sp.edit();
        binding.fragmentMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsLogin();
            }
        });
        checkIsLogin();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkIsLogin() {
        if (!global_sp.getBoolean("isLogin", false)) {
            Intent toLogin = new Intent(getContext(), LoginActivity.class);
            startActivity(toLogin);
        }
    }
}
