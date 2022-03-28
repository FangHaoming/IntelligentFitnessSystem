package com.example.intelligentfitnesssystem.fragment;

import android.os.Bundle;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.adapter.ArticleAdapter;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentCommunityBinding;

import static com.example.intelligentfitnesssystem.MyApplication.chosenArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.focusArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.latestArticleList;


public class CommunityFragment extends Fragment {

    private LayoutFragmentCommunityBinding binding;
    private ArticleAdapter articleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentCommunityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        final TextView[] current = {binding.chosen};
        articleAdapter = new ArticleAdapter(getContext(), chosenArticleList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(articleAdapter);

        binding.focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current[0].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                current[0].setTextColor(getContext().getResources().getColor(R.color.text_normal));
                current[0] = binding.focus;
                articleAdapter = new ArticleAdapter(getContext(), chosenArticleList); //TODO 处理数据变更，通知
                binding.recyclerView.setAdapter(articleAdapter);
            }
        });
        binding.chosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current[0].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                current[0].setTextColor(getContext().getResources().getColor(R.color.text_normal));
                current[0] = binding.chosen;
                articleAdapter = new ArticleAdapter(getContext(), chosenArticleList); //TODO 处理数据变更，通知
                binding.recyclerView.setAdapter(articleAdapter);
            }
        });
        binding.latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current[0].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                current[0].setTextColor(getContext().getResources().getColor(R.color.text_normal));
                current[0] = binding.latest;
                articleAdapter = new ArticleAdapter(getContext(), latestArticleList); //TODO 处理数据变更，通知
                binding.recyclerView.setAdapter(articleAdapter);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
