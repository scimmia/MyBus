package com.scimmia.mybus;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import com.google.gson.Gson;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.bean.DBVersion;
import com.scimmia.mybus.utils.bean.UpdateInfo;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.db.UpdateDBTask;
import com.scimmia.mybus.utils.http.HttpDownloadTask;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.File;
import java.util.List;

public class MainActivity extends SupportActivity  implements EasyPermissions.PermissionCallbacks {
    private Context _mActivity;
    private static final int RC_Write = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }
        _mActivity = MainActivity.this;
        checkUpdate();
        checkDBUpdate();
    }

    private void checkDBUpdate(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.getNewDBVersion, GlobalData.getNewDBVersionTag,
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), GlobalData.getNewDBVersionXML), new HttpListener() {
                @Override
                public void onSuccess(String tag, String content) {
                    try {
                        int currentDBVersion = new BusDBManager(_mActivity).getDBVersion();
                        DBVersion dbVersion = Global.getDBVersion(content);
                        DebugLog.e(currentDBVersion+"---"+dbVersion.toString());
                        if (currentDBVersion < dbVersion.getDbVersion()) {
                            new AlertDialog.Builder(_mActivity)
                                    .setTitle("数据更新提示")
                                    .setMessage("发现新数据\n文件大小："+Global.getFileSize(dbVersion.getDbSize())
                                            +"\n文件下载需要读写权限，\n如有提醒请允许"
                                    )
                                    .setNegativeButton("现在更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            beginDownloadDB();
                                        }
                                    })
                                    .setPositiveButton("以后再说",null)
                                    .setCancelable(false)
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else {
            EasyPermissions.requestPermissions(this, "从服务器自动更新需要写权限",
                    RC_Write, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(RC_Write)
    private void beginDownloadDB(){
        new HttpDownloadTask(
                _mActivity, GlobalData.downNewDBVersion, GlobalData.downNewDBVersionTag,
                GlobalData.newDBFile, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                new UpdateDBTask(_mActivity,null).execute();
            }
        }).execute();
    }
    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
    }

    private void checkUpdate(){
        new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.checkNewAPKVersion, GlobalData.checkNewAPKVersionTag,
                new HttpListener() {
                    @Override
                    public void onSuccess(String tag, String content) {
                        final UpdateInfo updateInfo = new Gson().fromJson(content,UpdateInfo.class);
                        try {
                            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            if (packageInfo.versionCode < updateInfo.getVersionCode()){
                                new AlertDialog.Builder(_mActivity)
                                        .setTitle("升级信息提示")
                                        .setMessage("发现新版本："+updateInfo.getVersionName()
                                                +"\n更新内容："+updateInfo.getUpdateLog()
                                                +"\n文件大小："+Global.getFileSize(NumberUtils.toDouble(updateInfo.getFileSize()))
                                                +"\n文件下载需要读写权限，\n如有提醒请允许"
                                        )
                                        .setNegativeButton("现在更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                beginDownload(updateInfo.getSoftUrl());
                                            }
                                        })
                                        .setPositiveButton("以后再说", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String t = updateInfo.getVersionName();
                                                if (StringUtils.contains(t,"b")){
                                                    finish();
                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).execute();
    }

//    private void checkUpdate(){
//        new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.checkNewAPKVersion, GlobalData.checkNewAPKVersionTag,
//                new FormBody.Builder()
//                        .add("aId", GlobalData.aId)
//                        .add("_api_key", GlobalData._api_key)
//                        .build(), new HttpListener() {
//            @Override
//            public void onSuccess(String tag, String content) {
//                UpdateInfo updateInfo = new Gson().fromJson(content,UpdateInfo.class);
//                final UpdateInfo.DataBean dataBean = updateInfo.getMaxVersion();
//                if (dataBean!=null){
//                    try {
//                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        if (packageInfo.versionCode < NumberUtils.toInt(dataBean.getAppVersionNo(),-1)){
//                            new AlertDialog.Builder(_mActivity)
//                                    .setTitle("升级信息提示")
//                                    .setMessage("发现新版本："+dataBean.getAppVersion()
//                                            +"\n更新内容："+dataBean.getAppUpdateDescription()
//                                            +"\n文件大小："+Global.getFileSize(NumberUtils.toDouble(dataBean.getAppFileSize()))
//                                            +"\n文件下载需要读写权限，\n如有提醒请允许"
//                                    )
//                                    .setNegativeButton("现在更新", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            beginDownload(dataBean.getAppKey());
//                                        }
//                                    })
//                                    .setPositiveButton("以后再说", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            String t = dataBean.getAppVersion();
//                                            if (StringUtils.contains(t,"b")){
//                                                finish();
//                                            }
//                                        }
//                                    })
//                                    .setCancelable(false)
//                                    .show();
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).execute();
//    }

    @AfterPermissionGranted(RC_Write)
    private void beginDownload(String url){
        new HttpDownloadTask(
                _mActivity,url , GlobalData.downNewAPKVersionTag,
                GlobalData.newapkFile, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                new UpdateDBTask(_mActivity, new HttpListener() {
                    @Override
                    public void onSuccess(String tag, String content) {
                        installNewApk(GlobalData.newapkFile);
                    }
                }).execute();
            }
        }).execute();
    }

    private void installNewApk( String apkPath) {
        if (StringUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(_mActivity, "com.scimmia.mybus.fileprovider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
