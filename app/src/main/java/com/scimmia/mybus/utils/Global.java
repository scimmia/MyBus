package com.scimmia.mybus.utils;

import android.content.Context;
import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scimmia.mybus.utils.bean.BusPosition;
import com.scimmia.mybus.utils.bean.BusPositionParam;
import com.scimmia.mybus.utils.bean.DBVersion;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.encode.AES;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lk on 2017/8/10.
 */
public class Global {

    public static String getRandom(String content){
        String result = "";
        try {
            DebugLog.e(content);
            String randomStr = StringUtils.substringBetween(content,"china.com/\">","</string>");
            DebugLog.e(randomStr);
            result = AES.Encrypt(randomStr);
            DebugLog.e(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static LinkedList<BusPosition> getBusPositions(String content,Context context,String tag){
        DebugLog.e(content);
        String temp = StringUtils.substringBetween(content,"[","]");
        if (temp == null){
            temp = "[]";
        }else {
            temp = '['+temp+']';
        }
        temp = StringUtils.replaceEach(temp, GlobalData.keyBefore,GlobalData.keyAfter);
        DebugLog.e(temp);
        LinkedList<BusPosition> t = new Gson().fromJson(temp,new TypeToken<LinkedList<BusPosition>>() {
        }.getType());
        for (BusPosition m :t
                ) {
            m.setLineName(new BusDBManager(context).getLineNamebyIDAttache(tag));
            m.setStationID(new BusDBManager(context).getstationNamebyID(m.getStationID(),StringUtils.split(tag,'-')[1]));
//            m.sumDistance(121.3641740318,37.5471192795);
            m.setLatLng();
            DebugLog.e(m.toString());
        }
        return t;
    }

    public static DBVersion getDBVersion(String content){
        DBVersion result = new DBVersion();
        try {
            DebugLog.e(content);
            String tempStr = StringUtils.substringBetween(content,"<ns1:out>","</ns1:out>");
            DebugLog.e(tempStr);
            result = new Gson().fromJson(tempStr,DBVersion.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer))>0){
            outs.write(buffer, 0, length);
        }
        outs.flush();
        outs.close();
        in.close();
    }

}
