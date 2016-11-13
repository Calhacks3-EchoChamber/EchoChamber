package com.calhacks.echochamber;

import android.app.DownloadManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Drake on 11/12/2016.
 */

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static String baseURL = "https://morning-basin-92683.herokuapp.com";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void userLogin(RequestParams params) {
        String path = "/user/login";
        Log.d(TAG, "User Login: " + baseURL + path);
        client.post(baseURL + path, params, new JsonHttpResponseHandler());
    }

    public static void requestConversation(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        String path = "/conversation/new";
        Log.d(TAG, "Request Conversation: " + baseURL + path);
        client.post(baseURL + path, params, responseHandler);
    }

    public static void leaveConversation(RequestParams params) {
        String path = "/conversation/leave";
        Log.d(TAG, "Leave Conversation: " + baseURL + path);
        client.post(baseURL + path, params, new JsonHttpResponseHandler());
    }
}
