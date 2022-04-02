package com.example.intelligentfitnesssystem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.ActivitySearchBinding;
import com.example.intelligentfitnesssystem.util.AppManager;
import com.example.intelligentfitnesssystem.util.EditIsCanUseBtnUtils;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        AppManager.addActivity(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        EditIsCanUseBtnUtils.getInstance()
                .addContext(this)
                .addEdittext(binding.input)
                .setBtn(binding.searchBtn)
                .build();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.input.getText().toString().trim();
                boolean isPhoneValid = binding.input.getText().toString().trim().length() == 11;
                if (isPhoneValid) {
                    handleSearch(getApplicationContext(), phone);
                }
            }
        });
    }

    private void handleSearch(Context context, String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyResponse<User> result = JSON.parseObject(Http.getUserInfo(context, phone), (Type) MyResponse.class);
                    switch (result.getStatus()) {
                        case 0:
                            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                            intent.putExtra("User", JSON.toJSONString(result.getData()));
                            startActivity(intent);
                            break;
                        case 1:
                            Looper.prepare();
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void back() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, R.anim.slide_right_out);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.AppExit(this);
        }
        return true;
    }
}
