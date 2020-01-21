package com.yorkismine.newslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.yorkismine.newslist.presenter.NewsPresenter;
import com.yorkismine.newslist.presenter.PresenterViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsView {

    private static final String NAME_OF_ACTIVITY = "MainActivity";
    private static final String PARCELABLE_LIST = "ParcelableList";

    private NewsAdapter adapter;
    private SharedPreferences pref;
    private String pasText;
    private String logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getPreferences(Context.MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent.getStringExtra("log") != null) {
            logText = intent.getStringExtra("log");
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(LoginActivity.PREF_LOGIN, logText);
            editor.apply();
        }

        if (pref.contains(LoginActivity.PREF_LOGIN)){
            logText = pref.getString(LoginActivity.PREF_LOGIN, "");

        } else{
            Intent toLogin = new Intent(this, LoginActivity.class);
            toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toLogin);
        }


        getSupportActionBar().setTitle(logText);

        Log.d("CHECKER", "value pas: " + pasText);
        Log.d("CHECKER", "value log: " + logText);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            ArrayList<Article> articles =
                    savedInstanceState.getParcelableArrayList(PARCELABLE_LIST);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit_menu_btn) {
            Intent intent = new Intent(this, LoginActivity.class);

            Log.d("CHECKER", "value pas: " + pasText);
            Log.d("CHECKER", "value log: " + logText);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        return true;
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
                .putParcelableArrayList(PARCELABLE_LIST, adapter.getNewsList());
        super.onSaveInstanceState(outState);
    }

}
