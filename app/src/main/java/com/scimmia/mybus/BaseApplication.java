package com.scimmia.mybus;

import android.app.Application;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by A on 2016/7/25.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initBusDB();
    }

    private void initBusDB(){
        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(GlobalData.BusDBInited,false)){
            String dirPath = getApplicationContext().getFilesDir().getParentFile().getPath()+"/databases/bus.db";
            DebugLog.e(dirPath);
            if (copyAssetsToFilesystem("bus.db",dirPath)) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(GlobalData.BusDBInited, true).apply();
            }
        }
    }
    private boolean copyAssetsToFilesystem(String assetsSrc, String des){
        try{
            File file = new File(des);
            if (!file.exists()) {
                file = file.getParentFile();
                if (!file.exists() && !file.mkdirs()) {
                    DebugLog.e("Create \"" + file.getPath() + "\" fail!");
                }
            }

            AssetManager am = getApplicationContext().getAssets();
            InputStream istream = am.open(assetsSrc);
            OutputStream ostream = new FileOutputStream(des);
            Global.writeExtractedFileToDisk(istream,ostream);
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
