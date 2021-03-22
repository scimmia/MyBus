package com.scimmia.mybus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.bean.BusPosition;
import com.scimmia.mybus.utils.bean.BusPositionParam;
import com.scimmia.mybus.utils.bean.BusPostionNew;
import com.scimmia.mybus.utils.bean.DBVersion;
import com.scimmia.mybus.utils.bean.StationInfo;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.encode.AES;
//import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
//import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lk on 2017/8/10.
 */
public class Global {

    public static String getRandom(String content) {
        String result = "";
        try {
            DebugLog.e(content);
            String randomStr = StringUtils.substringBetween(content, "china.com/\">", "</string>");
            DebugLog.e(randomStr);
            result = AES.Encrypt(randomStr);
            DebugLog.e(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String getBusStatusUrl(String linename, String upordown) {
        return GlobalData.getBusLineStatus + "linename=" + linename + "&upordown=" + upordown;
    }

    public static LinkedList<BusPosition> getBusPositions(String content, String inorder,LinkedList<StationInfo> stationInfos) {
        DebugLog.e(content);
        int minInorder = 200;
        try {
            minInorder = Integer.parseInt(inorder);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        LinkedList<BusPosition> result = new LinkedList<BusPosition>();
        String temp = StringUtils.substringBetween(content, "[", "]");
        if (temp == null) {
            temp = "[]";
        } else {
            temp = '[' + temp + ']';
        }
        temp = StringUtils.replaceEach(temp, GlobalData.keyBefore, GlobalData.keyAfter);
        DebugLog.e(temp);
        try {
            LinkedList<BusPostionNew> t = new Gson().fromJson(temp, new TypeToken<LinkedList<BusPostionNew>>() {
            }.getType());
            for (int i = stationInfos.size()-1;i>=0;i--){
                StationInfo s = stationInfos.get(i);
//            for (StationInfo s : stationInfos){
                if (Integer.parseInt(s.getStationID()) >= minInorder) {
                    continue;
                }
                for (BusPostionNew m : t) {
                    if (s.getStationID().equals(m.getInorder())){
                        BusPosition bus = new BusPosition();
                        bus.setStationID(s.getStationName());
                        bus.setLineName(m.getLinename());
                        bus.setCarID(m.getBusno());
                        bus.setGPSX(String.valueOf(m.getJingdu()));
                        bus.setGPSY(String.valueOf(m.getWeidu()));
                        bus.setLatLng();
                        result.add(bus);
                        if (result.size() == 4)
                            return result;
                    }
                }

            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static DBVersion getDBVersion(String content) {
        DBVersion result = new DBVersion();
        try {
            DebugLog.e(content);
            String tempStr = StringUtils.substringBetween(content, "<ns1:out>", "</ns1:out>");
            DebugLog.e(tempStr);
            result = new Gson().fromJson(tempStr, DBVersion.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            outs.write(buffer, 0, length);
        }
        outs.flush();
        outs.close();
        in.close();
    }

    public static String getFileSize(double b) {
        return new DecimalFormat("#.00").format(b / 1024 / 1024) + "M";
    }

    /**
     * 微信分享：分享网页
     *
     * @param context
     * @param scene
     */
    public static void shareToWeChatWithWebpage(Context context, int scene, String url) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, GlobalData.INVITE_WEIXIN_API);

        if (!iwxapi.isWXAppInstalled()) {
            DebugLog.e("您尚未安装微信客户端");
            return;
        }

        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = url;

        WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.mediaObject = wxWebpageObject;
        wxMediaMessage.title = GlobalData.INVITE_TITLE;
        wxMediaMessage.description = GlobalData.INVITE_CONTENT;
        wxMediaMessage.thumbData =
                bmpToByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.lanacher), true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = scene;

        iwxapi.sendReq(req);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
