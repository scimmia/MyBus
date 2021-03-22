package com.scimmia.mybus.realtime;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.amap.api.maps2d.CoordinateConverter;
import com.scimmia.mybus.utils.bean.BusPosition;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.base.BaseFragment;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.scimmia.mybus.utils.*;
import com.scimmia.mybus.utils.bean.FavStation;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;

import java.util.LinkedList;
public class RealTimeBusFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

    public static RealTimeBusFragment newInstance() {
        RealTimeBusFragment fragment = new RealTimeBusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private MarkerOptions markerOption;
    private AMap aMap;
    private MapView mapView;
    private Marker marker;
    private Toolbar mToolbar;

    BusDBManager manager;
    LinkedList<BusPosition> busPositions;
    String lineInfo;

    String currentDir;
    LinkedList<FavStation> currentStations;

    int busIcons[] = new int[]{R.drawable.bus,R.drawable.busa,R.drawable.busb,R.drawable.busc,R.drawable.busd};
    int currentIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_real_time_bus, container, false);
        busPositions = new LinkedList<>();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_realtime);
        mToolbar.setTitle("实时");
        mToolbar.inflateMenu(R.menu.stationmap);
        mToolbar.setOnMenuItemClickListener(this);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写

        manager = new BusDBManager(_mActivity);

        view.findViewById(R.id.gotobank).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh(GlobalData.goWork);
                    }
                }
        );
        view.findViewById(R.id.gotohome).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh(GlobalData.goHome);
                    }
                }
        );
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(37.5448665451,121.3757300377)));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            aMap.setTrafficEnabled(true);
        }
        currentIcon = 0;
        currentDir = GlobalData.goWork;
        return view;
    }
    private void refresh(String tag){
        if (aMap != null) {
            aMap.clear();
        }
        currentDir = tag;
        currentStations = manager.getFavByTag(tag);
        if (currentStations.size() == 0){
            showToast("先收藏站点吧");
            return;
        }

        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.BAIDU);
        converter.coord(new LatLng(currentStations.getFirst().getLat(),currentStations.getFirst().getLon()));
        LatLng currentStart = converter.convert();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentStart));
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.drawable.location))
                .position(currentStart)
                .draggable(false);
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();

        busPositions.clear();
        lineInfo = "";
        for (int i = currentStations.size()-1;i>=0;i--){
            final FavStation temp = currentStations.get(i);
//            new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.getBusLineStatusEncry,temp.getLineID()+'-'+temp.getAttach(),
//                    temp.getFormBody(Global.getRandom(content)), positionListener).execute();
            new HttpTask(_mActivity, GlobalData.httpMsg, Global.getBusStatusUrl(temp.getLinename(), temp.getAttach()),
                    temp.getLinename() + '-' + temp.getAttach(),
                    null, new HttpListener() {
                @Override
                public void onSuccess(String tag, String content) {
                    try {
                        LinkedList<BusPosition> t = Global.getBusPositions(content, temp.getStationID(),
                                manager.queryLineStations(temp.getLinename(),temp.getAttach()));
                        for (int i = t.size()-1;i>=0;i--){
                            addMarkersToMap(t.get(i));
                        }
                        if (t.size() > 0){
                            lineInfo = lineInfo+t.getFirst().getLineName()+"\t"+t.getFirst().getStationID()+"\n";
                        }
                        showToast(lineInfo, Toast.LENGTH_LONG);

                        busPositions.addAll(t);
                        currentIcon = (currentIcon + 1)%busIcons.length;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();
        }
    }

    private HttpListener positionListener = new HttpListener() {
        @Override
        public void onSuccess(String tag, String content) {
            try {
                LinkedList<BusPosition> t = new LinkedList<BusPosition>();
                for (int i = t.size()-1;i>=0;i--){
                    addMarkersToMap(t.get(i));
                }
                if (t.size() > 0){
                    lineInfo = lineInfo+t.getFirst().getLineName()+"\t"+t.getFirst().getStationID()+"\n";
                }
                showToast(lineInfo, Toast.LENGTH_LONG);

                busPositions.addAll(t);
                currentIcon = (currentIcon + 1)%busIcons.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void addMarkersToMap(BusPosition m) {
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(busIcons[currentIcon]))
                .position(m.getLatLng())
                .title(m.getLineName())
                .snippet(m.getStationID())
                .draggable(true);
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }

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
                currentIcon = 0;
                refresh(currentDir);
                break;
        }
        return true;
    }
}
