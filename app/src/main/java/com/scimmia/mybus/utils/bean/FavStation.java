package com.scimmia.mybus.utils.bean;

import com.scimmia.mybus.utils.GlobalData;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by lk on 2017/8/17.
 */
public class FavStation extends LineInfo{
    private String stationID;
    private String lineStatus;
    private String tag;
    private String stationname;
    private double lat;
    private double lon;

    public FavStation() {
    }
    public FavStation(LineInfo lineInfo,String stationID, String lineStatus, String tag) {
        this.lineID = lineInfo.getLineID();
        this.attach = lineInfo.getAttach();
        this.stationID = stationID;
        this.lineStatus = lineStatus;
        this.tag = tag;
        this.linename = lineInfo.getLinename();
    }
    public FavStation(String linename, String attach,String stationa,String stationb, String stationID, String tag,String stationname,double lat,double lon) {
        this.lineID = linename;
        this.attach = attach;
        this.stationa = stationa;
        this.stationb = stationb;
        this.stationID = stationID;
        this.lineStatus = "1";
        this.tag = tag;
        this.linename = linename;
        this.stationname = stationname;
        this.lat = lat;
        this.lon = lon;
    }

    public String toValue(){
        char splite = '|';
        return lineID+splite+attach+splite+stationID+splite+lineStatus;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "FavStation{" +
                "lineID='" + lineID + '\'' +
                ", linename='" + linename + '\'' +
                ", attach='" + attach + '\'' +
                ", stationa='" + stationa + '\'' +
                ", stationID='" + stationID + '\'' +
                ", stationb='" + stationb + '\'' +
                ", lineStatus='" + lineStatus + '\'' +
                ", tag='" + tag + '\'' +
                ", stationname='" + stationname + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        FavStation temp = (FavStation) obj;
        return StringUtils.equals(this.stationID,temp.stationID) &&
                StringUtils.equals(this.lineStatus,temp.lineStatus) ;
//        return StringUtils.equals(this.lineID,temp.lineID) &&
//                StringUtils.equals(this.attach,temp.attach) &&
//                StringUtils.equals(this.stationID,temp.stationID) &&
//                StringUtils.equals(this.lineStatus,temp.lineStatus) &&
//                StringUtils.equals(this.tag,temp.tag);
    }

    public RequestBody getFormBody(String strSession){
        return new FormBody.Builder()
                .add("stationID", stationID)
                .add("lineID", lineID)
                .add("lineStatus", lineStatus)
                .add("userRole", "1")
                .add("attach", attach)
                .add("strSession", strSession)
                .add("strFlag", "JIAODONG")
                .add("strIMEI", "76667")
                .build();
    }
}
