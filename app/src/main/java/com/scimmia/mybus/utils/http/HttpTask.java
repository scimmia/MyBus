package com.scimmia.mybus.utils.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.scimmia.mybus.utils.DebugLog;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by ASUS on 2014/12/4.
 */
public class HttpTask extends AsyncTask<Void,Void,String> {
    ProgressDialog mpDialog;
    Context mContent;
    String msgToShow;

    String mUrl;
    String mTag;
    RequestBody mFormBody;

    HttpListener mHttpListener;

    private static OkHttpClient okHttpClient;
    private static OkHttpClient getOkHttpClient(){
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    public HttpTask(Context mContent, String msgToShow,String url, String tag, HttpListener httpListener) {
        this(mContent,msgToShow,url,tag,null,httpListener);
    }

    public HttpTask(Context mContent, String msgToShow,String url, String tag, RequestBody formBody, HttpListener httpListener) {
        this.mContent = mContent;
        this.msgToShow = msgToShow;
        this.mUrl = url;
        this.mTag = tag;
        this.mFormBody = formBody;

        mHttpListener = httpListener;
//        mUrl = "http://ytbus.jiaodong.cn:4990/BusPosition.asmx/GetBusLineStatusEncry";
        DebugLog.e(this.mUrl);
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        if (StringUtils.isNotEmpty(msgToShow)){
            mpDialog = new ProgressDialog(mContent, ProgressDialog.THEME_HOLO_LIGHT);
//        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
            mpDialog.setMessage(msgToShow);
            mpDialog.setCancelable(true);

            mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    cancel(true);
                }
            });
            mpDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        Request request;
        if (mFormBody == null){
            request = new Request.Builder().tag(mTag)
                    .url(mUrl)
                    .build();
        }else {
            request = new Request.Builder().tag(mTag)
                    .url(mUrl)
                    .post(mFormBody)
                    .build();
        }
        if (request != null){
            OkHttpClient okHttpClient = getOkHttpClient();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    String temp = response.body().string();
                    DebugLog.e(temp);
                    return temp;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mpDialog != null) {
            mpDialog.dismiss();
        }
        if (mHttpListener != null){
            mHttpListener.onSuccess(mTag,result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        DebugLog.e("onCancelled");
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (StringUtils.equalsIgnoreCase((String)call.request().tag(),mTag))
                call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (StringUtils.equalsIgnoreCase((String)call.request().tag(),mTag))
                call.cancel();
        }
    }
}