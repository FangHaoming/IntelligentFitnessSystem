package com.example.intelligentfitnesssystem.activity;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.MainActivity;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.ActivityModifyBinding;
import com.example.intelligentfitnesssystem.util.AvatarStudio;
import com.example.intelligentfitnesssystem.util.FileUtils;
import com.example.intelligentfitnesssystem.util.Http;

import static com.example.intelligentfitnesssystem.MyApplication.global_editor;
import static com.example.intelligentfitnesssystem.MyApplication.initApp;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.local_editor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;

public class ModifyInfoActivity extends AppCompatActivity {

    private ActivityModifyBinding binding;
    private User user;
    private byte[] imgData;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        user = new User();

        binding.name.setText(localUser.getNickname());
        binding.birth.setText(localUser.getBirth());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyInfoActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.slide_right_out);
                finish();
            }
        });

        if (localUser.getImg() != null) {
            Glide.with(ModifyInfoActivity.this).load(getString(R.string.baseUrl) + getString(R.string.api_get_img) + localUser.getImg()).into(binding.headImg);
        }
        binding.headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AvatarStudio.Builder(ModifyInfoActivity.this)
                        .needCrop(true)
                        .setTextColor(Color.BLACK)
                        .dimEnabled(true)
                        .setAspect(1, 1)
                        .setOutput(250, 250)
                        .setText("打开相机", "从相册中选取", "取消")
                        .show(new AvatarStudio.CallBack() {
                            @Override
                            public void callback(String uri) {
                                System.out.println("*****uri:" + uri);
                                if (uri != null && !uri.equals("")) {
                                    user.setImg(uri.split("/")[uri.split("/").length-1]);
                                    setAvataor(uri);
                                }
                            }
                        });
            }
        });

        if (localUser.getGender() != null && localUser.getGender().trim().equals("男")) {
            binding.male.setChecked(true);
        } else if (localUser.getGender() != null && localUser.getGender().trim().equals("女")) {
            binding.female.setChecked(true);
        }

        binding.gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        user.setGender("男");
                        break;
                    case R.id.female:
                        user.setGender("女");
                        break;
                }
            }
        });

        binding.birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModifyInfoActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String select_month = String.valueOf(month + 1);
                        String select_day = String.valueOf(dayOfMonth);
                        if (month + 1 < 10) {
                            select_month = "0" + select_month;
                        }
                        if (dayOfMonth < 10) {
                            select_day = "0" + select_day;
                        }
                        binding.birth.setText(year + "/" + select_month + "/" + select_day);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONDAY), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setNickname(binding.name.getText().toString().trim());
                user.setBirth(binding.birth.getText().toString().trim());
                user.setPhone(localUser.getPhone());
                if (binding.pwd.getText().toString().trim().equals(binding.confirmPwd.getText().toString().trim())) {
                    if (!binding.pwd.getText().toString().trim().equals("")) {
                        user.setPwdHex(FileUtils.sha1String(binding.pwd.getText().toString()));
                    } else {
                        user.setPwdHex(localUser.getPwdHex());
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MyResponse<User> result = null;
                            try {
                                System.out.println("******modify req:" + JSON.toJSONString(imgData));
                                result = JSON.parseObject(Http.modifyUser(ModifyInfoActivity.this, user, imgData), (Type) MyResponse.class);
                                if (result != null && result.getStatus() == 0) {
                                    User user_res = JSON.parseObject(JSON.toJSONString(result.getData()), User.class);
                                    System.out.println("******modify res:" + JSON.toJSONString(user_res));
                                    localUser.setImg(user_res.getImg());
                                    localUser.setNickname(user_res.getNickname());
                                    localUser.setGender(user_res.getGender());
                                    localUser.setPwdHex(user_res.getPwdHex());
                                    localUser.setBirth(user_res.getBirth());
                                    local_editor.putString("localUser", JSON.toJSONString(localUser));
                                    local_editor.apply();
                                    Intent intent = new Intent(ModifyInfoActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(ModifyInfoActivity.this, getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(ModifyInfoActivity.this, getResources().getString(R.string.hint_pwd_diff), Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    private void setAvataor(final String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(uri));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    imgData = out.toByteArray();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.headImg.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
