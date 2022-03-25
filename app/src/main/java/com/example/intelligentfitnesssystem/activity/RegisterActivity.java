package com.example.intelligentfitnesssystem.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.ActivityRegisterBinding;
import com.example.intelligentfitnesssystem.util.EditIsCanUseBtnUtils;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    public String user_name;
    public String user_pwd;
    public String user_confirm_pwd;
    public String user_phone;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });
        EditIsCanUseBtnUtils.getInstance()
                .addContext(this)
                .setBtn(binding.registerBtn)
                .addEdittext(binding.pwd)
                .addEdittext(binding.confirmPwd)
                .addEdittext(binding.name)
                .addEdittext(binding.phone)
                .build();
        //确定
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserNameAndPwdValid()) {
                    System.out.println("****************pwd" + user_pwd);
                    commitRegister(user_name, user_phone, user_pwd);
                }
            }
        });
    }

    private void commitRegister(String nickname, String phone, String pwd) {
        User user = new User();
        user.setPhone(phone);
        user.setPwdHex(pwd);
        user.setNickname(nickname);
        String path = getResources().getString(R.string.baseUrl) + getResources().getString(R.string.registerApi);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(user), TYPE);
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(RegisterActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String info = response.body().string();
                JSONObject json = JSON.parseObject(info);
                System.out.println("**********info" + info);
                switch (Objects.requireNonNull(json.get("msg")).toString()) {
                    case "success":
                        Looper.prepare();
                        Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Looper.loop();
                        break;
                    case "server error":
                        Looper.prepare();
                        Toast.makeText(RegisterActivity.this, "注册失败!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                    case "already exist":
                        Looper.prepare();
                        Toast.makeText(RegisterActivity.this, "该账号已存在!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                }
            }
        });
    }

    public boolean isUserNameAndPwdValid() {
        user_name = binding.name.getText().toString().trim();
        user_pwd = binding.pwd.getText().toString().trim();
        user_confirm_pwd = binding.confirmPwd.getText().toString().trim();
        user_phone = binding.phone.getText().toString().trim();
        if (user_name.equals("")) {
            Toast.makeText(this, "用户名不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_phone.equals("")) {
            Toast.makeText(this, "手机号不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd.equals("")) {
            Toast.makeText(this, "密码不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_confirm_pwd.equals("")) {
            Toast.makeText(this, "请再次输入密码",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!user_pwd.equals(user_confirm_pwd)) {
            Toast.makeText(this, "两次密码不一样", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_phone.length() < 11) {
            Toast.makeText(this, "请输入正确的手机号",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
