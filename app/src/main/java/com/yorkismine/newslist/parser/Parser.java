package com.yorkismine.newslist.parser;

import com.yorkismine.newslist.Article;

import java.io.File;
import java.util.List;

public interface Parser {
    List<Article> parse(File file) throws Exception;
}
