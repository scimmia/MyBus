package com.scimmia.mybus.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.scimmia.mybus.utils.base.BaseFragment;
import com.scimmia.mybus.utils.bean.BusPosition;
import com.scimmia.mybus.R;
import com.scimmia.mybus.realtime.RealTimeStationActivity;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.bean.*;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StationListFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

    public static StationListFragment newInstance(LineInfo lineInfo) {
        StationListFragment fragment = new StationListFragment();
        Bundle args = new Bundle();
        args.putString("lineInfo", new Gson().toJson(lineInfo));
        args.putBoolean("isfav", lineInfo instanceof FavStation);
        fragment.setArguments(args);
        return fragment;
    }

    HashSet<String> favStations;

    private RecyclerView mRecyclerView;
    LinkedList<StationInfo> stationInfos;
    StationListAdapter mAdapter;

    BusDBManager manager;
    SingleLineInfo singleLineInfo;

    String mLineName;
    String mUporDown;
    int currentDirection;
    int currentPosition;
    StationInfo currentStationInfo;
    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_stationlist);
        mToolbar.inflateMenu(R.menu.stationlist);
        mToolbar.setOnMenuItemClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recy);
        manager = new BusDBManager(_mActivity);
        favStations = new HashSet<>();

        final LineInfo lineInfo = new Gson().fromJson(getArguments().getString("lineInfo"), LineInfo.class);
        mLineName = lineInfo.getLinename();
        mUporDown = lineInfo.getAttach();
        mToolbar.setTitle(mLineName);
        singleLineInfo = manager.queryUpDownLine(lineInfo);

        DebugLog.e(singleLineInfo.toString());
        currentPosition = -1;
        currentStationInfo = null;
        if (mUporDown.equals(GlobalData.upStr)) {
            currentDirection = GlobalData.upDir;
            stationInfos = singleLineInfo.getUpList();
        } else {
            currentDirection = GlobalData.downDir;
            stationInfos = singleLineInfo.getDownList();
        }

        if (getArguments().getBoolean("isfav")) {
            FavStation temp = new Gson().fromJson(getArguments().getString("lineInfo"), FavStation.class);
            favStations.add(temp.getStationID() + '|' + temp.getLineStatus());
//            if (singleLineInfo.getDownList().getFirst().getLineStatus().equals(temp.getLineStatus())) {
//                currentDirection = GlobalData.downDir;
//                stationInfos = singleLineInfo.getDownList();
//            }
            for (int i = 0; i < stationInfos.size(); i++) {
                if (stationInfos.get(i).getStationID().equals(temp.getStationID())) {
                    currentPosition = i;
                    break;
                }
            }
        }


        setSubTitle();
        mAdapter = new StationListAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, int position) {
                if (currentStationInfo != null) {
                    currentStationInfo.setBusPositions("");
                }
                currentPosition = position;
                queryLineStatue();
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                DebugLog.e("position:" + position);
                StationInfo temp = (StationInfo) adapter.getData().get(position);
                mToolbar.setSubtitle(stationInfos.getFirst().getStationName() + "-" + singleLineInfo.getUpList().getLast().getStationName() + '|' + mUporDown);

                final FavStation fTemp = new FavStation(mLineName,mUporDown,stationInfos.getFirst().getStationName(),stationInfos.getLast().getStationName(),
                        temp.getStationID(), temp.getLineStatus(), temp.getStationName(), temp.getLat(), temp.getLon());
                final String tt = temp.getStationID() + '|' + temp.getLineStatus();
                if (favStations.contains(tt)) {
                    favStations.remove(tt);
                    manager.deleteFav(fTemp);
                } else {
                    PopupMenu popup = new PopupMenu(_mActivity, view);
                    popup.getMenuInflater().inflate(R.menu.addfav, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_addwork:
                                    fTemp.setTag(GlobalData.goWork);
                                    break;
                                case R.id.menu_addhome:
                                    fTemp.setTag(GlobalData.goHome);
                                    break;
                                case R.id.menu_addnormal:
                                    fTemp.setTag(GlobalData.goNormal);
                                    break;
                            }
                            favStations.add(tt);
                            manager.replaceFav(fTemp);
                            mAdapter.notifyItemChanged(position);
                            return true;
                        }
                    });
                    popup.show();
                }
                mAdapter.notifyItemChanged(position);
            }
        });

        if (currentPosition >= 0) {
            mRecyclerView.scrollToPosition(Math.max(0, currentPosition - 3));
            queryLineStatue();
        }
    }

    private void queryLineStatue() {
        currentStationInfo = (StationInfo) mAdapter.getData().get(currentPosition);
        new HttpTask(_mActivity, GlobalData.httpMsg, Global.getBusStatusUrl(mLineName, mUporDown),
                mLineName + '-' + mUporDown,
                null, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                try {
                    LinkedList<BusPosition> t = Global.getBusPositions(content, stationInfos.get(currentPosition).getStationID(),stationInfos);
                    String result = "";
                    for (BusPosition b : t) {
                        DebugLog.e(b.toString());
                        result += b.getCarID() + '\t' + b.getStationID() + '\n';
                    }
                    if (StringUtils.isEmpty(result)) {
                        result = "暂无车辆信息";
                    }
                    currentStationInfo.setBusPositions(result);
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();

    }

    private void setSubTitle() {
        switch (currentDirection) {
            case GlobalData.upDir:
                mToolbar.setSubtitle(singleLineInfo.getUpList().getFirst().getStationName() + "-" + singleLineInfo.getUpList().getLast().getStationName() + '|' + mUporDown);
                break;
            case GlobalData.downDir:
                mToolbar.setSubtitle(singleLineInfo.getDownList().getFirst().getStationName() + "-" + singleLineInfo.getDownList().getLast().getStationName() + '|' + mUporDown);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.change:
                if (currentStationInfo != null) {
                    currentStationInfo.setBusPositions("");
                }
                currentPosition = -1;
                currentStationInfo = null;
                switch (currentDirection) {
                    case GlobalData.upDir:
                        mUporDown = GlobalData.downStr;
                        currentDirection = GlobalData.downDir;
                        stationInfos = singleLineInfo.getDownList();
                        break;
                    case GlobalData.downDir:
                        mUporDown = GlobalData.upStr;
                        currentDirection = GlobalData.upDir;
                        stationInfos = singleLineInfo.getUpList();
                        break;
                }
                setSubTitle();
                mAdapter.setNewData(stationInfos);
                break;
            case R.id.map:
                if (currentStationInfo == null) {
                    showToast("请先选择要查看的站点");
                } else {
                    Intent intent = new Intent(_mActivity, RealTimeStationActivity.class);
                    intent.putExtra("lineID", singleLineInfo.getLineInfo().getLinename());
                    intent.putExtra("attach", singleLineInfo.getLineInfo().getAttach());
                    intent.putExtra("stationInfo", new Gson().toJson(currentStationInfo));
                    startActivity(intent);
                }
                break;
        }
        return true;
    }

    class StationListAdapter extends BaseQuickAdapter<StationInfo, BaseViewHolder> {
        StationListAdapter() {
            super(R.layout.holder_station, stationInfos);
        }

        @Override
        protected void convert(BaseViewHolder viewHolder, StationInfo item) {
            viewHolder.setText(R.id.tv_linename, item.getStationName())
                    .setText(R.id.tv_linemsg, item.getBusPositions())
                    .addOnClickListener(R.id.img_stationstar)
            ;
            if (favStations.contains(item.getStationID() + '|' + item.getLineStatus())) {
                viewHolder.setImageResource(R.id.img_stationstar, R.drawable.favor_on);
            } else {
                viewHolder.setImageResource(R.id.img_stationstar, R.drawable.favor_off);
            }
        }
    }
}
