package com.yorkismine.newslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.yorkismine.newslist.presenter.NewsPresenter;
import com.yorkismine.newslist.presenter.PresenterViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsView{

    private static final String NAME_OF_ACTIVITY = "MainActivity";

    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null){
            ArrayList<Article> articles =
                    savedInstanceState.getParcelableArrayList("123");
            adapter.setData(articles);
        }

        PresenterViewModel presenter =
                ViewModelProviders.of(this).get(PresenterViewModel.class);
        presenter.setView(this);

        String key = "9145ee20b660406e9eea321aa4a0ee6c";
        if (savedInstanceState == null) {
            try {
                presenter.callEndpoints();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void showProgress(List<Article> articles) {
        if (articles.size() != 0 || articles != null) {
            adapter.setData(articles);

        } else Toast.makeText(this, "NO RESULTS FOUND",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(Throwable t) {
        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public File getNameExternalCacheDir() {
        return getExternalCacheDir();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState
                .putParcelableArrayList("123", adapter.getNewsList());
        super.onSaveInstanceState(outState);
    }
}
