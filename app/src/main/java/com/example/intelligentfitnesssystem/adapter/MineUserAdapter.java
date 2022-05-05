package com.example.intelligentfitnesssystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.activity.ArticleDetailActivity;
import com.example.intelligentfitnesssystem.activity.UserInfoActivity;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<User> list;

    public MineUserAdapter(Context mContext, List<User> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<User> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_mine_user_adapter, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ListViewHolder) {
            int safePosition = holder.getLayoutPosition();
            int adapterPosition = holder.getBindingAdapterPosition();
            User user = list.get(safePosition);
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            if (user != null) {
                Glide.with(mContext).load(mContext.getString(R.string.baseUrl) + mContext.getString(R.string.api_get_img) + user.getImg()).into(listViewHolder.head);
                listViewHolder.nickname.setText(user.getNickname());
                listViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("*****mineUser" + JSON.toJSONString(user));
                        Intent intent = new Intent(mContext.getApplicationContext(), UserInfoActivity.class);
                        intent.putExtra("User", JSON.toJSONString(user));
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
        CircleImageView head;
        TextView nickname;
        ConstraintLayout container;

        public ListViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            nickname = itemView.findViewById(R.id.nickname);
            container = itemView.findViewById(R.id.container);

        }
    }
}
