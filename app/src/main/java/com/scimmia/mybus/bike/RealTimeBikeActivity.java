package com.scimmia.mybus.bike;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.bean.BusPosition;
import com.scimmia.mybus.utils.bean.BusPositionParam;
import com.scimmia.mybus.utils.bean.StationInfo;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;

import java.util.LinkedList;

public class RealTimeBikeActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    protected AppCompatActivity _mActivity;

    private MarkerOptions markerOption;
    private AMap aMap;
    private MapView mapView;
    private Marker marker;
    private Toolbar mToolbar;

    LatLng mLatLng;
    BusPositionParam mBusPositionParam;
    String mTag;
    LinkedList<BusPosition> busPositions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mActivity = RealTimeBikeActivity.this;
        setContentView(R.layout.activity_real_time_station);
        busPositions = new LinkedList<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar_rtstation);
        mToolbar.setTitle("自行车");
        mToolbar.inflateMenu(R.menu.stationmap);
        mToolbar.setOnMenuItemClickListener(this);

        String lineID = getIntent().getStringExtra("lineID");
        String attach = getIntent().getStringExtra("attach");
        String temp = getIntent().getStringExtra("stationInfo");
        StationInfo stationInfo = new Gson().fromJson(temp,StationInfo.class);
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(stationInfo.getLat(),stationInfo.getLon()));
        mLatLng = converter.convert();
        mTag = lineID+'-'+attach;
        mBusPositionParam = new BusPositionParam(stationInfo.getStationID(),lineID,stationInfo.getLineStatus(),attach);

        mapView = (MapView) findViewById(R.id.rtstationmap);
        mapView.onCreate(savedInstanceState); // 此方法必须重写

        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            aMap.setTrafficEnabled(true);
        }
        getRandom();
    }

    private void getRandom(){
        new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.getRandom,GlobalData.getRandomTag, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.getBusLineStatusEncry,mTag,
                        mBusPositionParam.getFormBody(Global.getRandom(content)), positionListener).execute();
            }
        }).execute();
    }

    private HttpListener positionListener = new HttpListener() {
        @Override
        public void onSuccess(String tag, String content) {
            try {
                if (aMap != null) {
                    aMap.clear();
                }
                LinkedList<BusPosition> t = Global.getBusPositions(content,_mActivity,tag);
                markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.location))
                        .position(mLatLng)
                        .draggable(false);
                marker = aMap.addMarker(markerOption);
                marker.showInfoWindow();
                for (BusPosition m :t) {
                    addMarkersToMap(m);
                }
                busPositions.addAll(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void addMarkersToMap(BusPosition m) {
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.drawable.bus))
                .position(m.getLatLng())
                .title(m.getStationID())
//                .snippet(m.getStationID())
                .draggable(true);
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.refresh:
                getRandom();
                break;
        }
        return true;
    }
}
