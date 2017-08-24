package com.scimmia.mybus.utils.bean;

import java.util.LinkedList;

/**
 * Created by lk on 2017/8/14.
 */
public class SingleLineInfo {
    private LineInfo lineInfo;
    private LinkedList<StationInfo> upList;
    private LinkedList<StationInfo> downList;

    private String upID;
    private String downID;

    public SingleLineInfo(LineInfo lineInfo) {
        this.lineInfo = lineInfo;
        upList = new LinkedList<>();
        downList = new LinkedList<>();
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public String getUpID() {
        return upID;
    }

    public void setUpID(String upID) {
        this.upID = upID;
    }

    public String getDownID() {
        return downID;
    }

    public void setDownID(String downID) {
        this.downID = downID;
    }

    public LinkedList<StationInfo> getUpList() {
        return upList;
    }

    public LinkedList<StationInfo> getDownList() {
        return downList;
    }

    public void addUpStation(StationInfo stationInfo){
        upList.add(stationInfo);
    }
    public void addDownStation(StationInfo stationInfo){
        downList.add(stationInfo);
    }

    @Override
    public String toString() {
        return "SingleLineInfo{" +
                "lineInfo=" + lineInfo +
                ", upList=" + upList +
                ", downList=" + downList +
                ", upID='" + upID + '\'' +
                ", downID='" + downID + '\'' +
                '}';
    }
}
