package com.scimmia.mybus;

import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.model.LatLng;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by lk on 2017/8/10.
 */
public class BusPosition {
    /**
     * ID : 1870
     * 车牌 : 鲁FV8217
     * 站 : 13
     * GPSX : 121.361775
     * GPSY : 37.542967
     * 站内外 : 0
     */

    private String ID;
    private String GPSX;
    private String GPSY;
    private String distance;
    private LatLng latLng;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getGPSX() {
        return GPSX;
    }

    public void setGPSX(String GPSX) {
        this.GPSX = GPSX;
    }

    public String getGPSY() {
        return GPSY;
    }

    public void setGPSY(String GPSY) {
        this.GPSY = GPSY;
    }

    @Override
    public String toString() {
        return "BusPosition{" +
                "ID='" + ID + '\'' +
                ", GPSX='" + GPSX + '\'' +
                ", GPSY='" + GPSY + '\'' +
                ", distance='" + distance + '\'' +
                '}'+latLng.latitude+','+latLng.longitude;
    }

//    public void sumDistance(double gpsX, double gpsY){
//        double lat1 = (Math.PI/180)*(NumberUtils.toDouble(getGPSY()));
//        double lat2 = (Math.PI/180)*gpsY;
//
//        double lon1 = (Math.PI/180)*(NumberUtils.toDouble(getGPSX()));
//        double lon2 = (Math.PI/180)*gpsX;
//
//        //地球半径
//        double R = 6371;
//
//        //两点间距离 km，如果想要米的话，结果*1000就可以了
//        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
//
//        distance = d*1000 + "米";
//    }



    public void sumDistance(double gpsX, double gpsY){
        double lat1 = (Math.PI/180)*(NumberUtils.toDouble(getGPSY()));
        double lat2 = (Math.PI/180)*gpsY;
        double lon1 = (Math.PI/180)*(NumberUtils.toDouble(getGPSX()));
        double lon2 = (Math.PI/180)*gpsX;
        double a = lat1 - lat2;
        double b = lon1 - lon2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;
        s = Math.round(s * 10000) / 10000;
        distance = Math.round(s * 10000) / 10000 + "米";
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(){
        CoordinateConverter converter  = new CoordinateConverter();
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(new LatLng(NumberUtils.toDouble(getGPSY()),NumberUtils.toDouble(getGPSX())));
        // 执行转换操作
        latLng = converter.convert();
    }
}
