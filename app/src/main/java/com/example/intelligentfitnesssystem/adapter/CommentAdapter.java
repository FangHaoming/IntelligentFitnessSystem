package com.example.intelligentfitnesssystem.adapter;

import static com.example.intelligentfitnesssystem.MyApplication.commentId;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.ArticleDetailActivity;
import com.example.intelligentfitnesssystem.activity.ReleaseArticleActivity;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.Comment;
import com.example.intelligentfitnesssystem.bean.MyResponse;
import com.example.intelligentfitnesssystem.util.Http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.ittiger.player.VideoPlayerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Comment> list;
    private boolean isShowCommentBtn = true;
    private EditText et = null;

    public CommentAdapter(Context mContext, List<Comment> list) {
        this.mContext = mContext;
        this.list = new ArrayList(list);
    }

    public void setIsShowCommentBtn(Boolean isShowCommentBtn) {
        this.isShowCommentBtn = isShowCommentBtn;
    }

    public void addItemCommentCount() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == commentId) {
                list.get(i).setCommentCount(list.get(i).getCommentCount() + 1);
            }
        }
    }

    public void setEditText(EditText et) {
        this.et = et;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Comment> list) {
        this.list = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(Comment comment) {
        this.list.add(comment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_adapter, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ListViewHolder) {
            if (list.get(position).getPublisherImg() != null) {
                Glide.with(mContext).load(mContext.getResources().getString(R.string.baseUrl) + mContext.getResources().getString(R.string.api_get_img) + list.get(position).getPublisherImg()).into(((ListViewHolder) holder).head);
            }
            ((ListViewHolder) holder).nickname.setText(String.valueOf(list.get(position).getPublisherName()));
            ((ListViewHolder) holder).createTime.setText(list.get(position).getCreateTime());
            ((ListViewHolder) holder).content_text.setText(list.get(position).getContent());

            int[] temp = list.get(position).getLikeId();
            sort(temp);
            int index = binarySearch(temp, localUser.getId());
            if (-1 != index) {
                ((ListViewHolder) holder).praise.setBackground(mContext.getDrawable(R.drawable.praise_clicked));
            }
            ((ListViewHolder) holder).praise.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    if (-1 != index) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MyResponse<Object> result = JSON.parseObject(Http.cancelPraiseComment(mContext, list.get(position).getId()), (Type) MyResponse.class);
                                    if (result.getStatus() == 0) {
                                        ((ListViewHolder) holder).praise.setBackground(mContext.getDrawable(R.drawable.praise));
                                        ((ListViewHolder) holder).praise_num.setText(String.valueOf(list.get(position).getLikeCount() - 1));
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
                                    MyResponse<Object> result = JSON.parseObject(Http.praiseComment(mContext, list.get(position).getId()), (Type) MyResponse.class);
                                    if (result.getStatus() == 0) {
                                        ((ListViewHolder) holder).praise.setBackground(mContext.getDrawable(R.drawable.praise_clicked));
                                        ((ListViewHolder) holder).praise_num.setText(String.valueOf(list.get(position).getLikeCount() + 1));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });
            ((ListViewHolder) holder).praise_num.setText(String.valueOf(list.get(position).getLikeCount()));

            if (!isShowCommentBtn) ((ListViewHolder) holder).comment.setVisibility(View.GONE);
            ((ListViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (et != null) {
                        et.requestFocus();
                        commentId = list.get(position).getId();
                        System.out.println("*****commentId:" + commentId);
                        InputMethodManager inputMethodManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(et, 0);
                    }
                }
            });
            if (list.get(position).getCommentCount() > 0) {
                ((ListViewHolder) holder).comment_num.setText("查看更多" + list.get(position).getCommentCount() + "条评论");
            }

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
        TextView content_text;
        Button praise;
        TextView praise_num;
        Button comment;
        TextView comment_num;

        public ListViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            nickname = itemView.findViewById(R.id.nickname);
            createTime = itemView.findViewById(R.id.createTime);
            content_text = itemView.findViewById(R.id.content_text);
            praise = itemView.findViewById(R.id.praise);
            praise_num = itemView.findViewById(R.id.praise_num);
            comment = itemView.findViewById(R.id.comment);
            comment_num = itemView.findViewById(R.id.comment_num);
        }
    }
}
