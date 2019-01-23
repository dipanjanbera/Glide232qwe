package com.dipanjan.app.moviezone.listener;

/**
 * Created by LENOVO on 17-01-2019.
 */

public interface DataFetchListener {
    public void onResultFetchedSuccessful(String[] hostArr);
    public void onResultFetchError();
}
