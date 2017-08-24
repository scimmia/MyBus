package com.scimmia.mybus.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by lk on 2017/8/12.
 */
public interface GlobalData  {
    String BusDBInited = "BusDBInited";

    String keyBefore[] = {"车牌","站"};
    String keyAfter[] = {"carID","stationID"};

    int upDir = 2;
    int downDir = 1;

    String goWork = "1";
    String goHome = "2";
    String goNormal = "3";

    String httpMsg = "获取中...";
    String downMsg = "下载中...";
    String updateMsg = "升级中...";
    String getRandomTag = "getRandomTag";
    String getRandom = "http://ytbus.jiaodong.cn:4990/BusPosition.asmx/get_random";
    String getBusLineStatusEncry = "http://ytbus.jiaodong.cn:4990/BusPosition.asmx/GetBusLineStatusEncry";
    String getNewDBVersion = "http://ytbus.jiaodong.cn:4998/SynBusSoftWebservice/services/SynBusSoft";
    String getNewDBVersionTag = "getNewDBVersionTag";
    String getNewDBVersionXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><getNewDbVersion xmlns=\"http://www.dongfang-china.com/\"></getNewDbVersion></soap:Body></soap:Envelope>";

    String downNewDBVersion = "http://ytbus.jiaodong.cn:4998/SynBusSoftWebservice/DownloadServlet?method=downloadNewDb";
    String downNewDBVersionTag = "downNewDBVersionTag";
    String baseFolder = Environment.getExternalStorageDirectory().getPath()+ File.separator+"download"+ File.separator;
    String updateFolder = baseFolder + "update" + File.separator;
    String newDBFile = updateFolder + "db.zip";

    String downAPKTag = "downAPKTag";
    String newapkFile = updateFolder + "offical.apk";
}
