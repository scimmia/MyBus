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

    String httpMsg = "正在检查...";
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
    String updateFolder = baseFolder;
    String newDBFile = updateFolder + "ytdb.zip";

    String downAPKTag = "downAPKTag";
    String newapkFile = updateFolder + "ytnew.apk";
    String checkNewAPKVersionTag = "checkNewAPKVersionTag";
    String downNewAPKVersionTag = "downNewAPKVersionTag";
    String _api_key = "8c48e2a019fbf1074b09d0f774cda65f";
    String aId = "4caf5d048d32a96cb2ff89e8dce43981";
    String checkNewAPKVersion = "https://coding.net/u/yantaibus/p/main/git/raw/master/v.json";
//    String checkNewAPKVersion = "http://www.pgyer.com/apiv1/app/viewGroup";
    String downNewAPKVersion = "http://www.pgyer.com/apiv1/app/install?aKey=%s&_api_key="+_api_key;

//    String apkURL = "https://yantaibus.github.io/test/";
    String apkURL = "http://yantaibus.coding.me/main/";
//    String apkURL = "https://www.pgyer.com/TMWz";
    String INVITE_WEIXIN_API = "wx97e4649a10d7f44e";
    String INVITE_TITLE = "我的烟台公交";
    String INVITE_CONTENT = "带有实时路况的公交查询。";
    String INVITE_TARGET_URL = apkURL;

}
