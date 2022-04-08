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
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.activity.ReleaseArticleActivity;
import com.example.intelligentfitnesssystem.activity.SearchActivity;
import com.example.intelligentfitnesssystem.adapter.ArticleAdapter;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.ArticleList;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.databinding.LayoutFragmentCommunityBinding;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.intelligentfitnesssystem.MyApplication.chosenArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.focusArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.getArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.isLogin;
import static com.example.intelligentfitnesssystem.MyApplication.latestArticleList;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.setCurrentTab;


public class CommunityFragment extends Fragment {

    private LayoutFragmentCommunityBinding binding;
    private ArticleAdapter articleAdapter;
    private TextView current;
    private String ArticleListType = "hot";
    private int pageNum = 1;
    private List<Article> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentCommunityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initView();
        current = binding.chosen;
        switch (ArticleListType) {
            case "hot":
                list = chosenArticleList;
                list.clear();
                break;
            case "follow":
                list = focusArticleList;
                list.clear();
                break;
            case "newest":
                list = latestArticleList;
                list.clear();
                break;
        }

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
                switchType(binding.focus, "follow", focusArticleList);
            }
        });
        binding.chosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(binding.chosen, "hot", chosenArticleList);
            }
        });
        binding.latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(binding.latest, "newest", latestArticleList);
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
//                if (!isLogin) {
//                    Intent intent = new Intent(getContext(), LoginActivity.class);
//                    startActivity(intent);
//                    return;
//                }
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

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size() - 1) {
                    loadMore();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("*****fragment start");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("*****fragment pause");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        initView();
        System.out.println("*****fragment Resume");
        System.out.println("*****resume type: " + ArticleListType);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyResponse<ArticleList> result = null;
                try {
                    result = JSON.parseObject(Http.getArticleList(requireContext(), ArticleListType, 1, 10), (Type) MyResponse.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result != null && result.getStatus() == 0) {
                    JSONArray jsonArray = (JSONArray) JSONObject.parseObject(JSON.toJSONString(result.getData())).get("articles");
                    if (jsonArray != null) {
                        for (Object object : jsonArray) {
                            list.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Article.class));
                        }
                    }
                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articleAdapter.setList(list);
                        binding.recyclerView.setAdapter(articleAdapter);
                    }
                });
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("*****fragment destroy");
        binding = null;
    }

    private void initView() {
        if (isLogin) {
            Glide.with(requireContext()).load(requireContext().getString(R.string.baseUrl) + requireContext().getString(R.string.api_get_img) + localUser.getImg()).into(binding.head);
        } else {
            binding.head.setImageResource(R.drawable.user_img);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMore() {
        getArticleList(getContext(), ArticleListType, list, ++pageNum);
        articleAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void switchType(TextView tv, String type, List<Article> list) {
        ArticleListType = type;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        current.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_normal));
        current = tv;
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        current.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_selected));
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyResponse<ArticleList> result = null;
                try {
                    result = JSON.parseObject(Http.getArticleList(requireContext(), type, 1, 10), (Type) MyResponse.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result != null && result.getStatus() == 0) {
                    JSONArray jsonArray = (JSONArray) JSONObject.parseObject(JSON.toJSONString(result.getData())).get("articles");
                    list.clear();
                    if (jsonArray != null) {
                        for (Object object : jsonArray) {
                            list.add(JSONObject.parseObject(((JSONObject) object).toJSONString(), Article.class));
                        }
                    }
                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articleAdapter.setList(list);
                        binding.recyclerView.setAdapter(articleAdapter);
                    }
                });
            }
        }).start();
    }
}
