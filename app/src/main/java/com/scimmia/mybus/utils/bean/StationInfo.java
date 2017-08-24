package com.scimmia.mybus.utils.bean;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by lk on 2017/8/14.
 */
public class StationInfo {
    private String buslineDetail;
    private String stationID;
    private String stationName;
    private String lineStatus;
    private double lon;
    private double lat;
    private String busPositions;

    public String getBusPositions() {
        return busPositions;
    }

    public void setBusPositions(String busPositions) {
        this.busPositions = busPositions;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public String getBuslineDetail() {
        return buslineDetail;
    }

    public void setBuslineDetail(String buslineDetail) {
        this.buslineDetail = buslineDetail;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = StringUtils.removePattern(stationName,"\\[.*\\]");
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "StationInfo{" +
                "buslineDetail='" + buslineDetail + '\'' +
                ", stationID='" + stationID + '\'' +
                ", stationName='" + stationName + '\'' +
                ", lineStatus='" + lineStatus + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
