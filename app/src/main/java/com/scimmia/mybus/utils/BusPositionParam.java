package com.scimmia.mybus.utils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by lk on 2017/8/10.
 */
public class BusPositionParam {
    String stationID;
    String lineID;
    String lineStatus;

    public BusPositionParam(String stationID, String lineID, String lineStatus) {
        this.stationID = stationID;
        this.lineID = lineID;
        this.lineStatus = lineStatus;
    }

    public String getParam(String strSession){
        return "?stationID="+stationID+"&lineID="+lineID+"&lineStatus="+lineStatus+"&userRole=1&attach=1&strSession="+strSession+"&strFlag=JIAODONG&strIMEI=76667";
    }

    public RequestBody getFormBody(String strSession){
        return new FormBody.Builder()
                .add("stationID", stationID)
                .add("lineID", lineID)
                .add("lineStatus", lineStatus)
                .add("userRole", "1")
                .add("attach", "1")
                .add("strSession", strSession)
                .add("strFlag", "JIAODONG")
                .add("strIMEI", "76667")
                .build();
    }
}
