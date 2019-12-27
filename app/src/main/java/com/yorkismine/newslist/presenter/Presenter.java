package com.yorkismine.newslist.presenter;

import java.io.Serializable;

public interface Presenter extends Serializable {
    void callEndpoints() throws Exception;
}
