package com.scimmia.mybus.utils;

import com.amap.api.maps2d.model.LatLng;

import java.util.LinkedList;

/**
 * Created by lk on 2017/8/10.
 */
public class GlobalData {

    public static final int GOOFFICE = 0;
    public static final int GOHOME = 1;

    public static LinkedList<BusPositionParam> getParams(int checkType){
        LinkedList<BusPositionParam> params = new LinkedList<>();
        switch (checkType){
            case GOOFFICE:
                //gotoWork
                params.add(new BusPositionParam("133","3","2","1"));
                params.add(new BusPositionParam("133","68","2","1"));
                params.add(new BusPositionParam("133","7","1","1"));
                break;
            case GOHOME:
                //gotoWork
                params.add(new BusPositionParam("164","3","1","1"));
                params.add(new BusPositionParam("164","68","1","1"));
                params.add(new BusPositionParam("164","7","2","1"));//6
                break;
        }
        return params;
    }

    public static LatLng getHomePos(){
        return new LatLng(37.5477417343,121.3693571091);
    }

    public static LatLng getOfficePos(){
        return new LatLng(37.5429440213,121.3981211185);
    }
}
