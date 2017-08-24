package com.scimmia.mybus.utils.bean;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by lk on 2017/8/14.
 */
public class LineInfo {
    String lineID;
    String linename;
    String attach;
    String stationa;
    String stationb;

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = StringUtils.removePattern(linename,"\\[.*\\]");
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getStationa() {
        return stationa;
    }

    public void setStationa(String stationa) {
        this.stationa = StringUtils.removePattern(stationa,"\\[.*\\]");
    }

    public String getStationb() {
        return stationb;
    }

    public void setStationb(String stationb) {
        this.stationb = StringUtils.removePattern(stationb,"\\[.*\\]");
    }

    @Override
    public String toString() {
        return "LineInfo{" +
                "lineID='" + lineID + '\'' +
                ", linename='" + linename + '\'' +
                ", attach='" + attach + '\'' +
                ", stationa='" + stationa + '\'' +
                ", stationb='" + stationb + '\'' +
                '}';
    }
}
