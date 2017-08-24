package com.scimmia.mybus.utils.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.GlobalData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

/**
 * Created by ASUS on 2014/12/29.
 */
public class HttpDownloadTask extends AsyncTask<Void,Integer,String>{

    ProgressDialog mpDialog;
    Context mContent;
    String msgToShow;

    String mTag;
    String mUrl;
    String mFilePath;
    HttpListener mHttpListener;

    public HttpDownloadTask(Context mContent, String mUrl, String mTag, String filePath,HttpListener mHttpListener) {
        this.mContent = mContent;
        this.msgToShow = GlobalData.downMsg;
        this.mUrl = mUrl;
        this.mTag = mTag;
        this.mFilePath = filePath;
        this.mHttpListener = mHttpListener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mpDialog = new ProgressDialog(mContent, ProgressDialog.THEME_HOLO_LIGHT);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置风格为圆形进度条
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
        Request request = new Request.Builder().tag(mTag)
                .url(mUrl)
                .build();
        if (request != null){
            OkHttpClient okHttpClient = new OkHttpClient();
            InputStream input = null;
            OutputStream output = null;
            try {
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    long fileLength = response.body().contentLength();
                    input = response.body().byteStream();
                    File file = new File(mFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    createFileAndForders(mFilePath);

                    output = new FileOutputStream(mFilePath);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                    return mFilePath;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mpDialog.dismiss();

        if (mHttpListener != null){
            mHttpListener.onSuccess(mTag,result);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mpDialog.setIndeterminate(false);
        mpDialog.setMax(100);
        mpDialog.setProgress(progress[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        DebugLog.e("onCancelled");
    }

    public File createFileAndForders(String sFile) throws IOException {
        File file = new File(sFile);
        File filePrt = file.getParentFile();
        if (filePrt != null &&!filePrt.exists()) {
            filePrt.mkdirs();
        }
        file.createNewFile();
        return file;
    }
}
