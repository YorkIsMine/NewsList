package com.yorkismine.newslist;

import java.io.File;
import java.util.List;

public interface NewsView {
    void showProgress(List<Article> articles);
    void showError(Throwable t);
    File getNameExternalCacheDir();
    void setListOfArticles(List<Article> articles);
}
