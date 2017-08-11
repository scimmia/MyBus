package com.scimmia.mybus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scimmia.mybus.utils.AES;
import com.scimmia.mybus.utils.BusPositionParam;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpPostTask;
import com.scimmia.mybus.utils.http.HttpTask;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private int checkType = GlobalData.GOOFFICE;
    private MarkerOptions markerOption;
    private AMap aMap;
    private MapView mapView;
    private Marker marker;

    LinkedList<BusPosition> busPositions;

    void getRandom(){
        //"http://ytbus.jiaodong.cn:4990/BusPosition.asmx/get_random"
        new HttpTask(MainActivity.this, "loading", "get_random", "", new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                try {
                    DebugLog.e(content);
                    String randomStr = StringUtils.substringBetween(content,"china.com/\">","</string>");
                    DebugLog.e(randomStr);
                    String enString = AES.Encrypt(randomStr);
                    DebugLog.e(enString);
                    getPosition(enString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    void getPosition(String strSession){
        //"http://ytbus.jiaodong.cn:4990/BusPosition.asmx/GetBusLineStatusEncry?stationID=133&lineID=68&lineStatus=2&userRole=1&attach=1&strSession=jvquzmvOWHkLNQBA8YZmqw==&strFlag=JIAODONG&strIMEI=358108063421886?
        busPositions.clear();
        LinkedList<BusPositionParam> params = GlobalData.getParams(checkType);
        for (BusPositionParam temp :
                params) {
            new HttpPostTask(MainActivity.this,"loading...","GetBusLineStatusEncry",
                    temp.getFormBody(strSession), positionListener).execute();
        }
    }

    HttpListener positionListener = new HttpListener() {
        @Override
        public void onSuccess(String tag, String content) {
            DebugLog.e(content);
            String temp = StringUtils.substringBetween(content,"[","]");
            if (temp == null){
                temp = "[]";
            }else {
                temp = '['+temp+']';
            }
            DebugLog.e(temp);
            LinkedList<BusPosition> t = new Gson().fromJson(temp,new TypeToken<LinkedList<BusPosition>>() {
            }.getType());
            busPositions.addAll(t);
            for (BusPosition m :busPositions
                 ) {
                m.sumDistance(121.3641740318,37.5471192795);
                m.setLatLng();
                DebugLog.e(m.toString());
                addMarkersToMap(m.getLatLng());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busPositions = new LinkedList<>();
//        String cSrc = "848758143";
//        System.out.println(cSrc);
//        // 加密
//        long lStart = System.currentTimeMillis();

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        init();
    }

    private void init() {
        findViewById(R.id.gotobank).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (aMap != null) {
                            aMap.clear();
                        }
                        if (checkType != GlobalData.GOOFFICE) {
                            checkType = GlobalData.GOOFFICE;
                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(GlobalData.getHomePos()));
                        }
                        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.location))
                                .position(GlobalData.getHomePos())
                                .draggable(true);
                        marker = aMap.addMarker(markerOption);
                        marker.showInfoWindow();
                        getRandom();
                    }
                }
        );
        findViewById(R.id.gotohome).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (aMap != null) {
                            aMap.clear();
                        }
                        if (checkType != GlobalData.GOHOME) {
                            checkType = GlobalData.GOHOME;
                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(GlobalData.getOfficePos()));
                        }
                        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.location))
                                .position(GlobalData.getOfficePos())
                                .draggable(true);
                        marker = aMap.addMarker(markerOption);
                        marker.showInfoWindow();
                        getRandom();
                    }
                }
        );
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(GlobalData.getHomePos()));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            aMap.setTrafficEnabled(true);
        }
    }

    private void addMarkersToMap(LatLng latlng) {
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bus))
                .position(latlng)
                .draggable(true);
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }
}
