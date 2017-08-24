package com.scimmia.mybus.utils.db;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.http.HttpListener;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lk on 2017/8/23.
 */
public class UpdateDBTask  extends AsyncTask<Void,Integer,String> {

    ProgressDialog mpDialog;
    Context mContent;
    String msgToShow;
    HttpListener mHttpListener;

    public UpdateDBTask(Context mContent, HttpListener mHttpListener) {
        this.mContent = mContent;
        this.msgToShow = GlobalData.updateMsg;
        this.mHttpListener = mHttpListener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mpDialog = new ProgressDialog(mContent, ProgressDialog.THEME_HOLO_LIGHT);
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

    @Override
    protected String doInBackground(Void... params) {
        try {
            String oldDBPath = mContent.getFilesDir().getParentFile().getPath()+"/databases/bus.db";

            ZipInputStream zin = new ZipInputStream(new FileInputStream(GlobalData.newDBFile));
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                OutputStream ostream = new FileOutputStream(oldDBPath);
                Global.writeExtractedFileToDisk(zin,ostream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mpDialog.dismiss();

        if (mHttpListener != null){
            mHttpListener.onSuccess("",result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        DebugLog.e("onCancelled");
    }
}

