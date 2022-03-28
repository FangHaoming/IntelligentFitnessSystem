package com.example.intelligentfitnesssystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.activity.LoginActivity;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.util.List;

import cn.ittiger.player.VideoPlayerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Article> list;

    public ArticleAdapter(Context mContext, List<Article> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_article_adapter, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ListViewHolder) {
            Glide.with(mContext).load(list.get(position).getImg()[0]).into(((ListViewHolder) holder).head); //TODO 改为头像路径
            ((ListViewHolder) holder).nickname.setText(list.get(position).getUserId());//TODO 改为昵称
            ((ListViewHolder) holder).focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Http.followUser(mContext, list.get(position).getUserId());
                        if (((ListViewHolder) holder).focus.getText().equals(mContext.getResources().getString(R.string.focus))) {
                            ((ListViewHolder) holder).focus.setText(mContext.getResources().getString(R.string.focused));
                        } else {
                            ((ListViewHolder) holder).focus.setText(mContext.getResources().getString(R.string.focus));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            ((ListViewHolder) holder).content_text.setText(list.get(position).getText());
            if (list.get(position).getImg().length > 0 && !list.get(position).getImg()[0].split("/.")[1].equals("mp4")) {
                if (list.get(position).getImg()[0] != null) {
                    ((ListViewHolder) holder).img_0.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + list.get(position).getImg()[0]).into(((ListViewHolder) holder).img_0);
                }
                if (list.get(position).getImg()[1] != null) {
                    ((ListViewHolder) holder).img_1.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + list.get(position).getImg()[1]).into(((ListViewHolder) holder).img_1);
                }
                if (list.get(position).getImg()[2] != null) {
                    ((ListViewHolder) holder).img_2.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + list.get(position).getImg()[2]).into(((ListViewHolder) holder).img_2);
                }
            }
            if (list.get(position).getImg().length == 1 && list.get(position).getImg()[0].split("/.")[1].equals("mp4")) {
                ((ListViewHolder) holder).video.setVisibility(View.VISIBLE);
                ((ListViewHolder) holder).video.bind(list.get(position).getImg()[0]);
            }

            ((ListViewHolder) holder).praise.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    //TODO 根据isLike判断样式
                    ((ListViewHolder) holder).praise.setBackground(mContext.getDrawable(R.drawable.praise_clicked));
                }
            });
            ((ListViewHolder) holder).praise_num.setText(list.get(position).getLikeCount());
            ((ListViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext.getApplicationContext(), LoginActivity.class); //TODO 跳转到动态详情页
                    mContext.startActivity(intent);
                }
            });
            ((ListViewHolder) holder).comment_num.setText(list.get(position).getCommentCount());
            ((ListViewHolder) holder).transport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext.getApplicationContext(), LoginActivity.class); //TODO 跳转到发布动态页
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        CircleImageView head;
        TextView nickname;
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

        public ListViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            nickname = itemView.findViewById(R.id.nickname);
            focus = itemView.findViewById(R.id.focus);
            content_text = itemView.findViewById(R.id.content_text);
            img_0 = itemView.findViewById(R.id.image_0);
            img_1 = itemView.findViewById(R.id.image_1);
            img_2 = itemView.findViewById(R.id.image_2);
            video = itemView.findViewById(R.id.video);
            praise = itemView.findViewById(R.id.praise);
            praise_num = itemView.findViewById(R.id.praise_num);
            comment = itemView.findViewById(R.id.comment);
            comment_num = itemView.findViewById(R.id.comment_num);
            transport = itemView.findViewById(R.id.transport);
        }
    }
}
