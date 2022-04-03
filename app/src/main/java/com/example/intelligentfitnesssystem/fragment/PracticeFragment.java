package com.example.intelligentfitnesssystem.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.intelligentfitnesssystem.activity.DetectActivity;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentPracticeBinding;
import com.example.intelligentfitnesssystem.util.Tools;


import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Typeface.BOLD;

public class PracticeFragment extends Fragment {

    private LayoutFragmentPracticeBinding binding;
    public String type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentPracticeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String[] types = {"引体向上", "仰卧起坐", "俯卧撑"};
        String[] types_En = {"pullup", "situpdown", "lieupdown"};
        String[] types_Cl = {"#EAAB5D", "#BC885A", "#FA584C"};
        for (int i = 0; i < types.length; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int padding = Tools.dip2px(getContext(), 20);
            lp.setMargins(0, 0, 0, padding);
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(lp);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.parseColor(types_Cl[i]));
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextAppearance(BOLD);

            tv.setText(types[i]);

            int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = types_En[finalI];
                    Intent intent = new Intent(getActivity(), DetectActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            });
            binding.type.addView(tv);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


