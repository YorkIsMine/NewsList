package com.yorkismine.newslist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;
    NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Gson gson = new GsonBuilder().setLenient().create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        String key = "9145ee20b660406e9eea321aa4a0ee6c";

        callEndpoints();
    }

    @SuppressLint("CheckResult")
    private void callEndpoints() {
        NewsApi newsApi = retrofit.create(NewsApi.class);

        Observable<Article> usa = newsApi.getNewsUSA()
                .doOnNext(newsObject ->{
                    int i = 0;
                    for (Article article : newsObject.getArticles()){
                        Log.d("ELEM", article.toString() + " USA " + i++);
                    }
                }).map(NewsObject::getArticles)
                .flatMapIterable(articles -> articles)
                .take(5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(art -> {
                    art.setTitle("USA " + art.getTitle());
                    return art;
                });

        Observable<Article> ru = newsApi.getNewsRu()
                .doOnNext(newsObject ->{
                    int i = 0;
                    for (Article article : newsObject.getArticles()){
                        Log.d("ELEM", article.toString() + " RU " + i++);
                    }
                }).map(NewsObject::getArticles)
                .flatMapIterable(articles -> articles)
                .take(5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(art ->{
                    art.setTitle("RU " + art.getTitle());
                    return art;
                });

        Observable.merge(usa, ru)
                .filter(new Predicate<Article>() {
                    @Override
                    public boolean test(Article article) throws Exception {

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        Log.d("DATE", format.parse(format.format(new Date())).toString() + " 1");
                        Log.d("DATE", format.parse(article.getPublishedAt()) + " 2");

                        return format.parse(format.format(new Date())).equals(format.parse(article.getPublishedAt()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .toList()
                .subscribe(this::handleResults, this::handleError);


    }

    private void handleResults(List<Article> articles) {
        if (articles.size() != 0 || articles != null) {
            adapter.setData(articles);


        } else Toast.makeText(this, "NO RESULTS FOUND",
                Toast.LENGTH_LONG).show();
    }

    private void handleError(Throwable t) {
        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again",
                Toast.LENGTH_LONG).show();
    }
}
