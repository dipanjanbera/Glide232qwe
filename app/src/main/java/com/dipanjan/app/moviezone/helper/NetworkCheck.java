package com.dipanjan.app.moviezone.helper;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LENOVO on 03-11-2018.
 */

public class NetworkCheck extends AsyncTask<String, Void, Integer> {


    public static Integer TIMEOUT_DURATION = 10000;
    public static String DISPLAY_SNACBAR_MSG_IF_HOST_NOT_RESOLVE = "Host is not responding";
    public static String DISPLAY_MSG_IF_HOST_NOT_RESOLVE = "Host(yts.am) is not responding.\nMay be it is down now or blocked in your country.\nYou may use VPN to connect with host.";
    private String[] hostArr;

    public interface AsyncResponse {
        Integer processFinish(Integer urlIndexPos);
    }

    public AsyncResponse delegate = null;
    public NetworkCheck(String[] host,AsyncResponse delegate){
        this.delegate = delegate;
        this.hostArr=host;
    }



    protected Integer doInBackground(String... params) {
        Integer indexPosition =-1;
        indexPosition=processPingURL(this.hostArr);
        return indexPosition;
    }

    @Override
    protected void onPostExecute(Integer result) {
        delegate.processFinish(result);
    }


    public int processPingURL(String[] hostArr){
        for (int index = 0; index < hostArr.length; index++) {
            boolean pingResult = pingURL(hostArr[index], TIMEOUT_DURATION);
            if (pingResult) {
                return index;
            }

        }
        return -1;
    }

    public static boolean pingURL(String url, int timeout) {
        url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }

    }






}