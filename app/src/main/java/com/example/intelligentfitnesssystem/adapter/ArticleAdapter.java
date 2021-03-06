package com.example.intelligentfitnesssystem.adapter;

import static com.example.intelligentfitnesssystem.MyApplication.commentId;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.intelligentfitnesssystem.activity.ArticleDetailActivity;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.activity.ReleaseArticleActivity;
import com.example.intelligentfitnesssystem.activity.SearchActivity;
import com.example.intelligentfitnesssystem.activity.UserInfoActivity;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.bean.User;
import com.example.intelligentfitnesssystem.util.Http;
import com.example.intelligentfitnesssystem.util.Tools;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ittiger.player.VideoPlayerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Article> list;
    private boolean isPraise = false;

    public ArticleAdapter(Context mContext, List<Article> list) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_article_adapter, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ListViewHolder) {
            int safePosition = holder.getLayoutPosition();
            int adapterPosition = position;
            Article adapter_article = list.get(adapterPosition);
            Article article = list.get(adapterPosition);
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            if (article != null) {
                listViewHolder.nickname.setText(String.valueOf(article.getPublisherName()));
                listViewHolder.createTime.setText(article.getCreateTime());
                if (article.getText() != null && !article.getText().equals("")) {
                    listViewHolder.content_text.setVisibility(View.VISIBLE);
                    listViewHolder.content_text.setText(article.getText());
                }
                if (article.getIsShare() == 1) {
                    listViewHolder.share.setVisibility(View.VISIBLE);
                    Article shareArticle = article.getShareArticle();
                    if (shareArticle.getImg().length > 0 && !shareArticle.getImg()[0].split("\\.")[1].equals("mp4")) {
                        Glide.with(mContext)
                                .load(mContext.getString(R.string.baseUrl) + mContext.getString(R.string.api_get_img) + mContext.getString(R.string.api_get_articleImg) + shareArticle.getImg()[0])
                                .into(listViewHolder.shareImg);
                    } else if (shareArticle.getPublisherImg() != null) {
                        Glide.with(mContext).load(mContext.getString(R.string.baseUrl) + mContext.getString(R.string.api_get_img) + shareArticle.getPublisherImg()).into(listViewHolder.shareImg);
                    }
                    if (shareArticle.getText() != null && !shareArticle.getText().equals("")) {
                        listViewHolder.shareText.setVisibility(View.VISIBLE);
                        listViewHolder.shareText.setText(shareArticle.getText());
                        listViewHolder.shareNickname.setText(shareArticle.getPublisherName());
                    } else {
                        listViewHolder.shareNickname.setText(shareArticle.getPublisherName() + "???????????????");
                    }
                }
                if (article.getPublisherImg() != null) {
                    listViewHolder.head.setTag(safePosition);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + article.getPublisherImg())
                            .into(listViewHolder.head);
                }
                listViewHolder.head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MyResponse<User> result = JSON.parseObject(Http.getUserInfo(mContext, article.getUserId()), (Type) MyResponse.class);
                                    if (result != null && result.getStatus() == 0) {
                                        Intent intent = new Intent(mContext, UserInfoActivity.class);
                                        intent.putExtra("User", JSON.toJSONString(result.getData()));
                                        mContext.startActivity(intent);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                if (article.getImg().length > 0 && !article.getImg()[0].split("\\.")[1].equals("mp4")) {
                    listViewHolder.img_0.setTag(R.id.image_0, adapterPosition);
                    Glide.with(mContext)
                            .load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[0])
                            .placeholder(R.drawable.img_preview)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                                    if (adapterPosition != (Integer) listViewHolder.img_0.getTag(R.id.image_0))
                                        return;
                                    if (article.getImg()[0] != null) {
                                        adjustSize(listViewHolder, article, mContext, adapterPosition);
                                        listViewHolder.img_0.setVisibility(View.VISIBLE);
                                        listViewHolder.img_0.setImageDrawable(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                                    listViewHolder.img_0.setVisibility(View.GONE);
                                    Glide.with(mContext).clear(listViewHolder.img_0);
                                }
                            });
                    if (article.getImg().length > 1 && article.getImg()[1] != null) {
                        listViewHolder.img_1.setTag(R.id.image_1, adapterPosition);
                        Glide.with(mContext)
                                .load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[1])
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                                        if (adapterPosition != (Integer) listViewHolder.img_1.getTag(R.id.image_1))
                                            return;
                                        if (article.getImg().length > 1 && article.getImg()[1] != null) {
                                            adjustSize(listViewHolder, article, mContext, adapterPosition);
                                            listViewHolder.img_1.setVisibility(View.VISIBLE);
                                            listViewHolder.img_1.setImageDrawable(resource);
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                                        listViewHolder.img_1.setVisibility(View.GONE);
                                        Glide.with(mContext).clear(listViewHolder.img_1);
                                    }
                                });
                    }
                    if (article.getImg().length > 2 && article.getImg()[2] != null) {
                        listViewHolder.img_2.setTag(R.id.image_2, adapterPosition);
                        Glide.with(mContext)
                                .load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[2])
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                                        if (adapterPosition != (Integer) listViewHolder.img_2.getTag(R.id.image_2))
                                            return;
                                        if (article.getImg().length > 2 && article.getImg()[2] != null) {
                                            adjustSize(listViewHolder, article, mContext, adapterPosition);
                                            listViewHolder.img_2.setVisibility(View.VISIBLE);
                                            listViewHolder.img_2.setImageDrawable(resource);
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                                        listViewHolder.img_2.setVisibility(View.GONE);
                                        Glide.with(mContext).clear(listViewHolder.img_2);
                                    }
                                });
                    }
                }
                if (article.getImg().length == 1 && article.getImg()[0].split("\\.")[1].equals("mp4")) {
                    listViewHolder.video.setVisibility(View.VISIBLE);
                    listViewHolder.video.bind(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[0]);
                }
                System.out.println("*****position:" + "  " + adapterPosition + " " + listViewHolder.video.getTag(R.id.content_video) + " " + Arrays.toString(article.getImg()));
                listViewHolder.praise_num.setText(String.valueOf(article.getLikeCount()));
                listViewHolder.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext.getApplicationContext(), ArticleDetailActivity.class);
                        intent.putExtra("Article", JSON.toJSONString(article));
                        mContext.startActivity(intent);
                    }
                });
                listViewHolder.praise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext.getApplicationContext(), ArticleDetailActivity.class);
                        intent.putExtra("Article", JSON.toJSONString(article));
                        mContext.startActivity(intent);
                    }
                });
                listViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext.getApplicationContext(), ArticleDetailActivity.class);
                        intent.putExtra("Article", JSON.toJSONString(article));
                        mContext.startActivity(intent);
                    }
                });
                listViewHolder.comment_num.setText(String.valueOf(article.getCommentCount()));
                listViewHolder.transport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext.getApplicationContext(), ReleaseArticleActivity.class);
                        intent.putExtra("Article", JSON.toJSONString(article));
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    /**
     * @param holder
     */
    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ListViewHolder) {
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            listViewHolder.img_0.setVisibility(View.GONE);
            listViewHolder.img_1.setVisibility(View.GONE);
            listViewHolder.img_2.setVisibility(View.GONE);
            listViewHolder.video.bind(null);
            listViewHolder.video.setVisibility(View.GONE);
            listViewHolder.share.setVisibility(View.GONE);
            listViewHolder.content_text.setVisibility(View.GONE);
            Glide.with(mContext).clear(listViewHolder.img_0);
            Glide.with(mContext).clear(listViewHolder.img_1);
            Glide.with(mContext).clear(listViewHolder.img_2);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static void adjustSize(ListViewHolder listViewHolder, Article article, Context mContext, Integer adapterPosition) {
        switch (article.getImg().length) {
            case 1:
                LinearLayout.LayoutParams Params = (LinearLayout.LayoutParams) listViewHolder.img_0.getLayoutParams();
                Params.height = Tools.dip2px(mContext, 200);
                if (adapterPosition == (Integer) listViewHolder.img_0.getTag(R.id.image_0)) {
                    listViewHolder.img_0.setLayoutParams(Params);
                }
                break;
            case 2:
                LinearLayout.LayoutParams Params0 = (LinearLayout.LayoutParams) listViewHolder.img_0.getLayoutParams();
                Params0.height = Tools.dip2px(mContext, 150);
                if (adapterPosition == (Integer) listViewHolder.img_0.getTag(R.id.image_0)) {
                    listViewHolder.img_0.setLayoutParams(Params0);
                }
                LinearLayout.LayoutParams Params1 = (LinearLayout.LayoutParams) listViewHolder.img_1.getLayoutParams();
                Params1.height = Tools.dip2px(mContext, 150);
                if (adapterPosition == (Integer) listViewHolder.img_1.getTag(R.id.image_1)) {
                    listViewHolder.img_1.setLayoutParams(Params1);
                }
                break;
            default:
                LinearLayout.LayoutParams Params00 = (LinearLayout.LayoutParams) listViewHolder.img_0.getLayoutParams();
                Params00.height = Tools.dip2px(mContext, 100);
                if (adapterPosition == (Integer) listViewHolder.img_0.getTag(R.id.image_0)) {
                    listViewHolder.img_0.setLayoutParams(Params00);
                }
                LinearLayout.LayoutParams Params11 = (LinearLayout.LayoutParams) listViewHolder.img_1.getLayoutParams();
                Params11.height = Tools.dip2px(mContext, 100);
                if (adapterPosition == (Integer) listViewHolder.img_1.getTag(R.id.image_1)) {
                    listViewHolder.img_1.setLayoutParams(Params11);
                }
                break;
        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        CircleImageView head;
        TextView nickname;
        TextView createTime;
        Button focus;
        TextView content_text;
        ImageView img_0;
        ImageView img_1;
        ImageView img_2;
        VideoPlayerView video;
        Button praise;
        TextView praise_num;
        Button comment;
        TextView comment_num;
        Button transport;
        LinearLayout content;
        LinearLayout share;
        ImageView shareImg;
        TextView shareNickname;
        TextView shareText;

        public ListViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            nickname = itemView.findViewById(R.id.nickname);
            createTime = itemView.findViewById(R.id.createTime);
            focus = itemView.findViewById(R.id.focus);
            content_text = itemView.findViewById(R.id.content_text);
            img_0 = itemView.findViewById(R.id.image_0);
            img_1 = itemView.findViewById(R.id.image_1);
            img_2 = itemView.findViewById(R.id.image_2);
            video = itemView.findViewById(R.id.content_video);
            praise = itemView.findViewById(R.id.praise);
            praise_num = itemView.findViewById(R.id.praise_num);
            comment = itemView.findViewById(R.id.comment);
            comment_num = itemView.findViewById(R.id.comment_num);
            transport = itemView.findViewById(R.id.transport);
            content = itemView.findViewById(R.id.content);
            share = itemView.findViewById(R.id.share);
            shareImg = itemView.findViewById(R.id.share_img);
            shareNickname = itemView.findViewById(R.id.share_nickname);
            shareText = itemView.findViewById(R.id.share_text);
        }
    }
}
