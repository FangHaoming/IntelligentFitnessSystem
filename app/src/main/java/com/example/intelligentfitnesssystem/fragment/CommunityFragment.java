package com.example.intelligentfitnesssystem.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.activity.ReleaseArticleActivity;
import com.example.intelligentfitnesssystem.activity.SearchActivity;
import com.example.intelligentfitnesssystem.adapter.ArticleAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentCommunityBinding;

import java.util.List;

import static com.example.intelligentfitnesssystem.MyApplication.chosenArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.focusArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.latestArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.setCurrentTab;


public class CommunityFragment extends Fragment {

    private LayoutFragmentCommunityBinding binding;
    private ArticleAdapter articleAdapter;
    private TextView current;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentCommunityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initView();
        current = binding.chosen;

        articleAdapter = new ArticleAdapter(getContext(), chosenArticleList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(articleAdapter);

        binding.focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                switchType(binding.focus, focusArticleList);
            }
        });
        binding.chosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(binding.chosen, chosenArticleList);
            }
        });
        binding.latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(binding.latest, latestArticleList);
            }
        });
        binding.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentTab(2);
            }
        });
        binding.releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getContext(), ReleaseArticleActivity.class);
                startActivity(intent);
            }
        });
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        initView();
//        articleAdapter.setList(chosenArticleList);
//        articleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView(){
        if(isLogin){
            Glide.with(requireContext()).load(requireContext().getString(R.string.baseUrl)+requireContext().getString(R.string.api_get_img)+localUser.getImg()).into(binding.head);
        }else{
            binding.head.setImageResource(R.drawable.user_img);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void switchType(TextView tv, List<Article> list) {
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        current.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_normal));
        current = tv;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        current.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selected));
        articleAdapter.setList(list);
        binding.recyclerView.setAdapter(articleAdapter);
    }
}
