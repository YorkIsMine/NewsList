package com.yorkismine.newslist.presenter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yorkismine.newslist.Article;
import com.yorkismine.newslist.NewsApi;
import com.yorkismine.newslist.NewsObject;
import com.yorkismine.newslist.NewsView;
import com.yorkismine.newslist.parser.CsvParser;
import com.yorkismine.newslist.parser.Parser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PresenterViewModel extends ViewModel implements Presenter{
    private NewsView view;

    public void setView(NewsView view) {
        this.view = view;
    }

    public NewsView getView() {
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void callEndpoints() throws Exception {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        NewsApi newsApi = retrofit.create(NewsApi.class);
        Parser parser = new CsvParser();
        Observable
                .fromIterable(parser.parse(new File(view.getNameExternalCacheDir(), "news.csv")))
                .toList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list ->{
                    System.out.println("CACHED NEWS\n\n\n\n\\n\n");
                    view.showProgress(list);
                });


        newsApi.getNewsUSA()
                .doOnNext(newsObject ->{
                    int i = 0;
                    for (Article article : newsObject.getArticles()){
                        Log.d("ELEM", article.toString() + " USA " + i++);
                    }
                }).map(NewsObject::getArticles)
                .flatMapIterable(articles -> articles)
                .filter(article -> {

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    Log.d("DATE", format.parse(format.format(new Date())).toString() + " 1");
                    Log.d("DATE", format.parse(article.getPublishedAt()) + " 2");

                    return format.parse(format.format(new Date())).equals(format.parse(article.getPublishedAt()));
                })
                .take(5)
                .map(art -> {
                    art.setTitle("USA " + art.getTitle());
                    return art;
                }).toList()
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list ->{
                    System.out.println("NEWS USA");
                    view.showProgress(list);
                });

        newsApi.getNewsRu()
                .doOnNext(newsObject ->{
                    int i = 0;
                    for (Article article : newsObject.getArticles()){
                        Log.d("ELEM", article.toString() + " RU " + i++);
                    }
                }).map(NewsObject::getArticles)
                .flatMapIterable(articles -> articles)
                .filter(article -> {

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    Log.d("DATE", format.parse(format.format(new Date())).toString() + " 1");
                    Log.d("DATE", format.parse(article.getPublishedAt()) + " 2");

                    return format.parse(format.format(new Date())).equals(format.parse(article.getPublishedAt()));
                })
                .take(5)
                .map(art ->{
                    art.setTitle("RU " + art.getTitle());
                    return art;
                }).toList()
                .delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list ->{
                    System.out.println("NEWS RU");
                    view.showProgress(list);
                });
    }
}
