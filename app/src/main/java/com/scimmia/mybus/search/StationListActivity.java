package com.scimmia.mybus.search;

import android.os.Bundle;
import com.google.gson.Gson;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.base.BaseActivity;
import com.scimmia.mybus.utils.bean.FavStation;
import com.scimmia.mybus.utils.bean.LineInfo;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class StationListActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineInfo lineInfo = null;
        String temp = getIntent().getStringExtra("lineInfo");
        if (getIntent().getBooleanExtra("isfav",false)){
            lineInfo = new Gson().fromJson(temp,FavStation.class);
        }else {
            lineInfo = new Gson().fromJson(temp,LineInfo.class);
        }

        if (findFragment(StationListFragment.class) == null) {
            loadRootFragment(R.id.fl_container, StationListFragment.newInstance(lineInfo));
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

//
//    private RecyclerView mRecyclerView;
//    LinkedList<StationInfo> stationInfos;
//    StationListAdapter mAdapter;
//
//    BusDBManager manager;
//    SingleLineInfo singleLineInfo;
//
//    int currentDirection;
//    int currentPosition;
//    StationInfo currentStationInfo;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_station_list);
//        mRecyclerView = (RecyclerView) findViewById(R.id.recy);
//        manager = new BusDBManager(StationListActivity.this);
//
//        LineInfo lineInfo = new Gson().fromJson(getIntent().getExtras().getString("lineInfo"),LineInfo.class);
//        setTitle(lineInfo.getLinename());
//        singleLineInfo = manager.queryUpDownLine(lineInfo);
//        DebugLog.e(singleLineInfo.toString());
//        currentDirection = GlobalData.upDir;
//        currentPosition = -1;
//        currentStationInfo = null;
//        stationInfos = singleLineInfo.getUpList();
//        mAdapter = new StationListAdapter();
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(StationListActivity.this));
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(final BaseQuickAdapter adapter, View view, int position) {
//                if (currentStationInfo != null){
//                    currentStationInfo.setBusPositions("");
//                }
//                switch (currentDirection){
//                    case GlobalData.upDir:
//                        currentStationInfo = singleLineInfo.getUpList().get(position);
//                        break;
//                    case GlobalData.downDir:
//                        currentStationInfo = singleLineInfo.getDownList().get(position);
//                        break;
//                }
//
//                final BusPositionParam busPositionParam = new BusPositionParam(currentStationInfo.getStationID(),singleLineInfo.getLineInfo().getLineID(),""+currentDirection,singleLineInfo.getLineInfo().getAttach());
//                new HttpTask(StationListActivity.this, "loading", "get_random", "", new HttpListener() {
//                    @Override
//                    public void onSuccess(String tag, String content) {
//                        new HttpPostTask(StationListActivity.this, "loading...",
//                                singleLineInfo.getLineInfo().getLineID() + '-' + singleLineInfo.getLineInfo().getAttach(),
//                                busPositionParam.getFormBody(Global.getRandom(content)), new HttpListener() {
//                            @Override
//                            public void onSuccess(String tag, String content) {
//                                LinkedList<BusPosition> t = Global.getBusPositions(content,StationListActivity.this,tag);
//                                String result = "";
//                                for (BusPosition b :t) {
//                                    DebugLog.e(b.toString());
//                                    result += b.getCarID()+'\t'+b.getStationID()+'\n';
//                                }
//                                if (StringUtils.isEmpty(result)){
//                                    result = "暂无车辆信息";
//                                }
//                                currentStationInfo.setBusPositions(result);
//                                adapter.notifyDataSetChanged();
//                            }
//                        }).execute();
//                    }
//                }).execute();
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.stationlist, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id){
//            case R.id.change:
//                if (currentStationInfo != null){
//                    currentStationInfo.setBusPositions("");
//                }
//                currentPosition = -1;
//                currentStationInfo = null;
//                switch (currentDirection){
//                    case GlobalData.upDir:
//                        currentDirection = GlobalData.downDir;
//                        stationInfos = singleLineInfo.getDownList();
//                        break;
//                    case GlobalData.downDir:
//                        currentDirection = GlobalData.upDir;
//                        stationInfos = singleLineInfo.getUpList();
//                        break;
//                }
//                mAdapter.setNewData(stationInfos);
//                mAdapter.notifyDataSetChanged();
//                break;
//        }
//        return true;
//
//    }
//    class StationListAdapter extends BaseQuickAdapter<StationInfo, BaseViewHolder> {
//        public StationListAdapter() {
//            super(R.layout.holder_line, stationInfos);
//        }
//
//        @Override
//        protected void convert(BaseViewHolder viewHolder, StationInfo item) {
//            viewHolder.setText(R.id.tv_linename, item.getStationName())
//                .setText(R.id.tv_linestation, item.getBusPositions());
//        }
//    }
}
