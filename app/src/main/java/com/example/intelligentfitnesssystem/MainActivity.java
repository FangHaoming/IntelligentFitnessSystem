package com.example.intelligentfitnesssystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTabHost;

import com.example.intelligentfitnesssystem.fragment.CommunityFragment;
import com.example.intelligentfitnesssystem.fragment.PracticeFragment;
import com.example.intelligentfitnesssystem.fragment.MineFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private FragmentTabHost tabHost;
    private String[] tabs = new String[]{"练习", "社区", "我的"};
    private Class[] mFragmentClasses = new Class[]{PracticeFragment.class, CommunityFragment.class, MineFragment.class};
    private int[] selectorImg = new int[]{R.drawable.tab_ic_home_selector,R.drawable.tab_ic_community_selector,R.drawable.tab_ic_mine_selector};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  隐藏状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.layout_main);
        tabHost=findViewById(R.id.container);
        tabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        for (int i = 0; i < 3; i++) {
            tabHost.addTab(tabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i)), mFragmentClasses[i], null);
        }
        // 设置默认tab
        tabHost.setCurrentTab(0);
    }

    private View getTabView(int index) {
        View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tab, null);
        ImageView tabImage = inflate.findViewById(R.id.tab_image);
        TextView tabTitle = inflate.findViewById(R.id.tab_title);
        tabImage.setImageResource(selectorImg[index]); // 通过selector来控制图片的改变
        tabTitle.setText(tabs[index]);// 通过selector来控制文字颜色的改变
        return inflate;
    }
}
