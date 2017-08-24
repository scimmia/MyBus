package com.scimmia.mybus;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.bean.DBVersion;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.db.UpdateDBTask;
import com.scimmia.mybus.utils.http.HttpDownloadTask;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

public class MainActivity extends SupportActivity  implements EasyPermissions.PermissionCallbacks {
    private static final int RC_Write = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }
        checkDBUpdate();
    }

    @AfterPermissionGranted(RC_Write)
    private void checkDBUpdate(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!

            final Context _mActivity = MainActivity.this;
            new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.getNewDBVersion, GlobalData.getNewDBVersionTag,
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), GlobalData.getNewDBVersionXML), new HttpListener() {
                @Override
                public void onSuccess(String tag, String content) {
                    int currentDBVersion = new BusDBManager(_mActivity).getDBVersion();
                    DBVersion dbVersion = Global.getDBVersion(content);
                    DebugLog.e(currentDBVersion+"---"+dbVersion.toString());
                    if (currentDBVersion < dbVersion.getDbVersion()) {
                        new HttpDownloadTask(
                                _mActivity, GlobalData.downNewDBVersion, GlobalData.downNewDBVersionTag,
                                GlobalData.newDBFile, new HttpListener() {
                            @Override
                            public void onSuccess(String tag, String content) {
                                new UpdateDBTask(_mActivity,null).execute();
                            }
                        }).execute();
                    }
                }
            }).execute();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "从服务器更新数据库需要写权限",
                    RC_Write, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
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
}
