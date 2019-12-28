package com.yorkismine.newslist.parser;

import android.os.Parcel;

import com.yorkismine.newslist.Article;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser implements Parser {
    @Override
    public List<Article> parse(File file) throws Exception{
        List<String> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
        String str;
        while ((str = reader.readLine()) != null){
            list.add(str);
        }

        List<Article> articles = new ArrayList<>();
        for (String s : list){
            String[] strings = s.split(";");
            Article article = new Article();
            article.setSource(null);
            article.setAuthor(strings[1]);
            article.setTitle(strings[2]);
            article.setDescription(strings[3]);
            article.setUrl(strings[4]);
            article.setUrlToImage(strings[5]);
            article.setPublishedAt(strings[6]);
            article.setContent(strings[7]);
            articles.add(article);
        }

        return articles;
    }
}
