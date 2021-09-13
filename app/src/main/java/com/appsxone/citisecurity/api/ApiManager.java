package com.appsxone.citisecurity.api;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.Dialog_CustomProgress;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class ApiManager {
    final int DEFAULT_TIMEOUT = 1000000000;
    Activity activity;
    String getOrPost;
    String apiName;
    RequestParams params;
    ApiCallback apiCallback;
    Dialog_CustomProgress customProgressDialog;
    public static boolean shouldShowPD = true;

    public ApiManager(Activity activity, String getOrPost, String apiName, RequestParams params, ApiCallback apiCallback) {
        this.activity = activity;
        this.getOrPost = getOrPost;
        this.apiName = apiName;
        this.params = params;
        this.apiCallback = apiCallback;
        customProgressDialog = new Dialog_CustomProgress(activity);

        System.out.println("-- Req URL : " + Const.BASE_URL + apiName);
        System.out.println("-- Params : " + params.toString());
    }

    public void loadURL(int loader) {
        if (loader == 1) {
            customProgressDialog.showProgressDialog();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(Const.BASE_URL + apiName, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            customProgressDialog.dismissProgressDialog();
                            String content = new String(responseBody);
                            apiCallback.onApiResponce(statusCode, 1, apiName, content);
                            Log.d("onSuccess", "Success: " + content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        try {
                            customProgressDialog.dismissProgressDialog();
                            String content = new String(responseBody);
                            Log.d("onFailure", "Failure: " + content);
                            apiCallback.onApiResponce(statusCode, 0, apiName, content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}