package com.scimmia.mybus;

import android.app.Application;
import okhttp3.OkHttpClient;

/**
 * Created by A on 2016/7/25.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static OkHttpClient okHttpClient;
    public static OkHttpClient getOkHttpClient(){
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}
