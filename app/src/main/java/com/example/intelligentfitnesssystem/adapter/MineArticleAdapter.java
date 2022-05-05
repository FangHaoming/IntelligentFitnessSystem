package com.example.intelligentfitnesssystem.adapter;

import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.ArticleDetailActivity;
import com.example.intelligentfitnesssystem.activity.ReleaseArticleActivity;
import com.example.intelligentfitnesssystem.activity.UserInfoActivity;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import cn.ittiger.player.VideoPlayerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Article> list;
    private boolean isFocus = false;
    private boolean isPraise = false;
    private User focusedUser = new User();

    public MineArticleAdapter(Context mContext, List<Article> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Article> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_mine_article_adapter, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ListViewHolder) {
            int safePosition = holder.getLayoutPosition();
            int adapterPosition = holder.getBindingAdapterPosition();
            Article article = list.get(safePosition);
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            if (article != null) {
                if (article.getIsShare() == 1) {
                    Article shareArticle = article.getShareArticle();
                    if (shareArticle.getImg().length > 0 && !shareArticle.getImg()[0].split("\\.")[1].equals("mp4")) {
                        Glide.with(mContext).load(mContext.getString(R.string.baseUrl) + mContext.getString(R.string.api_get_img) + mContext.getString(R.string.api_get_articleImg) + shareArticle.getImg()[0]).into(listViewHolder.image);
                    } else if (shareArticle.getPublisherImg() != null) {
                        Glide.with(mContext).load(mContext.getString(R.string.baseUrl) + mContext.getString(R.string.api_get_img) + shareArticle.getPublisherImg()).into(listViewHolder.image);
                    }
                    if (shareArticle.getText() != null && !shareArticle.getText().equals("")) {
                        listViewHolder.text.setText(shareArticle.getText());
                    } else {
                        listViewHolder.text.setText(shareArticle.getPublisherName() + "分享的动态");
                    }
                }else {
                    if (article.getImg().length > 0 && !article.getImg()[0].split("\\.")[1].equals("mp4")){
                        Glide.with(mContext).load(mContext.getString(R.string.baseUrl) + mContext.getString(R.string.api_get_img) + mContext.getString(R.string.api_get_articleImg) + article.getImg()[0]).into(listViewHolder.image);
                    }else if (article.getPublisherImg() != null) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + article.getPublisherImg())
                                .into(listViewHolder.image);
                    }
                    if (article.getText() != null && !article.getText().equals("")) {
                        listViewHolder.text.setText(article.getText());
                    }
                }
                listViewHolder.createTime.setText(article.getCreateTime());
                listViewHolder.praise_num.setText(String.valueOf(article.getLikeCount()));
                listViewHolder.comment_num.setText(String.valueOf(article.getCommentCount()));
                listViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext.getApplicationContext(), ArticleDetailActivity.class);
                        intent.putExtra("Article", JSON.toJSONString(article));
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ListViewHolder) {
            ListViewHolder listViewHolder = (ListViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView createTime;
        TextView text;
        ImageView image;
        TextView praise_num;
        TextView comment_num;
        ConstraintLayout container;

        public ListViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            createTime = itemView.findViewById(R.id.createTime);
            image = itemView.findViewById(R.id.image);
            praise_num = itemView.findViewById(R.id.praise_num);
            comment_num = itemView.findViewById(R.id.comment_num);
            container = itemView.findViewById(R.id.container);

        }
    }
}
