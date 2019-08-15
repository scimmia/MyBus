package com.scimmia.mybus.bike;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.base.BaseFragment;
import com.scimmia.mybus.utils.bean.BikeStation;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;

public class RealTimeBikeFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

    public static RealTimeBikeFragment newInstance() {
        RealTimeBikeFragment fragment = new RealTimeBikeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private MarkerOptions markerOption;
    private AMap aMap;
    private MapView mapView;
    private Toolbar mToolbar;

    LinkedList<BikeStation> currentStations;

    TextView tvStation;
    WebView wBike;
    WebView wStation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_real_time_bike, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_realtime);
        mToolbar.setTitle("自行车");
        mToolbar.inflateMenu(R.menu.stationmap);
        mToolbar.setOnMenuItemClickListener(this);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写

        tvStation = view.findViewById(R.id.tv_station);
        wBike = view.findViewById(R.id.wb_bike);
        wStation = view.findViewById(R.id.wb_station);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(37.5448665451,121.3757300377)));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            aMap.setTrafficEnabled(true);
            aMap.setOnMarkerClickListener(markerClickListener);
        }
        return view;
    }
    private void refresh(){
        if (aMap != null) {
            aMap.clear();
        }
        new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.getBikeStations,GlobalData.httpMsg, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                DebugLog.e(content);
                try {
                    content = StringUtils.remove(content,"var ibike = ");
                    HashMap<String,LinkedList<BikeStation>> temp = new Gson().fromJson(content, new TypeToken<HashMap<String,LinkedList<BikeStation>>>() {}.getType());
                    currentStations = temp.get("station");
                    for (BikeStation m: currentStations){
                        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.bicycle))
                                .position(m.getLatLng())
                                .title(m.getName())
                                .snippet(""+m.getId())
                                .draggable(true);
                        aMap.addMarker(markerOption);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }
    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            DebugLog.e(marker.getSnippet());

            tvStation.setText(marker.getTitle());
            DebugLog.e(GlobalData.getAvalibalBike+marker.getSnippet());
            DebugLog.e(GlobalData.getAvalibalStation+marker.getSnippet());
            wBike.loadUrl(GlobalData.getAvalibalBike+marker.getSnippet());
            wStation.loadUrl(GlobalData.getAvalibalStation+marker.getSnippet());
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

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
                refresh();
                break;
        }
        return true;
    }
}
