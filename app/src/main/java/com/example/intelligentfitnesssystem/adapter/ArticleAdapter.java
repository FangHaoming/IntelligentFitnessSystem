package com.example.intelligentfitnesssystem.adapter;

import static com.example.intelligentfitnesssystem.MyApplication.commentId;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
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
    private boolean isFocus = false;
    private boolean isPraise = false;
    private User focusedUser = new User();

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ListViewHolder) {
            int safePosition = holder.getBindingAdapterPosition();
            Article article = list.get(safePosition);
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            if (article.getPublisherImg() != null) {
                Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + article.getPublisherImg()).into(listViewHolder.head);
            }
            listViewHolder.head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MyResponse<User> result = JSON.parseObject(Http.getUserInfo(mContext, article.getUserId()), (Type) MyResponse.class);
                                if (result.getStatus() == 0) {
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
            listViewHolder.nickname.setText(String.valueOf(article.getPublisherName()));
            listViewHolder.createTime.setText(article.getCreateTime());
            for (User user : localUser.getFocus()) {
                if (user.getId() == article.getUserId()) {
                    isFocus = true;
                    focusedUser = user;
                }
            }
            if (isFocus) {
                listViewHolder.focus.setText(mContext.getResources().getString(R.string.focused));
            }
            listViewHolder.focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFocus) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            MyResponse<Object> result = JSON.parseObject(Http.followUser(mContext, article.getUserId()), (Type) MyResponse.class);
                                            if (result != null && result.getStatus() == 0) {
                                                listViewHolder.focus.setText(mContext.getResources().getString(R.string.focused));

                                            } else {
                                                Looper.prepare();
                                                Toast.makeText(mContext, mContext.getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            MyResponse<Object> result = JSON.parseObject(Http.unFollowUser(mContext, article.getUserId()), (Type) MyResponse.class);
                                            if (result != null && result.getStatus() == 0) {
                                                listViewHolder.focus.setText(mContext.getResources().getString(R.string.focus));
                                            } else {
                                                Looper.prepare();
                                                Toast.makeText(mContext, mContext.getResources().getString(R.string.info_error_server), Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }
                    }).start();

                }
            });
            if (article.getText() != null && !article.getText().equals("")) {
                listViewHolder.content_text.setVisibility(View.VISIBLE);
                listViewHolder.content_text.setText(article.getText());
            }
            if (article.getImg().length > 0 && !article.getImg()[0].split("\\.")[1].equals("mp4")) {
                if (article.getImg()[0] != null) {
                    listViewHolder.img_0.setVisibility(View.VISIBLE);
                    listViewHolder.img_0.setImageResource(R.drawable.img_preview);
                    Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[0]).into(listViewHolder.img_0);
                }
                if (article.getImg().length > 1 && article.getImg()[1] != null) {
                    listViewHolder.img_1.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[1]).into(listViewHolder.img_1);
                }
                if (article.getImg().length > 2 && article.getImg()[2] != null) {
                    listViewHolder.img_2.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[2]).into(listViewHolder.img_2);
                }
            }
            if (article.getImg().length == 1 && article.getImg()[0].split("\\.")[1].equals("mp4")) {
                listViewHolder.video.setVisibility(View.VISIBLE);
                listViewHolder.video.bind(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + mContext.getResources().getString(R.string.api_get_articleImg) + article.getImg()[0]);
            }
            int[] temp = article.getLikeId();
            sort(temp);
            isPraise = -1 != binarySearch(temp, localUser.getId());
            if (isPraise) {
                listViewHolder.praise.setBackground(mContext.getDrawable(R.drawable.praise_clicked));
            }
            listViewHolder.praise.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    if (isPraise) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MyResponse<Object> result = JSON.parseObject(Http.cancelPraiseArticle(mContext, article.getId()), (Type) MyResponse.class);
                                    if (result != null && result.getStatus() == 0) {
                                        listViewHolder.praise.setBackground(mContext.getDrawable(R.drawable.praise));
                                        article.setLikeCount(article.getLikeCount() - 1);
                                        listViewHolder.praise_num.setText(String.valueOf(article.getLikeCount()));
                                        isPraise = false;

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MyResponse<Object> result = JSON.parseObject(Http.praiseArticle(mContext, article.getId()), (Type) MyResponse.class);
                                    if (result != null && result.getStatus() == 0) {
                                        listViewHolder.praise.setBackground(mContext.getDrawable(R.drawable.praise_clicked));
                                        article.setLikeCount(article.getLikeCount() + 1);
                                        listViewHolder.praise_num.setText(String.valueOf(article.getLikeCount()));
                                        isPraise = true;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });
            listViewHolder.praise_num.setText(String.valueOf(article.getLikeCount()));
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
                    intent.putExtra("Article",JSON.toJSONString(article));
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
        }
    }
}
