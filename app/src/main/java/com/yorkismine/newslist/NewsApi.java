package com.yorkismine.newslist;



import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("top-headlines?country=us&category=business&apiKey=9145ee20b660406e9eea321aa4a0ee6c")
    Observable<NewsObject> getNewsUSA();

    @GET("top-headlines?country=ru&category=business&apiKey=9145ee20b660406e9eea321aa4a0ee6c")
    Observable<NewsObject> getNewsRu();
}
