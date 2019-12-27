package com.yorkismine.newslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.yorkismine.newslist.presenter.NewsPresenter;
import com.yorkismine.newslist.presenter.Presenter;
import com.yorkismine.newslist.presenter.PresenterViewModel;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsView{

    private NewsAdapter adapter;
    private PresenterViewModel presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        presenter = ViewModelProviders.of(this).get(PresenterViewModel.class);
        presenter.setView(this);

        String key = "9145ee20b660406e9eea321aa4a0ee6c";

        try {
            presenter.callEndpoints();
        } catch (Exception e) {
            e.printStackTrace();
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
        outState.putSerializable("KEY", presenter);
        outState.putSerializable("KEY2", adapter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        presenter = (PresenterViewModel) savedInstanceState.getSerializable("KEY");
        adapter = (NewsAdapter) savedInstanceState.getSerializable("KEY2");
    }
}
