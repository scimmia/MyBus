package com.scimmia.mybus.search;


import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SearchView;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.google.gson.Gson;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.bean.FavStation;
import com.scimmia.mybus.utils.bean.LineInfo;
import com.scimmia.mybus.utils.db.BusDBManager;
import me.yokeyword.fragmentation.SupportFragment;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LineListFragment extends SupportFragment {

    public static LineListFragment newInstance() {
        LineListFragment fragment = new LineListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    private SearchView mSearchView;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    LinkedList<LineInfo> lineInfos;
    LineListAdapter mAdapter;

    BusDBManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_linelist);

        mToolbar.setTitle("线路");
//        initToolbarMenu(mToolbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recy);

        mSearchView = (SearchView) view.findViewById(R.id.search_line);
        setupSearchView();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recy);
        manager = new BusDBManager(_mActivity);
        lineInfos = new LinkedList<>();
//        lineInfos.addAll(manager.getAllFav());
        mAdapter = new LineListAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                BusProvider.getInstance().post(new StartBrotherEvent(StationListFragment.newInstance(lineInfos.get(position))));

                Intent intent = new Intent(_mActivity,StationListActivity.class);
                intent.putExtra("isfav",lineInfos.get(position) instanceof FavStation);
                intent.putExtra("lineInfo",new Gson().toJson(lineInfos.get(position)));
                startActivity(intent);
            }
        });
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                DebugLog.e("onItemSwiped"+pos);
                if (lineInfos.get(pos) instanceof FavStation){
                    FavStation temp = (FavStation) lineInfos.get(pos);
                    manager.deleteFav(temp);
                }
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.cheese_hunt_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lineInfos.clear();
                if (StringUtils.isNotEmpty(newText)) {
                    lineInfos.addAll(manager.queryLine(newText));
                }else {
                    lineInfos.addAll(manager.getAllFav());
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StringUtils.isEmpty(mSearchView.getQuery())){
            lineInfos.clear();
            lineInfos.addAll(manager.getAllFav());
            mAdapter.notifyDataSetChanged();
        }
    }


    class LineListAdapter extends BaseItemDraggableAdapter<LineInfo, BaseViewHolder> {
        public LineListAdapter() {
            super(R.layout.holder_line, lineInfos);
        }

        @Override
        protected void convert(BaseViewHolder viewHolder, LineInfo item) {
            if (item instanceof FavStation){
                viewHolder.setText(R.id.tv_linename, item.getLinename()+'|'+item.getAttach()+'|'+((FavStation)item).getStationname())
                        .setText(R.id.tv_linestation, item.getStationa() + '-' + item.getStationb());
                String tag = ((FavStation)item).getTag();
                if (tag.equals(GlobalData.goWork)){
                    viewHolder.setImageResource(R.id.img_linstar,R.drawable.gowork);
                }else if (tag.equals(GlobalData.goHome)){
                    viewHolder.setImageResource(R.id.img_linstar,R.drawable.gohome);
                }else {
                    viewHolder.setImageResource(R.id.img_linstar,R.drawable.fav_normal);
                }
            }else {
                viewHolder.setText(R.id.tv_linename, item.getLinename()+'|'+item.getAttach())
                        .setText(R.id.tv_linestation, item.getStationa() + '-' + item.getStationb())
                        .setImageResource(R.id.img_linstar,R.drawable.fav_normal);
            }
        }
    }
}
