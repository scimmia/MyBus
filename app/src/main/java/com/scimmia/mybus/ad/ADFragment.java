package com.scimmia.mybus.ad;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.TileService;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.base.BaseFragment;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ADFragment extends BaseFragment {

    public static ADFragment newInstance() {
        ADFragment fragment = new ADFragment();
        return fragment;
    }

    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ad, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_ad);
        mToolbar.setTitle("分享");

        view.findViewById(R.id.ad_showmoney).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AlipayZeroSdk.hasInstalledAlipayClient(_mActivity)) {
                    AlipayZeroSdk.startAlipayClient(_mActivity, "FKX02077NT0CXTG66SMY15");
                } else {
                    showToast("谢谢，您没有安装支付宝客户端");
                }
            }
        });
//        final EditText s = (EditText)view.findViewById(R.id.shareurl);
        view.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ClipboardManager cm = (ClipboardManager) _mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setPrimaryClip(ClipData.newRawUri("apkurl",Uri.parse(GlobalData.apkURL)));
//                showToast("应用安装地址已复制到剪切板，分享给好友吧。");

//                Global.shareToWeChatWithWebpage(_mActivity, SendMessageToWX.Req.WXSceneSession);


//                final String url = s.getText().toString();
                final String url = GlobalData.INVITE_TARGET_URL;
                new AlertDialog.Builder(getActivity()).setTitle("分享应用到")
                        .setSingleChoiceItems(new String[]{"微信好友","朋友圈"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedPosition) {
                                switch (selectedPosition){
                                    case 0:
                                        Global.shareToWeChatWithWebpage(_mActivity, SendMessageToWX.Req.WXSceneSession,url);
                                        break;
                                    case 1:
                                        Global.shareToWeChatWithWebpage(_mActivity,SendMessageToWX.Req.WXSceneTimeline,url);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
        return view;
    }


}
class AlipayZeroSdk {
    // 支付宝包名
    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";

    // 旧版支付宝二维码通用 Intent Scheme Url 格式
    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
            "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s" +
            "%3Dweb-other&_t=1472443966571#Intent;" +
            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    /**
     * 打开转账窗口
     * 旧版支付宝二维码方法，需要使用 https://fama.alipay.com/qrcode/index.htm 网站生成的二维码
     * 这个方法最好，但在 2016 年 8 月发现新用户可能无法使用
     *
     * @param activity Parent Activity
     * @param urlCode 手动解析二维码获得地址中的参数，例如 https://qr.alipay.com/aehvyvf4taua18zo6e 最后那段
     * @return 是否成功调用
     */
    public static boolean startAlipayClient(Activity activity, String urlCode) {
        return startIntentUrl(activity, INTENT_URL_FORMAT.replace("{urlCode}", urlCode));
    }

    /**
     * 打开 Intent Scheme Url
     *
     * @param activity Parent Activity
     * @param intentFullUrl Intent 跳转地址
     * @return 是否成功调用
     */
    public static boolean startIntentUrl(Activity activity, String intentFullUrl) {
        try {
            Intent intent = Intent.parseUri(
                    intentFullUrl,
                    Intent.URI_INTENT_SCHEME
            );
            activity.startActivity(intent);
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     * @param context Context
     * @return 支付宝客户端是否已安装
     */
    public static boolean hasInstalledAlipayClient(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取支付宝客户端版本名称，作用不大
     * @param context Context
     * @return 版本名称
     */
    public static String getAlipayClientVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 打开支付宝扫一扫界面
     * @param context Context
     * @return 是否成功打开 Activity
     */
    public static boolean openAlipayScan(Context context) {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (context instanceof TileService) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ((TileService) context).startActivityAndCollapse(intent);
                }
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打开支付宝付款码
     * @param context Context
     * @return 是否成功打开 Activity
     */
    public static boolean openAlipayBarcode(Context context) {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (context instanceof TileService) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ((TileService) context).startActivityAndCollapse(intent);
                }
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
