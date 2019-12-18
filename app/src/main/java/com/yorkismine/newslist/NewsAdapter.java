package com.yorkismine.newslist;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private List<Article> newsList = new ArrayList<>();

    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        Article article = newsList.get(position);
        holder.title.setText(article.getTitle());
        if (article.getUrlToImage() != null ){
            Glide.with(context).load(article.getUrlToImage()).into(holder.image);
        }

        holder.author.setText(article.getAuthor());
    }

    public void setData(List<Article> articles){
        newsList.addAll(articles);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView image;
        TextView author;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            author = itemView.findViewById(R.id.author);
        }
    }
}
