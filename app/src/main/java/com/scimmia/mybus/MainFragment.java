package com.scimmia.mybus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.scimmia.mybus.ad.ADFragment;
import com.scimmia.mybus.bike.RealTimeBikeFragment;
import com.scimmia.mybus.readme.ReadmeHomeFragment;
import com.scimmia.mybus.realtime.RealTimeBusFragment;
import com.scimmia.mybus.search.LineListFragment;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.Global;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.base.BaseFragment;
import com.scimmia.mybus.utils.bean.DBVersion;
import com.scimmia.mybus.utils.db.BusDBManager;
import com.scimmia.mybus.utils.db.UpdateDBTask;
import com.scimmia.mybus.utils.eventbus.event.StartBrotherEvent;
import com.scimmia.mybus.utils.http.HttpDownloadTask;
import com.scimmia.mybus.utils.http.HttpListener;
import com.scimmia.mybus.utils.http.HttpTask;
import com.squareup.otto.Subscribe;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class MainFragment extends BaseFragment {
    private SupportFragment[] mFragments = new SupportFragment[5];

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    BottomNavigationBar mBottomNavigationBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mBottomNavigationBar = (BottomNavigationBar)view.findViewById(R.id.bottom_navigation_bar);

        if (savedInstanceState == null) {
            mFragments[0] = LineListFragment.newInstance();
            mFragments[1] = RealTimeBusFragment.newInstance();
            mFragments[2] = ReadmeHomeFragment.newInstance();
//            mFragments[3] = RealTimeBikeFragment.newInstance();
//            mFragments[4] = ADFragment.newInstance();

            loadMultipleRootFragment(R.id.main_layout, 0,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2]
//                    mFragments[3],
//                    mFragments[4]
            );
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[0] = findChildFragment(LineListFragment.class);
            mFragments[1] = findChildFragment(RealTimeBusFragment.class);
            mFragments[2] = findChildFragment(ReadmeHomeFragment.class);
//            mFragments[3] = findChildFragment(RealTimeBikeFragment.class);
//            mFragments[4] = findChildFragment(ADFragment.class);
        }
        initView(view);
        return view;
    }

    private void initView(View view) {
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setActiveColor(R.color.blue);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.main_station, "线路").setInactiveIconResource(R.drawable.main_station))
                .addItem(new BottomNavigationItem(R.drawable.main_road, "实时").setInactiveIconResource(R.drawable.main_road))
                .addItem(new BottomNavigationItem(R.drawable.main_help, "帮助").setInactiveIconResource(R.drawable.main_help))
//                .addItem(new BottomNavigationItem(R.drawable.main_bike, "自行车").setInactiveIconResource(R.drawable.main_bike))
//                .addItem(new BottomNavigationItem(R.drawable.main_ad, "分享").setInactiveIconResource(R.drawable.main_ad))
                .initialise();

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                DebugLog.e(mBottomNavigationBar.getCurrentSelectedPosition()+"onTabSelected---"+position);
            }
            @Override
            public void onTabUnselected(int position) {
                DebugLog.e(mBottomNavigationBar.getCurrentSelectedPosition()+"onTabUnselected---"+position);
                showHideFragment(mFragments[mBottomNavigationBar.getCurrentSelectedPosition()], mFragments[position]);
            }
            @Override
            public void onTabReselected(int position) {
                DebugLog.e(mBottomNavigationBar.getCurrentSelectedPosition()+"onTabReselected---"+position);
            }
        });
    }


    @Subscribe
    public void onStartBrother(StartBrotherEvent event){
        if (event != null && event.targetFragment != null){
            start(event.targetFragment);
        }
    }



    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;
    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            return false;
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            showToast("再按一次退出");
            return true;
        }
    }
}
