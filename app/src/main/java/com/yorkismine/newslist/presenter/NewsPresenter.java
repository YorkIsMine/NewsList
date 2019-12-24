package com.yorkismine.newslist.presenter;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yorkismine.newslist.Article;
import com.yorkismine.newslist.NewsApi;
import com.yorkismine.newslist.NewsObject;
import com.yorkismine.newslist.NewsView;
import com.yorkismine.newslist.parser.CsvParser;
import com.yorkismine.newslist.parser.Parser;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsPresenter implements Presenter {
    private NewsView view;

    public NewsPresenter(NewsView view){
        this.view = view;
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
        Single<List<Article>> observable =
                Observable.fromIterable(parser.parse(new File(view.getNameExternalCacheDir(), "news.csv"))).toList();


        Observable<Article> usa = newsApi.getNewsUSA().delay(5, TimeUnit.SECONDS)
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

        Observable<Article> ru = newsApi.getNewsRu().delay(2, TimeUnit.SECONDS)
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
        Observable.merge(ru, usa)
                .filter(article -> {

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    Log.d("DATE", format.parse(format.format(new Date())).toString() + " 1");
                    Log.d("DATE", format.parse(article.getPublishedAt()) + " 2");

                    return format.parse(format.format(new Date())).equals(format.parse(article.getPublishedAt()));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .toList()
                .mergeWith(observable)
                .subscribe(list ->{
                    view.showProgress(list);
                });
    }
}
