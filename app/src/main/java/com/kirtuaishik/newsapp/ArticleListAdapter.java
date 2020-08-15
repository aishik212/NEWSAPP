package com.kirtuaishik.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    private Activity activity;

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final TextView date_tv, title_tv;
        private final ImageView articleIMV;
        private final LinearLayout web;

        private ArticleViewHolder(View itemView) {
            super(itemView);
            date_tv = itemView.findViewById(R.id.date_tv);
            title_tv = itemView.findViewById(R.id.title_tv);
            articleIMV = itemView.findViewById(R.id.imageView);
            web = itemView.findViewById(R.id.webOPEN);
        }
    }

    private final LayoutInflater mInflater;
    private List<Articles> article; // Cached copy of words

    ArticleListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        if (article != null) {
            Articles current = article.get(position);
            String[] split = current.getDate().replaceAll("[a-zA-Z]", " ").split(" ");
            holder.date_tv.setText(String.format("Date - %s at %s", split[0], split[1]));
            holder.date_tv.append(String.format("\nURL - %s", current.getUrl()));
            holder.title_tv.setText(current.getTitle());
            Glide.with(activity).load(current.getImageUrl()).into(holder.articleIMV);
            holder.web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, webpage.class);
                    intent.putExtra("url", current.getUrl());
                    intent.putExtra("title", current.getTitle());
                    activity.startActivity(intent);
                }
            });
        }
    }

    void setArticles(List<Articles> articles, Activity activity) {
        article = articles;
        this.activity = activity;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (article != null)
            return article.size();
        else return 0;
    }
}