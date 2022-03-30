package com.example.intelligentfitnesssystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.ActivityModifyBinding;

import static com.example.intelligentfitnesssystem.MyApplication.global_editor;
import static com.example.intelligentfitnesssystem.MyApplication.initApp;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;

public class ModifyInfoActivity extends AppCompatActivity {

    private ActivityModifyBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogin = false;
                global_editor.putInt("user_id", 0);
                global_editor.putBoolean("isLogin", false);
                global_editor.apply();
                Intent intent = new Intent(ModifyInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
