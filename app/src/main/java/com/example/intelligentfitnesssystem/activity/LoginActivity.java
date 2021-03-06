package com.example.intelligentfitnesssystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.ActivityLoginBinding;
import com.example.intelligentfitnesssystem.util.AppManager;
import com.example.intelligentfitnesssystem.util.EditIsCanUseBtnUtils;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;

import static com.example.intelligentfitnesssystem.MyApplication.global_sp;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.local_editor;
import static com.example.intelligentfitnesssystem.MyApplication.global_editor;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    /**
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.registerBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.registerBtn.getPaint().setAntiAlias(true);
        EditIsCanUseBtnUtils.getInstance()
                .addContext(this)
                .addEdittext(binding.phone)
                .addEdittext(binding.pwd)
                .setBtn(binding.loginBtn)
                .build();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        //????????????
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //????????????
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.phone.getText().length() < 11) {
                    Toast.makeText(LoginActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                } else if (binding.pwd.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                } else {
                    handleLogin(binding.phone.getText().toString().trim(), binding.pwd.getText().toString().trim());
                }

            }
        });
    }

    private void handleLogin(String phone, String pwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = Http.commitLogin(getApplicationContext(), phone, pwd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (response == null) {
                }
                JSONObject result = JSON.parseObject(response);
                JSONObject data = (JSONObject) result.get("data");
                switch (result.getInteger("status")) {
                    case 0:
                        System.out.println("*****login res:" + JSON.toJSONString(data));
                        assert data != null;
                        localUser = (User) JSONObject.parseObject(data.toJSONString(), User.class);
                        local_editor.putString("token", result.getString("token"));
                        local_editor.putString("localUser", data.toJSONString());
                        global_editor.putBoolean("isLogin", true);
                        isLogin = true;
                        local_editor.apply();
                        global_editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                    case 1:
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                    case 2:
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "??????????????????!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                    case -1:
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "?????????????????????!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, R.anim.slide_right_out);
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
