package com.example.intelligentfitnesssystem.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligentfitnesssystem.databinding.ActivityArticleDetailBinding;

public class ArticleDetailActivity extends AppCompatActivity {


    private ActivityArticleDetailBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }
}
